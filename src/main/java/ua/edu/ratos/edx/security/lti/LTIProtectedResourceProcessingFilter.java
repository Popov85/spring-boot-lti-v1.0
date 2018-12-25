package ua.edu.ratos.edx.security.lti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;

public class LTIProtectedResourceProcessingFilter extends ProtectedResourceProcessingFilter {
	
	private static final Log LOG = LogFactory.getLog(LTIProtectedResourceProcessingFilter.class);

	
	private final LTISecurityUtils ltiSecurityUtils;
	
    public LTIProtectedResourceProcessingFilter(LTISecurityUtils ltiSecurityUtils) {
		super();
		this.ltiSecurityUtils = ltiSecurityUtils;
	}


	/**
     * Default behavior is to reset the Authentication to the previous state that was before doing this filter.
     * So if previous authentication was null, it will be again null after this filter has done its job.
     * Now we need to override this, by resetting back to previous only in case of failed attempt to authenticate with OAuth
     * Otherwise let this new authentication to remain and create authentication session
     * with ROLE_LTI (or ROLE_STUDENT as well if e-mail parameter has proven the user identity)
     * Also, in case we already had an authenticated full-fledged user (LTI+STUDENT) before the launch, 
     * do not lose his authentication, 
     * but rather merge the new OAuth launch credentials with the existing STUDENT role.
     * @param previousAuthentication
     */
    @Override
    protected void resetPreviousAuthentication(Authentication previousAuthentication) {
    	// Get newly created OAuth authentication during launch request
        Authentication oauthAuthentication = SecurityContextHolder.getContext().getAuthentication();
        // If OAuth authentication failed and no authentication now is present in the context, reset to previous one
		if (oauthAuthentication == null || oauthAuthentication.getPrincipal()==null ) {
            super.resetPreviousAuthentication(previousAuthentication);
            return;
        }
        // If OAuth authentication just provided only LTI role (but failed to provide STUDENT role) 
        // and previous authentication was LTI & STUDENT, merge them and set to the context
        if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(oauthAuthentication) 
        		&& ltiSecurityUtils.isLMSUserWithLTIAndSTUDENTRoles(previousAuthentication)) {
            LOG.debug("Try to merge authentication (existing STUDENT and new LTI)");
        	Authentication resultingAuthentication = mergeAuthentication(previousAuthentication, oauthAuthentication);
            super.resetPreviousAuthentication(resultingAuthentication);
            return;
        }
    }


	private Authentication mergeAuthentication(Authentication previousAuthentication, Authentication oauthAuthentication) {
		LTIToolConsumerCredentials updatedPrincipal = (LTIToolConsumerCredentials)oauthAuthentication.getPrincipal();
		LTIUserConsumerCredentials previousPrincipal = (LTIUserConsumerCredentials) previousAuthentication.getPrincipal();   	
		
		LTIUserConsumerCredentials resultingPrincipal = LTIUserConsumerCredentials
				.create(previousPrincipal.getUserId(), updatedPrincipal.getLmsId(), updatedPrincipal.getConsumerCredentials());
		resultingPrincipal.setEmail(previousPrincipal.getEmail().orElse(null));
		resultingPrincipal.setUser(updatedPrincipal.getUser().orElse(null));
		resultingPrincipal.setOutcome(updatedPrincipal.getOutcome().orElse(null));
		
		SignatureSecret updatedSignatureSecret = (SignatureSecret) oauthAuthentication.getCredentials();
		
		// We have an updated principal and updated signatureSecret
		
		Authentication resultingAuthentication = new UsernamePasswordAuthenticationToken(resultingPrincipal, updatedSignatureSecret, previousAuthentication.getAuthorities());
        LOG.debug("Merged authentication (Password+new LTI) for user ID :: "+ previousPrincipal.getUserId());
		return resultingAuthentication;
	}
}
