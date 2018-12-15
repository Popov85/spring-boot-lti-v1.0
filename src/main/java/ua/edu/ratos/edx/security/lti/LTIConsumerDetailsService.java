package ua.edu.ratos.edx.security.lti;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j
@Component
public class LTIConsumerDetailsService implements ConsumerDetailsService {

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
            log.debug("LTI success: found the client secret and created basic ConsumerDetails object, {}", cd);
            return cd;
        }
        throw new OAuthException("LTI failure: no client secret matching consumer key was found in DB");
	}

	@PostConstruct
    public void init() {
	    this.localKeySecretHolder.put("edx_ratos_key", "edx_ratos_secret");
        this.localKeySecretHolder.put("edx_ratos_key_1", "edx_ratos_secret_1");
        this.localKeySecretHolder.put("edx_ratos_key_2", "edx_ratos_secret_2");
    }

}
