package com.stevemd.orderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@Deprecated(since = "6.1", forRemoval = true)
public class SecurityConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()  // Disable basic authentication (if needed)
                .csrf().disable()  // Disable CSRF for stateless applications
                .authorizeRequests()
                .requestMatchers("/order/**").permitAll()  // Use requestMatchers instead of antMatchers
                .anyRequest().authenticated()  // Require authentication for all other requests
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Use stateless session management (for JWT or other stateless systems)

        return httpSecurity.build();
    }
}
