package ua.edu.ratos.edx.security.lti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.edu.ratos.edx.security.AuthenticatedUser;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Overrides the default behavior of UsernamePasswordAuthenticationFilter in order to pick up specific LTI information
 * wired into a corresponding Authentication object. In order to do so, we check for the presence of LMS authentication
 * and if exists, remember this authentication, clear security context, try to authenticate with UsernamePasswordAuthenticationFilter
 * and(if successful) merge the previous LTI-specific authentication to the new password based authentication. That's it.
 */
public class LTIAwareUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Log LOG = LogFactory.getLog(LTIAwareUsernamePasswordAuthenticationFilter.class);

	private final LTISecurityUtils ltiSecurityUtils;

	public LTIAwareUsernamePasswordAuthenticationFilter(LTISecurityUtils ltiSecurityUtils) {
		super();
		this.ltiSecurityUtils = ltiSecurityUtils;
	}


	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication previousAuth = SecurityContextHolder.getContext().getAuthentication();
        // Check for LMS LTI v1p0 authentication in place
        if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(previousAuth)) {
            LOG.debug("LTI authentication exists, try to authenticate with UsernamePasswordAuthenticationFilter in the usual way");
            SecurityContextHolder.clearContext();
            Authentication passwordAuthentication = null;
            try {// Attempt to authenticate with standard UsernamePasswordAuthenticationFilter
                passwordAuthentication = super.attemptAuthentication(request, response);
            } catch (AuthenticationException e) {
                // If fails by throwing an exception, catch it in unsuccessfulAuthentication() method
            	LOG.debug("Failed to upgrade authentication with UsernamePasswordAuthenticationFilter");
                SecurityContextHolder.getContext().setAuthentication(previousAuth);
                throw e;
            }
            LOG.debug("Try to merge authentication (existing LTI and new STUDENT)");
            Authentication resultingAuthentication = mergeAuthentication(previousAuth, passwordAuthentication);
            return resultingAuthentication;
        }
        LOG.debug("No LTI authentication exists, try to authenticate with UsernamePasswordAuthenticationFilter in the usual way");
        return super.attemptAuthentication(request, response);
    }


    private Authentication mergeAuthentication(Authentication previousAuth, Authentication passwordAuth) {
        AuthenticatedUser passwordPrincipal = (AuthenticatedUser) passwordAuth.getPrincipal();
        Long userId = passwordPrincipal.getUserId();
        String email = passwordPrincipal.getUsername();
        Collection<GrantedAuthority> passwordAuthorities = passwordPrincipal.getAuthorities();

        LTIToolConsumerCredentials previousPrincipal = (LTIToolConsumerCredentials) previousAuth.getPrincipal();
        LTIUserConsumerCredentials resultingPrincipal =
                LTIUserConsumerCredentials.create(userId, previousPrincipal.getLmsId(), previousPrincipal);
        resultingPrincipal.setOutcome(previousPrincipal.getOutcome().orElse(null));
        resultingPrincipal.setUser(previousPrincipal.getUser().orElse(null));
        resultingPrincipal.setEmail(email);

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(passwordAuthorities);
        updatedAuthorities.addAll(previousAuth.getAuthorities());

        Authentication resultingAuthentication = new UsernamePasswordAuthenticationToken(
                resultingPrincipal, previousAuth.getCredentials(), Collections.unmodifiableList(updatedAuthorities));
        LOG.debug("Merged authentication (LTI+new STUDENT) for user ID :: "+ userId);
        return resultingAuthentication;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        Authentication previousAuth = SecurityContextHolder.getContext().getAuthentication();
        if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(previousAuth)) {
        	LOG.debug("Unsuccessful authentication upgrade for LTI user, fallback to previous authentication");
            super.unsuccessfulAuthentication(request, response, failed);
            SecurityContextHolder.getContext().setAuthentication(previousAuth);
        } else {
        	LOG.debug("Unsuccessful authentication for non-LTI user with UsernamePasswordAuthenticationFilter");
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }
}
