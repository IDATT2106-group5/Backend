package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller handling authentication requests such as login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;

  /**
   * Constructor for AuthController.
   *
   * @param authService the authentication service
   */
  public AuthController(AuthService authService, AuthenticationManager authenticationManager,
                        JwtTokenProvider tokenProvider) {
    this.authService = authService;
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
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

  /**
   * Process login requests and return JWT tokens.
   *
   * @param loginRequest - a {@link LoginRequest} containing user credentials
   * @return ResponseEntity with JWT token if authentication successful
   */
  @PostMapping("/login")
  public ResponseEntity<Map<String, LoginResponse>> authenticateUser(@RequestBody LoginRequest loginRequest) {
    try {
      LoginResponse response = authService.loginUser(loginRequest);
      return ResponseEntity.status(201).body(Map.of("token", response));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during login: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", null));
    } catch (Exception e) {
      logger.error("Unexpected error during login for {}: {}", loginRequest.getEmail(), e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", null));
    }
  }
}
