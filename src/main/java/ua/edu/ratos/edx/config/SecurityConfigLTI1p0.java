package ua.edu.ratos.edx.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth.provider.OAuthProcessingFilterEntryPoint;
import org.springframework.security.oauth.provider.filter.ProtectedResourceProcessingFilter;
import org.springframework.security.oauth.provider.nonce.InMemoryNonceServices;
import org.springframework.security.oauth.provider.token.InMemoryProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Order(1)
@EnableWebSecurity
public class SecurityConfigLTI1p0 extends WebSecurityConfigurerAdapter {

    @Autowired
    private ConsumerDetailsServiceLTI1p0 ConsumerDetailsServiceLTI1p0;

    @Autowired
    private AuthenticationHandlerLTI1p0 AuthenticationHandlerLTI1p0;

    @Bean
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        // NOTE: we don't use the OAuthProviderTokenServices for 0-legged
        // but it cannot be null
        return new InMemoryProviderTokenServices();
    }

    @Bean
    public ProtectedResourceProcessingFilter lti1p0ProviderProcessingFilter() {
        ProtectedResourceProcessingFilter protectedResourceProcessingFilter =
                new ProtectedResourceProcessingFilter();
        protectedResourceProcessingFilter.setConsumerDetailsService(ConsumerDetailsServiceLTI1p0);
        protectedResourceProcessingFilter.setAuthHandler(AuthenticationHandlerLTI1p0);
        protectedResourceProcessingFilter.setTokenServices(oauthProviderTokenServices());
        protectedResourceProcessingFilter.setAuthenticationEntryPoint(new OAuthProcessingFilterEntryPoint());
        protectedResourceProcessingFilter.setNonceServices(new InMemoryNonceServices());
        // Fails if OAuth params are not included
        protectedResourceProcessingFilter.setIgnoreMissingCredentials(true);
        return protectedResourceProcessingFilter;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/lti1p0/launch", configuration);
        return source;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .addFilterBefore(lti1p0ProviderProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .antMatcher("/lti1p0/**")
            .authorizeRequests()
            .anyRequest().hasRole("OAUTH")
            .and()
            .headers()
                .frameOptions().disable();
    }
}
