package ua.edu.ratos.edx.security.lti;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

public class LTIAwareAccessDeniedHandler implements AccessDeniedHandler {
	
	private static final Log LOG = LogFactory.getLog(LTIAwareAccessDeniedHandler.class);
	
	private LTISecurityUtils ltiSecurityUtils;

	public LTISecurityUtils getLtiSecurityUtils() {
		return ltiSecurityUtils;
	}

	public void setLtiSecurityUtils(LTISecurityUtils ltiSecurityUtils) {
		this.ltiSecurityUtils = ltiSecurityUtils;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (ltiSecurityUtils.isLMSUserWithOnlyLTIRole(auth)) {
			LOG.debug("Detected an LTI user lacking authority trying to access protected resource.., redirected to /login");
			// Remember the request pathway
			RequestCache requestCache = new HttpSessionRequestCache();
			requestCache.saveRequest(request, response);
			
			response.sendRedirect(request.getContextPath() + "/login-custom");
			return;
		}

		LOG.debug("Detected a non-LTI user lacking authority trying to access protected resource, redirection to /access-denied endpoint");
		response.sendRedirect(request.getContextPath() + "/access-denied");
	}

}
