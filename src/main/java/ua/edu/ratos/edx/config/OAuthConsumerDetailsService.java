package ua.edu.ratos.edx.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

@Component
public class OAuthConsumerDetailsService implements ConsumerDetailsService {

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
		BaseConsumerDetails cd = new BaseConsumerDetails();
        cd.setConsumerKey(consumerKey);
        cd.setSignatureSecret(new SharedConsumerSecretImpl("ratos_client_secret"));
        cd.setConsumerName("Sample");
        cd.setRequiredToObtainAuthenticatedToken(false); // no token required (0-legged)
        cd.getAuthorities().add(new SimpleGrantedAuthority("ROLE_OAUTH")); 
		return cd;
	}

}
