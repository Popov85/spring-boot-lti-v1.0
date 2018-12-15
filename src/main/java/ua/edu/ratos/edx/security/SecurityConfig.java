package ua.edu.ratos.edx.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.edu.ratos.edx.security.lti.LTIAwareUsernamePasswordAuthenticationFilter;

@Order(2)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // LTI Pre- bean
    @Bean
    public LTIAwareUsernamePasswordAuthenticationFilter ltiAwareUsernamePasswordAuthenticationFilter() throws Exception {
        LTIAwareUsernamePasswordAuthenticationFilter filter = new LTIAwareUsernamePasswordAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager());
        filter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler("/login?error"));
        return filter;
    }

    @Bean
    public FilterRegistrationBean ltiAwareUsernamePasswordAuthenticationFilterRegistration() throws Exception {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(ltiAwareUsernamePasswordAuthenticationFilter());
        registration.addUrlPatterns("/login");
        registration.setName("ltiAwareUsernamePasswordAuthenticationFilter");
        registration.setOrder(1);
        registration.setEnabled(false);
        return registration;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .addFilterBefore(ltiAwareUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/login*","/sign-in/**").permitAll()
            .antMatchers("/student/**").hasAnyRole("STUDENT")
            .and()
            .formLogin();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceBean()).passwordEncoder(passwordEncoder());
        auth.inMemoryAuthentication()
            .withUser("student").password("{noop}dT09Rx06").roles("STUDENT");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }
}
