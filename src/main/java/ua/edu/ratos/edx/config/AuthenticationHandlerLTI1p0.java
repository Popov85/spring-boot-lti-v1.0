package ua.edu.ratos.edx.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.signature.SignatureSecret;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Component
public class AuthenticationHandlerLTI1p0 implements OAuthAuthenticationHandler {

    private static final String LIS_EMAIL = "lis_person_contact_email_primary";
    private static final String LIS_USER_NAME = "lis_person_name_given";
    private static final String LIS_USER_SURNAME = "lis_person_name_family";
    private static final String LIS_SOURCED = "lis_result_sourcedid";
    private static final String LIS_OUTCOME_URL = "lis_outcome_service_url";

    @Override
    public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication, OAuthAccessProviderToken authToken) {

        System.out.println("LTI filter");

        String email = request.getParameter(LIS_EMAIL);

        String name = request.getParameter(LIS_USER_NAME);
        String surname = request.getParameter(LIS_USER_SURNAME);

        ToolConsumerUserLTI1p0 userDetails = null;
        if (name!=null && !name.isEmpty() && surname!=null && !surname.isEmpty()) {
            userDetails = new ToolConsumerUserLTI1p0(name, surname);
        }

        String sourcedId = request.getParameter(LIS_SOURCED);
        String outcomeURL = request.getParameter(LIS_OUTCOME_URL);

        ToolConsumerOutcomeLTI1p0 outcome = null;
        if (outcomeURL!=null && !outcomeURL.isEmpty() && sourcedId!=null && !sourcedId.isEmpty()) {
            outcome = new ToolConsumerOutcomeLTI1p0(sourcedId, outcomeURL);
        }

        Collection<GrantedAuthority> authorities = authentication.getAuthorities();
        SignatureSecret signatureSecret = authentication.getConsumerDetails().getSignatureSecret();
        // authentication.getConsumerCredentials().getConsumerKey();
        // TODO : go to DB and find LMS by its credentials
        // at this stage we already know that the key matches the secret
        Long lmsId = 1L;


        if (email!=null && !email.isEmpty()) {
            // try to find userId by email
            //User user = userRepository.findByEmail(email);
            if ("staff@example.com".equals(email)) {
                // if successful - create authentication based on AuthenticatedToolProviderUser
                Long userId = 1L;//user.getUserId();

                AuthenticatedToolProviderUserLTI1p0 principal = new AuthenticatedToolProviderUserLTI1p0(userId, lmsId,
                        authentication.getConsumerCredentials().getConsumerKey(),
                        authentication.getConsumerCredentials().getSignature(),
                        authentication.getConsumerCredentials().getSignatureMethod(),
                        authentication.getConsumerCredentials().getSignatureBaseString(),
                        authentication.getConsumerCredentials().getToken()
                );
                principal
                        .setEmail(email)
                        .setUser(userDetails)
                        .setOutcome(outcome);

                authorities.add(new SimpleGrantedAuthority("ROLE_STUDENT"));

                Authentication auth = new UsernamePasswordAuthenticationToken
                        (principal, signatureSecret, authorities);

                return auth;
            }
        }

        AuthenticatedToolConsumerLTI1p0 principal = new AuthenticatedToolConsumerLTI1p0(lmsId,
                authentication.getConsumerCredentials().getConsumerKey(),
                authentication.getConsumerCredentials().getSignature(),
                authentication.getConsumerCredentials().getSignatureMethod(),
                authentication.getConsumerCredentials().getSignatureBaseString(),
                authentication.getConsumerCredentials().getToken());

        principal
                .setEmail(email)
                .setUser(userDetails)
                .setOutcome(outcome);

        Authentication auth = new UsernamePasswordAuthenticationToken(principal, signatureSecret, authorities);
        return auth;
    }
}
