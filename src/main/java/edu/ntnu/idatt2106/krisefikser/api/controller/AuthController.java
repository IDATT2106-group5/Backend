package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller for handling authentication requests.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  /**
   * Constructor for AuthController.
   *
   * @param authService the authentication service
   */
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  /**
   * Register a new user.
   *
   * @param request the user to register
   * @return a response entity with a message
   */
  @PostMapping("/register")
  public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDto request) {
    try {
      authService.registerUser(request);
      return ResponseEntity.status(201).body(Map.of("message", "User registered successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during registration: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during registration for {}: {}", request.getEmail(), e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
