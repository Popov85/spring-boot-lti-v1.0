package ua.edu.ratos.edx.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AuthenticatedToolProviderUserLTI1p0 extends AuthenticatedToolConsumerLTI1p0 {
    /**
     * Unique identifier of a user within TP
     */
    private final Long userId;

    public AuthenticatedToolProviderUserLTI1p0(Long userId, Long lmsId, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
        super(lmsId, consumerKey, signature, signatureMethod, signatureBaseString, token);
        this.userId = userId;
    }
}
