package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import edu.ntnu.idatt2106.krisefikser.security.MockJwtTokenProvider;
import jakarta.annotation.PostConstruct;

@TestConfiguration
@Profile("test")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestJwtConfig {

    @PostConstruct
    public void setup() {
        // These need to be set as system properties for other components
        System.setProperty("JWT_SECRET", "test-secret-key-that-is-very-long-for-testing-purposes-only");
        System.setProperty("JWT_EXPIRATION_MS", "86400000");
        System.setProperty("app.jwt.secret", "test-secret-key-that-is-very-long-for-testing-purposes-only");
        System.setProperty("app.jwt.expiration-ms", "86400000");
        
        System.getProperties().put("JWT_SECRET", "test-secret-key-that-is-very-long-for-testing-purposes-only");
        System.getProperties().put("JWT_EXPIRATION_MS", "86400000");
    }
    
    @Bean
    @Primary
    public JwtTokenProvider jwtTokenProvider() {
        return new MockJwtTokenProvider();
    }
}