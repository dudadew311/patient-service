package com.clinic.patient_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless REST APIs using HTTP Basic Auth
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // 1. Allow public access to Swagger UI and OpenAPI docs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                        // 2. Allow anyone to view paginated patient data
                        .requestMatchers(HttpMethod.GET, "/api/v1/patients/**").permitAll()

                        // 3. Restrict all modifications to ADMIN users
                        .requestMatchers(HttpMethod.POST, "/api/v1/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/patients/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**").hasRole("ADMIN")

                        // Any other random request must be authenticated
                        .anyRequest().authenticated()
                )
                // Use standard basic credentials header entrypoints
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Configure two mock users in-memory for immediate testing
        UserDetails staff = User.withDefaultPasswordEncoder()
                .username("staff")
                .password("staff123")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin123")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(staff, admin);
    }
}