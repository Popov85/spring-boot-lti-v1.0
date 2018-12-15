package ua.edu.ratos.edx.security.lti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@Component
public class LTIAuthenticationHandler implements OAuthAuthenticationHandler {

    private static final String LIS_EMAIL = "lis_person_contact_email_primary";
    private static final String LIS_USER_NAME = "lis_person_name_given";
    private static final String LIS_USER_SURNAME = "lis_person_name_family";
    private static final String LIS_SOURCED = "lis_result_sourcedid";
    private static final String LIS_OUTCOME_URL = "lis_outcome_service_url";


    /**
     * Handles LTI authentication within the so-called "launch request" as per LTI specification (see also section)
     * The launch request may contain user e-mail
     *    This <b>optional</b> parameter (email) plays the role of a pre-authenticated (by LMS) user identifier,
     *    a key that can be used to retrieve information about the user from DB;
     *
     * Here are 3 situations:
     * <ul>
      *  <li>Email is included into launch request and user is found.<br>
     *       Try to find user by email in DB, if successful <b>(existing user)</b> grant him both ROLE_LMS and ROLE_STUDENT/ROLE_STAFF
     *       and let him access the RATOS starting page immediately.
     *  </li>
        <li>Email is included into launch request and user is NOT found.
            Try to find user by email in DB, if not successful <b>(first-time user)</b> grant him ROLE_LMS
            and let him try to re-authenticate via form-based authentication specially designed for LMS purposes
        </li>
        <li>Email is NOT included into launch request
            <b>(incognito user)</b> grant him ROLE_LMS
            and let him try to re-authenticate via form-based authentication specially designed for LMS purposes
        </li>
     </ul>
     * @param request
     * @param authentication
     * @param authToken
     * @return fully populated Authentication object, either with ROLE_LMS or both ROLE_LMS and ROLE_STUDENT
     * @see <a href="https://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide#toc-3">LTI v 1.1.1</a>
     */
    @Override
    public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication, OAuthAccessProviderToken authToken) {

        // Parse request for LTI-specific parameters

        // Optional, if not present, just force user to authenticate via form-based method
        String email = request.getParameter(LIS_EMAIL);

        // Optional: if present, use it as a prompt for filling the form while registration at RATOS
        String name = request.getParameter(LIS_USER_NAME);
        String surname = request.getParameter(LIS_USER_SURNAME);

        // LTI has support for the TP to call IMS Learning Information Services (LIS) when those services can be made available to the TP.
        // LTI does not require LIS services, but the TC can send LIS key information to the TP using values in the basic launch request.
        LISUser lmsUser = null;
        if (name!=null && !name.isEmpty() && surname!=null && !surname.isEmpty()) {
            lmsUser = new LISUser(name, surname);
        }

        // Optional: if not present work in service-only mode (do not persist results to LMS)
        String sourcedId = request.getParameter(LIS_SOURCED);
        String outcomeURL = request.getParameter(LIS_OUTCOME_URL);

        LTIOutcomeParams ltiOutcomeParams = null;
        if (outcomeURL!=null && !outcomeURL.isEmpty() && sourcedId!=null && !sourcedId.isEmpty()) {
            ltiOutcomeParams = new LTIOutcomeParams(sourcedId, outcomeURL);
        }
        // Remember secret that is needed for posting outcomes back to LMS
        SignatureSecret signatureSecret = authentication.getConsumerDetails().getSignatureSecret();

        // At this stage we already know that the consumer key matches the client secret
        Long lmsId = 1L;


        if (email!=null && !email.isEmpty()) {
            String user = "staff@example.com";
            if (user!=null) {
                Long userId = 1L;
                LTIUserConsumerCredentials principal = LTIUserConsumerCredentials.create(userId, lmsId, authentication.getConsumerCredentials());

                principal.setEmail(email).setUser(lmsUser).setOutcome(ltiOutcomeParams);

                Collection<GrantedAuthority> newAuthorities = AuthorityUtils.createAuthorityList("ROLE_LTI", "ROLE_STUDENT");

                Authentication auth = new UsernamePasswordAuthenticationToken(principal, signatureSecret, newAuthorities);

                log.debug("LTI user authentication successful :: {}", auth);
                return auth;
            }
        }
        // So far Oauth parameters have been valid and client key matches client secret,
        // which is enough to authenticate LMS user
        LTIToolConsumerCredentials principal = LTIToolConsumerCredentials.create(lmsId, authentication.getConsumerCredentials());

        principal.setEmail(email).setUser(lmsUser).setOutcome(ltiOutcomeParams);

        // Stick to ROLE_LTI
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, signatureSecret, authentication.getAuthorities());

        log.debug("LTI LMS authentication successful :: {}", auth);
        return auth;
    }
}
