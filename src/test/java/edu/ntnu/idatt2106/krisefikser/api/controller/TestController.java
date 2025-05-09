package edu.ntnu.idatt2106.krisefikser.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/test")
@Profile("test")
public class TestController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostMapping("/reset-db")
    public ResponseEntity<String> resetDatabase() {
        try {
            executeScript("db/schema-test.sql");
            executeScript("db/data-test.sql");
            
            return ResponseEntity.ok("Database reset successful");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error resetting database: " + e.getMessage());
        }
    }
    
    private void executeScript(String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        String script = FileCopyUtils.copyToString(new InputStreamReader(
            resource.getInputStream(), StandardCharsets.UTF_8));
        
        jdbcTemplate.execute(script);
    }
}