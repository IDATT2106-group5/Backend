package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(Integer.MIN_VALUE) // Absolute highest priority (-2147483648)
public class TestApiSecurityConfig {

    @Bean
    public SecurityFilterChain testApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/test/**") // Only apply to test API endpoints
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable);
        
        return http.build();
    }
}