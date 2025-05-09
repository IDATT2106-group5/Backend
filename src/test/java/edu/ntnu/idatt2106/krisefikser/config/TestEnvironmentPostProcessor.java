package edu.ntnu.idatt2106.krisefikser.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class TestEnvironmentPostProcessor implements EnvironmentPostProcessor {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Only apply in test profiles
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equals("test")) {
                Properties props = new Properties();
                props.put("JWT_SECRET", "test-secret-key-that-is-very-long-for-testing-purposes-only");
                props.put("JWT_EXPIRATION_MS", "86400000");
                props.put("app.jwt.secret", "test-secret-key-that-is-very-long-for-testing-purposes-only");
                props.put("app.jwt.expiration-ms", "86400000");
                
                // Add with highest precedence
                environment.getPropertySources().addFirst(
                    new PropertiesPropertySource("testJwtProperties", props)
                );
                
                return;
            }
        }
    }
}