package org.donorcalendar;

import org.donorcalendar.service.UserCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(@Qualifier("UserDetailsService") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.
            csrf().disable();

        http
            .authorizeRequests()
                // Allowing unauthenticated access to static resources common locations
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // Allowing unauthenticated access to method for registering a new donor
                .mvcMatchers(HttpMethod.POST, "/user").permitAll()
                // Requesting authentication on all requests except the allowed ones
                .anyRequest().authenticated()
                .and()
            .httpBasic()
        ;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(UserCredentialsService.getNewPasswordEncoder());
    }
}