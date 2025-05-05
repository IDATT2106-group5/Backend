package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.PasswordResetRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
  private final AuthService authService;

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
      logger.error("Unexpected error during registration for {}: {}", request.getEmail(),
          e.getMessage(), e);
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
  public ResponseEntity<Map<String, String>> authenticateUser(
      @RequestBody LoginRequest loginRequest) {
    try {
      if (loginRequest == null) {
        throw new IllegalArgumentException("Request object is null");
      }

      LoginResponse response = authService.loginUser(loginRequest);

      // If 2FA is required, inform the client
      if (response.isRequires2Fa()) {
        return ResponseEntity.ok(Map.of(
            "requires2FA", "true",
            "message", "2FA verification required"
        ));
      }

      return ResponseEntity.status(201).body(Map.of("token", response.getToken()));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during login: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during login for {}: {}",
          loginRequest != null ? loginRequest.getEmail() : "null",
          e.getMessage(), e);
      return ResponseEntity.internalServerError()
          .body(Map.of("error", "An unexpected error occurred"));
    }
  }

  /**
   * Confirms the user's email address using a confirmation token.
   *
   * @param token The confirmation token sent to the user's email.
   * @return ResponseEntity with a redirect to the success or failure page.
   */
  @GetMapping("/confirm")
  public ResponseEntity<Map<String, String>> confirmEmail(@RequestParam("token") String token) {
    try {
      authService.confirmUser(token);
      return ResponseEntity.status(302)
          .header("Location", "http://localhost:5173/register-success")
          .build();
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(302)
          .header("Location", "http://localhost:5173/register-failed")
          .build();
    }
  }

  /**
   * Initiates a password reset process by sending a reset link to the user's email.
   *
   * @param request the request containing the user's email
   * @return a response entity with a message
   */
  @PostMapping("/request-password-reset")
  public ResponseEntity<Map<String, String>> requestPasswordReset(
      @RequestBody Map<String, String> request) {
    try {
      String email = request.get("email");
      if (email == null || email.isBlank()) {
        logger.warn("Email is required for password reset");
        throw new IllegalArgumentException("Email is required");
      }

      authService.initiatePasswordReset(email);
      logger.info("Password reset link sent to email: {}", email);
      return ResponseEntity.ok(Map.of("message", "Password reset link sent to your email"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during password reset request: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during password reset request: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Resets the user's password using a reset token.
   *
   * @param request the request containing the new password and token
   * @return a response entity with a message
   */
  @PostMapping("/validate-reset-token")
  public ResponseEntity<Map<String, String>> validateResetToken(
      @RequestBody Map<String, String> request) {
    try {
      String token = request.get("token");
      if (token == null || token.isBlank()) {
        logger.warn("Missing token in request body");
        throw new IllegalArgumentException("Reset token is required");
      }

      authService.validateResetPasswordToken(token);
      return ResponseEntity.ok(Map.of("message", "Token is valid"));
    } catch (IllegalArgumentException e) {
      logger.warn("Invalid or expired reset token: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during token validation: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Resets the user's password using a reset token.
   *
   * @param request the request containing the new password and token
   * @return a response entity with a message
   */
  @PostMapping("/reset-password")
  public ResponseEntity<Map<String, String>> resetPassword(
      @RequestBody PasswordResetRequestDto request) {
    try {
      String token = request.getToken();
      String newPassword = request.getNewPassword();

      if (token == null || token.isBlank()) {
        throw new IllegalArgumentException("Reset token is required");
      }
      if (newPassword == null || newPassword.isBlank()) {
        throw new IllegalArgumentException("New password is required");
      }

      authService.resetPassword(token, newPassword);
      return ResponseEntity.ok(Map.of("message", "Password reset successful"));
    } catch (IllegalArgumentException e) {
      logger.warn("Password reset failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error during password reset: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
