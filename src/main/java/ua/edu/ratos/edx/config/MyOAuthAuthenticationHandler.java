package ua.edu.ratos.edx.config;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth.provider.ConsumerAuthentication;
import org.springframework.security.oauth.provider.ConsumerCredentials;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.stereotype.Component;

@Component
public class MyOAuthAuthenticationHandler implements OAuthAuthenticationHandler {

	@Override
	public Authentication createAuthentication(HttpServletRequest request, ConsumerAuthentication authentication,
			OAuthAccessProviderToken authToken) {
		
        Collection<GrantedAuthority> authorities = new HashSet<>(authentication.getAuthorities());

		 Principal principal = new NamedOAuthPrincipal("Test user", authorities,
	                authentication.getConsumerCredentials().getConsumerKey(),
	                authentication.getConsumerCredentials().getSignature(),
	                authentication.getConsumerCredentials().getSignatureMethod(),
	                authentication.getConsumerCredentials().getSignatureBaseString(),
	                authentication.getConsumerCredentials().getToken()
	        );
		
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        System.out.println("MyOAuthAuthenticationHandler invoked...");
		return auth;
	}
	
	public static class NamedOAuthPrincipal extends ConsumerCredentials implements Principal {
        public String name;
        public Collection<GrantedAuthority> authorities;

        public NamedOAuthPrincipal(String name, Collection<GrantedAuthority> authorities, String consumerKey, String signature, String signatureMethod, String signatureBaseString, String token) {
            super(consumerKey, signature, signatureMethod, signatureBaseString, token);
            this.name = name;
            this.authorities = authorities;
        }

        @Override
        public String getName() {
            return name;
        }

        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
    }

}
