package ua.edu.ratos.edx.security.lti;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class LTIConsumerDetailsService implements ConsumerDetailsService {
	
	private static final Log LOG = LogFactory.getLog(LTIConsumerDetailsService.class);

    private Map<String, String> localKeySecretHolder = new HashMap<>();

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
        String secret = localKeySecretHolder.get(consumerKey);
        if (secret!=null) {
            BaseConsumerDetails cd = new BaseConsumerDetails();
            cd.setConsumerName("LMS");
            cd.setConsumerKey(consumerKey);
            cd.setSignatureSecret(new SharedConsumerSecretImpl(localKeySecretHolder.get(consumerKey)));
            cd.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_LTI"));
            // no token required (0-legged)
            cd.setRequiredToObtainAuthenticatedToken(false);
            LOG.debug("LTI success: found the client secret and created basic ConsumerDetails object :: "+cd);
            return cd;
        }
        throw new OAuthException("LTI failure: no client secret matching consumer key was found in DB");
	}

	@PostConstruct
    public void init() {
	    this.localKeySecretHolder.put("ratos_consumer_key", "ratos_client_secret");
        this.localKeySecretHolder.put("ratos_consumer_key_1", "ratos_client_secret_1");
        this.localKeySecretHolder.put("ratos_consumer_key_2", "ratos_client_secret_2");
    }

}
