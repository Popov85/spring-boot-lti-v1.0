package ua.edu.ratos.edx.security.lti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class LTISecurityUtils {

	private static final Log LOG = LogFactory.getLog(LTISecurityUtils.class);

	public boolean isLMSUserWithOnlyLTIRole(Authentication auth) {
		LOG.debug("Challenge whether it is an LMS user with only LTI role?");
		if (auth == null)
			return false;
		if (!auth.getPrincipal().getClass().equals(LTIToolConsumerCredentials.class))
			return false;
		if (auth.getAuthorities().size() != 1)
			return false;
		if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LTI")))
			return false;
		return true;
	}
	
	public boolean isLMSUserWithLTIAndSTUDENTRoles(Authentication auth) {
		LOG.debug("Challenge whether it is an LMS user with both LTI & STUDENT roles?");
		if (auth == null)
			return false;
		if (!auth.getPrincipal().getClass().equals(LTIUserConsumerCredentials.class))
			return false;
		if (auth.getAuthorities().size() != 2)
			return false;
		if (!auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_LTI")) 
				|| !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_STUDENT")))
			return false;
		return true;
		
	}

}
