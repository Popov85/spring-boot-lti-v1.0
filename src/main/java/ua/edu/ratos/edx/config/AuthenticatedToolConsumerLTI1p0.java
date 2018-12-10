package ua.edu.ratos.edx.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.oauth.provider.ConsumerCredentials;

import java.security.Principal;
import java.util.Optional;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AuthenticatedToolConsumerLTI1p0 extends ConsumerCredentials implements Principal {

    /**
     * Unique identifier of a recognized TC (LMS) based on the pair key-secret;
     */
    private final Long lmsId;

    public AuthenticatedToolConsumerLTI1p0(Long lmsId, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
        super(consumerKey, signature, signatureMethod, signatureBaseString, token);
        this.lmsId = lmsId;
    }

    /**
     * Recommended parameter to include to a launch request to smoothly recognize a learner;
     * As per LTI v 1.1.1 specification the request parameter is called: "lis_person_contact_email_primary"
     * @see <a href="http://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide">LTI v 1.1.1</a>
     */
    private String email;

    /**
     * Most often TC (LMS) do not send these parameters for safety reasons
     */
    private ToolConsumerUserLTI1p0 user;

    /**
     * Must be present if TC (LMS) wants TP to send outcome grades back;
     */
    private ToolConsumerOutcomeLTI1p0 outcome;


    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    public Optional<ToolConsumerUserLTI1p0> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<ToolConsumerOutcomeLTI1p0> getOutcome() {
        return Optional.ofNullable(outcome);
    }

    @Override
    public String getName() {
        return lmsId.toString();
    }


}
