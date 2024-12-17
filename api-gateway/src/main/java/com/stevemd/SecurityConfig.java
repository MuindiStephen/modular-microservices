package com.stevemd;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
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
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic().disable()  // Disable basic authentication (if needed)
                .csrf().disable()  // Disable CSRF for stateless applications
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**").permitAll()  // Allow public endpoints (e.g., login or registration)
                .requestMatchers("/api/v1/product/**", "/api/v1/order/**").authenticated()  // Protect routes to services
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);  // Stateless session management

        return httpSecurity.build();
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", r -> r.path("/api/v1/product/**")
                        .filters(f -> f.addRequestHeader("Authorization", "#{request.headers['Authorization']}"))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/v1/order/**")
                        .filters(f -> f.addRequestHeader("Authorization", "#{request.headers['Authorization']}"))
                        .uri("lb://order-service"))
                .build();
    }

}

