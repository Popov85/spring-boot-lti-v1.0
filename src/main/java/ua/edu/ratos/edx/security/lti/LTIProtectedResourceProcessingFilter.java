package ua.edu.ratos.edx.security.lti;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;

public class LTIProtectedResourceProcessingFilter extends ProtectedResourceProcessingFilter {

    /**
     * Default behaviour is to reset the Authentication to the previous state that was before doing this filter.
     * So if previous authentication was null, it will be again null after this filter has done its job.
     * Now we need to override this, by not letting null if it was null before.
     * Otherwise let this new authentication to remain and create authentication session
     * with ROLE_LTI (or ROLE_STUDENT as well if e-mail parameter has proven the user identity)
     * @param previousAuthentication
     */
    @Override
    protected void resetPreviousAuthentication(Authentication previousAuthentication) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal()==null ) {
            super.resetPreviousAuthentication(previousAuthentication);
        }
    }
}
