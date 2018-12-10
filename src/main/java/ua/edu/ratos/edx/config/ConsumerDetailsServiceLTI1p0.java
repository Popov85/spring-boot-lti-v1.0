package ua.edu.ratos.edx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.common.signature.SharedConsumerSecretImpl;
import org.springframework.security.oauth.provider.BaseConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.stereotype.Component;

@Component
public class ConsumerDetailsServiceLTI1p0 implements ConsumerDetailsService {

    @Autowired
    private Environment environment;

	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String consumerKey) throws OAuthException {
	    // TODO : work here to find what secret is associated with this key
        // Query LMSCredentials table for that
		BaseConsumerDetails cd = new BaseConsumerDetails();
        cd.setConsumerKey(consumerKey);
        cd.setSignatureSecret(new SharedConsumerSecretImpl(this.environment.getProperty("ratos.lti.secret")));
        cd.setConsumerName("Sample");
        cd.setRequiredToObtainAuthenticatedToken(false); // no token required (0-legged)
        cd.getAuthorities().add(new SimpleGrantedAuthority("ROLE_OAUTH"));
		return cd;
	}

}
