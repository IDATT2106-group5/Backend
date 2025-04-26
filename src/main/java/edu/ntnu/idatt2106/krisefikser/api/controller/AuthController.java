package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller handling authentication requests.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;
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
      logger.error("Unexpected error during registration for {}: {}", request.getEmail(),
          e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Process login requests and return JWT tokens.
   *
   * @param loginRequest containing user credentials
   * @return ResponseEntity with JWT token if authentication successful
   */
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
    // Authenticate the user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        )
    );

    // Set the authentication object in the security context
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Generate JWT token
    String jwt = tokenProvider.generateToken(authentication);

    // Return token in the response
    return ResponseEntity.ok(new LoginResponse(jwt));
  }

  @GetMapping("/confirm")
  public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam("token") String token) {
    try {
      authService.confirmUser(token);
      return ResponseEntity.ok(Map.of("message", "Email confirmed successfully"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}
