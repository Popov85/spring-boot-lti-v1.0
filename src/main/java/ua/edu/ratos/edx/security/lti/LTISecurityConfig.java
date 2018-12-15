package ua.edu.ratos.edx.security.lti;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
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
import java.util.List;

@Order(1)
@EnableWebSecurity
public class LTISecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment environment;


    @Autowired
    private LTIConsumerDetailsService ltiConsumerDetailsService;

    @Autowired
    private LTIAuthenticationHandler ltiAuthenticationHandler;

    // CORS bean
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins=Arrays.asList("*");
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/lti/**/launch", configuration);
        return source;
    }

    // OAuth/LTI 1.0 beans
    @Bean
    public OAuthProviderTokenServices oauthProviderTokenServices() {
        return new InMemoryProviderTokenServices();
    }

    @Bean
    public ProtectedResourceProcessingFilter ltiProtectedResourceProcessingFilter() {
        ProtectedResourceProcessingFilter protectedResourceProcessingFilter =
                new LTIProtectedResourceProcessingFilter();
        protectedResourceProcessingFilter.setConsumerDetailsService(ltiConsumerDetailsService);
        protectedResourceProcessingFilter.setAuthHandler(ltiAuthenticationHandler);
        protectedResourceProcessingFilter.setTokenServices(oauthProviderTokenServices());
        protectedResourceProcessingFilter.setAuthenticationEntryPoint(new OAuthProcessingFilterEntryPoint());
        protectedResourceProcessingFilter.setNonceServices(new InMemoryNonceServices());
        // Fails if OAuth params are not included
        protectedResourceProcessingFilter.setIgnoreMissingCredentials(false);
        return protectedResourceProcessingFilter;
    }

    @Bean
    public FilterRegistrationBean ltiProtectedResourceProcessingFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(ltiProtectedResourceProcessingFilter());
        registration.addUrlPatterns("/lti/1p0/launch");
        registration.setName("ltiProtectedResourceProcessingFilter");
        registration.setOrder(1);
        registration.setEnabled(false);
        return registration;
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .antMatcher("/lti/1p0/launch")
            .addFilterBefore(ltiProtectedResourceProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .anyRequest().hasRole("LTI")
            .and()
            .headers()
                .frameOptions().disable();
    }

}
