package ua.edu.ratos.edx.security.lti;


import org.springframework.security.oauth.provider.ConsumerCredentials;
import java.security.Principal;
import java.util.Optional;

public class LTIToolConsumerCredentials extends ConsumerCredentials implements Principal {

	private static final long serialVersionUID = 1L;

	/**
     * Unique identifier of a recognized TC (LMS) based on the pair key-secret;
     */
    private final Long lmsId;

    protected LTIToolConsumerCredentials(Long lmsId, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
        super(consumerKey, signature, signatureMethod, signatureBaseString, token);
        this.lmsId = lmsId;
    }

    public static LTIToolConsumerCredentials create(final Long lmsId, final ConsumerCredentials consumerCredentials) {
        return new LTIToolConsumerCredentials(lmsId,
                consumerCredentials.getConsumerKey(),
                consumerCredentials.getSignature(),
                consumerCredentials.getSignatureMethod(),
                consumerCredentials.getSignatureBaseString(),
                consumerCredentials.getToken());
    }

    private String email;

    private LISUser user;

    private LTIOutcomeParams outcome;

    public LTIToolConsumerCredentials setEmail(String email) {
        this.email = email;
        return this;
    }

    public LTIToolConsumerCredentials setUser(LISUser user) {
        this.user = user;
        return this;
    }

    public LTIToolConsumerCredentials setOutcome(LTIOutcomeParams outcome) {
        this.outcome = outcome;
        return this;
    }

    /**
     * Recommended parameter to include to a launch request to smoothly recognize a learner;
     * As per LTI v 1.1.1 specification the request parameter is called: "lis_person_contact_email_primary"
     * @see <a href="http://www.imsglobal.org/specs/ltiv1p1p1/implementation-guide">LTI v 1.1.1</a>
     */
    public Optional<String> getEmail() {
        return Optional.ofNullable(email);
    }

    /**
     * Most often TC (LMS) do not send these parameters for safety reasons
     */
    public Optional<LISUser> getUser() {
        return Optional.ofNullable(user);
    }

    /**
     * Must be present if TC (LMS) wants TP to send outcome grades back;
     */
    public Optional<LTIOutcomeParams> getOutcome() {
        return Optional.ofNullable(outcome);
    }

    public Long getLmsId() {
        return lmsId;
    }

    @Override
    public String getName() {
        return lmsId.toString();
    }

    @Override
    public String toString() {
        return "LTIToolConsumerCredentials{" +
                "lmsId=" + lmsId +
                ", email='" + email + '\'' +
                ", user=" + user +
                '}';
    }
}
