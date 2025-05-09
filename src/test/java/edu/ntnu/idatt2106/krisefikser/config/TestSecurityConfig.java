package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@Profile("test")
@Order(-100) // Very high priority to override other configs
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testApiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/api/test/**"))
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(AbstractHttpConfigurer::disable)  // Disable CSRF for test endpoints
            .cors(cors -> cors.configure(http));    // Enable CORS

        return http.build();
    }
}
