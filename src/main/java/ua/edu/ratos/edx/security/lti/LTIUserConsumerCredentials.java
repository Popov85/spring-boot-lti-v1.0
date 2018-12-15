package ua.edu.ratos.edx.security.lti;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.security.oauth.provider.ConsumerCredentials;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class LTIUserConsumerCredentials extends LTIToolConsumerCredentials {
    /**
     * Unique identifier of a user within TP
     */
    private final Long userId;

    protected LTIUserConsumerCredentials(Long userId, Long lmsId, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
        super(lmsId, consumerKey, signature, signatureMethod, signatureBaseString, token);
        this.userId = userId;
    }

    public static LTIUserConsumerCredentials create(@NonNull final Long userId, @NonNull final Long lmsId, @NonNull final ConsumerCredentials consumerCredentials) {
        return new LTIUserConsumerCredentials(
                userId, lmsId,
                consumerCredentials.getConsumerKey(),
                consumerCredentials.getSignature(),
                consumerCredentials.getSignatureMethod(),
                consumerCredentials.getSignatureBaseString(),
                consumerCredentials.getToken());
    }
}
