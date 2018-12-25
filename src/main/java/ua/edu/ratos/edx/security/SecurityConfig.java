package ua.edu.ratos.edx.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.edu.ratos.edx.security.lti.LTIAwareAccessDeniedHandler;
import ua.edu.ratos.edx.security.lti.LTIAwareUsernamePasswordAuthenticationFilter;
import ua.edu.ratos.edx.security.lti.LTISecurityUtils;

@Order(2)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // LTI Pre- bean
    @Autowired
    private LTISecurityUtils ltiSecurityUtils;
    
    @Bean
    public LTIAwareUsernamePasswordAuthenticationFilter ltiAwareUsernamePasswordAuthenticationFilter() throws Exception {
        LTIAwareUsernamePasswordAuthenticationFilter filter = new LTIAwareUsernamePasswordAuthenticationFilter(ltiSecurityUtils);
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login-custom?error=true"));
        return filter;
    }

    @Bean
    public FilterRegistrationBean<LTIAwareUsernamePasswordAuthenticationFilter> ltiAwareUsernamePasswordAuthenticationFilterRegistration() throws Exception {
        FilterRegistrationBean<LTIAwareUsernamePasswordAuthenticationFilter> registration = new FilterRegistrationBean<LTIAwareUsernamePasswordAuthenticationFilter>();
        registration.setFilter(ltiAwareUsernamePasswordAuthenticationFilter());
        registration.addUrlPatterns("/login");
        registration.setName("ltiAwareUsernamePasswordAuthenticationFilter");
        registration.setOrder(1);
        registration.setEnabled(false);
        return registration;
    }

    @Autowired
    private AuthenticatedUserDetails authenticatedUserDetailsService;

    @Override
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return authenticatedUserDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .addFilterBefore(ltiAwareUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/login*","/access-denied").permitAll()
            .antMatchers("/sign-up/**").hasAnyRole("LTI","ANONYMOUS")
            .antMatchers("/student/**").hasAnyRole("STUDENT")
            .antMatchers("/admin/**").hasAnyRole("ADMIN")
            .and()
            .formLogin().loginPage("/login-custom").loginProcessingUrl("/login")
            .and()
            .headers()
            	.frameOptions().disable()
        	.and()
            	.exceptionHandling().accessDeniedHandler(ltiAwareAccessDeniedHandler());
    }

    @Bean
    public AccessDeniedHandler ltiAwareAccessDeniedHandler() {
		LTIAwareAccessDeniedHandler accessDeniedHandler = new LTIAwareAccessDeniedHandler();
		accessDeniedHandler.setLtiSecurityUtils(ltiSecurityUtils);
		return accessDeniedHandler;
	}

	@Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder());
        auth.inMemoryAuthentication()
            .withUser("test_student").password("{noop}password").roles("STUDENT")
            .and()
            .withUser("test_admin").password("{noop}password").roles("ADMIN");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }
}
