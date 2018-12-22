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
 * and(if successful) add the previous LTI-specific authentication to the new authentication. That's it.
 */
public class LTIAwareUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	private static final Log LOG = LogFactory.getLog(LTIAwareUsernamePasswordAuthenticationFilter.class);

	private LTISecurityUtils ltiSecurityUtils;

	public LTISecurityUtils getLtiSecurityUtils() {
		return ltiSecurityUtils;
	}

	public void setLtiSecurityUtils(LTISecurityUtils ltiSecurityUtils) {
		this.ltiSecurityUtils = ltiSecurityUtils;
	}

	@Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication previousAuth = SecurityContextHolder.getContext().getAuthentication();
        // Check for LMS LTI v1p0 authentication in place
        if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(previousAuth)) {
            LOG.debug("LTI authentication exists, try to authenticate with UsernamePasswordAuthenticationFilter in the usual way");
            SecurityContextHolder.clearContext();
            Authentication authentication = null;
            try {// Attempt to authenticate with standard UsernamePasswordAuthenticationFilter
                authentication = super.attemptAuthentication(request, response);
            } catch (AuthenticationException e) {
                // If fails by throwing an exception, catch it in unsuccessfulAuthentication() method
            	LOG.debug("Failed to upgrade authentication with UsernamePasswordAuthenticationFilter");
                SecurityContextHolder.getContext().setAuthentication(previousAuth);
                throw e;
            }
            Authentication newAuth = mergeAuthentication(previousAuth, authentication);
            return newAuth;
        }
        LOG.debug("No LTI authentication exists, try to authenticate with UsernamePasswordAuthenticationFilter in the usual way");
        return super.attemptAuthentication(request, response);
    }

    private Authentication mergeAuthentication(Authentication previousAuth, Authentication newAuthentication) {
        LOG.debug("Obtained a valid authentication with UsernamePasswordAuthenticationFilter");
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) newAuthentication.getPrincipal();
        Long userId = authenticatedUser.getUserId();
        String email = authenticatedUser.getUsername();
        Collection<GrantedAuthority> newAuthorities = authenticatedUser.getAuthorities();

        LTIToolConsumerCredentials ltiToolConsumerCredentials = (LTIToolConsumerCredentials) previousAuth.getPrincipal();
        LTIUserConsumerCredentials ltiUserConsumerCredentials =
                LTIUserConsumerCredentials.create(userId, ltiToolConsumerCredentials.getLmsId(), ltiToolConsumerCredentials);
        ltiUserConsumerCredentials.setOutcome(ltiToolConsumerCredentials.getOutcome().orElse(null));
        ltiUserConsumerCredentials.setUser(ltiToolConsumerCredentials.getUser().orElse(null));
        ltiUserConsumerCredentials.setEmail(email);

        List<GrantedAuthority> updatedAuthorities = new ArrayList<>(newAuthorities);
        updatedAuthorities.addAll(previousAuth.getAuthorities());

        Authentication resultingAuthentication = new UsernamePasswordAuthenticationToken(
                ltiUserConsumerCredentials, previousAuth.getCredentials(), Collections.unmodifiableList(updatedAuthorities));
        LOG.debug("Created an updated authentication for user ID :: "+ userId);
        return resultingAuthentication;
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
            throws IOException, ServletException {
        Authentication previousAuth = SecurityContextHolder.getContext().getAuthentication();
        if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(previousAuth)) {
        	LOG.debug("unsuccessfulAuthentication upgrade for LTI user, previous authentication :: "+ previousAuth);
            super.unsuccessfulAuthentication(request, response, failed);
            LOG.debug("Unsuccessful authentication upgrade for LTI user, fallback to previous authentication");
            SecurityContextHolder.getContext().setAuthentication(previousAuth);
        } else {
        	LOG.debug("unsuccessfulAuthentication for non-LTI user with UsernamePasswordAuthenticationFilter");
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }
}
