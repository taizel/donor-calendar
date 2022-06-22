package org.donorcalendar;

import org.donorcalendar.persistence.UserCredentialsDao;
import org.donorcalendar.persistence.UserProfileDao;
import org.donorcalendar.security.UserSecurityDetailsService;
import org.donorcalendar.service.UserCredentialsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserProfileDao userProfileDao, UserCredentialsDao userCredentialsDao) {
        userDetailsService = new UserSecurityDetailsService(userProfileDao, userCredentialsDao);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            return http
                    .csrf().disable()
                    .authorizeRequests()
                    // Allowing unauthenticated access to static resources common locations
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    // Allowing unauthenticated access to method for registering a new donor
                    .mvcMatchers(HttpMethod.POST, "/user").permitAll()
                    .mvcMatchers(HttpMethod.GET, "/v3/api-docs", "/swagger-ui.html").permitAll()
                    // Requesting authentication on all requests except the allowed ones
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic().and().build();
        } catch (Exception e) {
            throw new SecurityException("Unable to properly instantiate SecurityFilterChain");
        }
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return UserCredentialsService.getNewPasswordEncoder();
    }
}
