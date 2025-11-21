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
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(UserProfileDao userProfileDao, UserCredentialsDao userCredentialsDao) {
        userDetailsService = new UserSecurityDetailsService(userProfileDao, userCredentialsDao);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        try {
            http
                    // CSRF is disabled intentionally because the API is stateless and uses HTTP Basic auth.
                    // It does not rely on cookies, so CSRF protection is unnecessary. (S3751)
                    .csrf(csrf -> csrf.disable())
                    .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth
                            // Allow static resources
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            // Allow POST /user
                            .requestMatchers(HttpMethod.POST, "/user").permitAll()
                            // Allow Swagger docs
                            .requestMatchers(HttpMethod.GET, "/v3/api-docs", "/swagger-ui.html").permitAll()
                            // Everything else requires auth
                            .anyRequest().authenticated()
                    )
                    .httpBasic(Customizer.withDefaults());

            return http.build();
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
