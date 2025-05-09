package edu.ntnu.idatt2106.krisefikser.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class TestController {
    
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        logger.info("Health check called at {}", new Date());
        return ResponseEntity.ok("Test API is healthy");
    }

    @PostMapping("/reset-db")
    public ResponseEntity<String> resetDatabase() {
        logger.info("Resetting database...");
        try {
            executeScript("db/schema-test.sql");
            executeScript("db/data-test.sql");
            logger.info("Database reset successful");
            return ResponseEntity.ok("Database reset successful");
        } catch (Exception e) {
            logger.error("Error resetting database", e);
            return ResponseEntity.internalServerError().body("Error resetting database: " + e.getMessage());
        }
    }
    
    private void executeScript(String path) throws IOException {
        logger.info("Executing script: {}", path);
        Resource resource = new ClassPathResource(path);
        String script = FileCopyUtils.copyToString(new InputStreamReader(
            resource.getInputStream(), StandardCharsets.UTF_8));
        
        jdbcTemplate.execute(script);
    }
}