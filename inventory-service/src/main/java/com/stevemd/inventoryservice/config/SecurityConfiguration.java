package com.stevemd.inventoryservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()  // Disable basic authentication (if needed)
                .csrf().disable()  // Disable CSRF for stateless applications
                .authorizeHttpRequests()  // Use authorizeHttpRequests instead of authorizeRequests
                .requestMatchers("/inventory/**").permitAll()  // Use requestMatchers instead of antMatchers
                .anyRequest().authenticated()  // Require authentication for all other requests
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Use stateless session management (for JWT or other stateless systems)

        return httpSecurity.build();
    }
}
