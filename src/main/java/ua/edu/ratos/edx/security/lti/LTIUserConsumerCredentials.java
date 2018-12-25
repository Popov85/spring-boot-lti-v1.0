package ua.edu.ratos.edx.security.lti;

import org.springframework.security.oauth.provider.ConsumerCredentials;

public class LTIUserConsumerCredentials extends LTIToolConsumerCredentials {
  
	private static final long serialVersionUID = 1L;
	
	/**
     * Unique identifier of a user within TP
     */
    private final Long userId;

    protected LTIUserConsumerCredentials(Long userId, Long lmsId, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
        super(lmsId, consumerKey, signature, signatureMethod, signatureBaseString, token);
        this.userId = userId;
    }

    public static LTIUserConsumerCredentials create(final Long userId, final Long lmsId, final ConsumerCredentials consumerCredentials) {
        return new LTIUserConsumerCredentials(
                userId, lmsId,
                consumerCredentials.getConsumerKey(),
                consumerCredentials.getSignature(),
                consumerCredentials.getSignatureMethod(),
                consumerCredentials.getSignatureBaseString(),
                consumerCredentials.getToken());
    }

    public Long getUserId() {
        return userId;
    }
    

    @Override
    public String toString() {
        return "LTIUserConsumerCredentials{" +
                "userId=" + userId +
                '}';
    }
}
