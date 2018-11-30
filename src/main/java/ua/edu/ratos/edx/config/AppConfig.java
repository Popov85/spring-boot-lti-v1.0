package ua.edu.ratos.edx.config;


import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth.provider.OAuthAuthenticationHandler;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.nonce.InMemoryNonceServices;
import org.springframework.security.oauth.provider.nonce.OAuthNonceServices;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode;


@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter {
	
	   private ZeroLeggedOAuthProviderProcessingFilter zeroLeggedOAuthProviderProcessingFilter; // OK
	   
       @Autowired
       private OAuthConsumerDetailsService oauthConsumerDetailsService; // OK
       
       @Autowired
       private OAuthAuthenticationHandler oauthAuthenticationHandler; // OK
       
       @Autowired
       private OAuthProcessingFilterEntryPoint oauthProcessingFilterEntryPoint; // OK
       
       @Autowired
       private OAuthProviderTokenServices oauthProviderTokenServices; // OK
       
       @PostConstruct
       public void init() {
           zeroLeggedOAuthProviderProcessingFilter = new ZeroLeggedOAuthProviderProcessingFilter(oauthConsumerDetailsService, new InMemoryNonceServices(), oauthProcessingFilterEntryPoint, oauthAuthenticationHandler, oauthProviderTokenServices);
       }
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {       
        http.csrf().disable()
        .antMatcher("/oauth/**")
        .addFilterBefore(zeroLeggedOAuthProviderProcessingFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeRequests().anyRequest().hasRole("OAUTH")
        .and()
        .headers().frameOptions().disable();
        	//.addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
    }
    
    // OAuth beans

    @Bean(name = "oauthAuthenticationEntryPoint")
    public OAuthProcessingFilterEntryPoint oauthAuthenticationEntryPoint() {
        return new OAuthProcessingFilterEntryPoint();
    }

    @Bean(name = "oauthProviderTokenServices")
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        // NOTE: we don't use the OAuthProviderTokenServices for 0-legged but it cannot be null
        return new InMemoryProviderTokenServices();
    }
    
	public static class ZeroLeggedOAuthProviderProcessingFilter extends ProtectedResourceProcessingFilter {
		
		ZeroLeggedOAuthProviderProcessingFilter(OAuthConsumerDetailsService oAuthConsumerDetailsService,
				OAuthNonceServices oAuthNonceServices, OAuthProcessingFilterEntryPoint oAuthProcessingFilterEntryPoint,
				OAuthAuthenticationHandler oAuthAuthenticationHandler,
				OAuthProviderTokenServices oAuthProviderTokenServices) {
			super();
			setAuthenticationEntryPoint(oAuthProcessingFilterEntryPoint);
			setAuthHandler(oAuthAuthenticationHandler);
			setConsumerDetailsService(oAuthConsumerDetailsService);
			setNonceServices(oAuthNonceServices);
			setTokenServices(oAuthProviderTokenServices);
			setIgnoreMissingCredentials(false); // die if OAuth params are not included
			
			System.out.println("ZeroLeggedOAuthProviderProcessingFilter invoked...");
		}
    }
	
}
