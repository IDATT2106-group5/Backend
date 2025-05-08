package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.TwoFactorVerifyRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.admin.AdminInviteRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.admin.AdminSetupRequest;
import edu.ntnu.idatt2106.krisefikser.service.AdminInvitationService;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import edu.ntnu.idatt2106.krisefikser.service.TwoFactorService;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling admin-related requests. This includes inviting new admins, setting up
 * admin accounts, and handling two-factor authentication.
 */
@Tag(name = "Admin", description = "Endpoints for admin related requests")
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
  private final AdminInvitationService adminInvitationService;
  private final TwoFactorService twoFactorService;
  private final AuthService authService;
  private final UserService userService;

  /**
   * Constructor for AdminController.
   *
   * @param adminInvitationService The service for handling admin invitations.
   * @param twoFactorService       The service for handling two-factor authentication.
   * @param authService            The service for handling authentication.
   */
  public AdminController(AdminInvitationService adminInvitationService,
      TwoFactorService twoFactorService, AuthService authService, UserService userService) {
    this.adminInvitationService = adminInvitationService;
    this.twoFactorService = twoFactorService;
    this.authService = authService;
    this.userService = userService;
  }

  /**
   * Invites a new admin user by sending an invitation email.
   *
   * @param request The request containing the email and full name of the new admin.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Invites a new admin user",
      description = "Sends an invitation to an email with a link to create an admin user")
  @PostMapping("/invite")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> inviteAdmin(@RequestBody AdminInviteRequest request) {
    try {
      adminInvitationService.createAdminInvitation(request.getEmail(), request.getFullName());
      logger.info("Admin invitation sent to: {}", request.getEmail());
      return ResponseEntity.ok(Map.of("message", "Admin invitation sent successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during admin invitation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Completes the admin setup process. This includes verifying the invitation token and setting the
   * password.
   *
   * @param request The request containing the token and password.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Completes the admin setup process",
      description = "Verifies the confirmation token and sets the password for the new admin user")
  @PostMapping("/setup")
  @PreAuthorize("isAnonymous()")  // This allows only unauthenticated users
  public ResponseEntity<?> setupAdmin(@RequestBody AdminSetupRequest request) {
    try {
      logger.info("Setup admin request from frontend: {}", request.getToken());
      adminInvitationService.completeAdminSetup(request.getToken(), request.getPassword());
      return ResponseEntity.ok(Map.of("message", "Admin account setup completed"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during admin setup: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Generates a two-factor authentication code and sends it to the user's email.
   *
   * @param request The request containing the email address.
   * @return A response entity indicating the result of the operation.
   */
  @Operation(summary = "Sends two-factor authentication code to the user's email",
      description = "Generates a two-factor authentication code and sends it to the user's email")
  @PostMapping("/login/2fa/generate")
  public ResponseEntity<?> generateTwoFactorCode(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    twoFactorService.generateAndSendOtp(email);
    logger.info("2FA code sent to email: {}", email);
    return ResponseEntity.ok(Map.of("message", "2FA code sent to your email"));
  }

  /**
   * Verifies the two-factor authentication code provided by the user.
   *
   * @param request The request containing the email address and OTP code.
   * @return A response entity with the JWT token if verification is successful.
   */
  @Operation(summary = "Verifies the two-factor authentication code",
      description = "Verifies the two-factor code and returns a JWT token if successful")
  @PostMapping("/login/2fa/verify")
  public ResponseEntity<?> verifyTwoFactor(@RequestBody TwoFactorVerifyRequest request) {
    try {
      LoginResponse response = authService.verify2Fa(request.getEmail(), request.getOtp());
      logger.info("2FA verification successful for email: {}", request.getEmail());
      return ResponseEntity.ok(Map.of(
          "token", response.getToken(),
          "message", "2FA verification successful"
      ));
    } catch (IllegalArgumentException e) {
      logger.warn("2FA verification failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Initiates a password reset for an admin user. Only accessible by SUPERADMIN users.
   *
   * @param request The request containing the admin's email
   * @return ResponseEntity indicating the result of the operation
   */
  @Operation(summary = "Initiates password reset for an admin user.",
      description = "Initiates password reset for an admin user by sending a reset email."
          + "only accesible by SUPERADMIN users.")
  @PostMapping("/reset-password/initiate")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> initiateAdminPasswordReset(@RequestBody Map<String, String> request) {
    try {
      String email = request.get("email");
      authService.initiatePasswordReset(email);
      logger.info("Password reset email sent to admin: {}", email);
      return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during password reset initiation: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Gets a list of all admin and superadmin users. Only accessible by SUPERADMIN users.
   *
   * @return ResponseEntity containing the list of admin and superadmin users
   */
  @Operation(summary = "Fetches all admin and superadmin users",
      description = "Gets a list of all admin and superadmin users. "
          + "Only accesible by SUPERADMIN users.")
  @GetMapping
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> getAllAdmins() {
    try {
      List<UserResponseDto> admins = userService.getAllAdmins();
      logger.info("Fetched all admin users successfully");
      return ResponseEntity.ok(admins);
    } catch (Exception e) {
      logger.error("Error fetching admin users: {}", e.getMessage());
      return ResponseEntity.status(500)
          .body(Map.of("error", "Error retrieving admins: " + e.getMessage()));
    }
  }

  /**
   * Deletes an admin user by their ID. Only accessible by SUPERADMIN users.
   *
   * @param request The request containing the admin ID
   * @return ResponseEntity indicating the result of the operation
   */
  @Operation(summary = "Deletes an admin user",
      description = "Deletes an admin user by their id. Only accesible by SUPERADMIN users")
  @PostMapping("/delete")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> deleteAdmin(@RequestBody Map<String, String> request) {
    try {
      adminInvitationService.deleteAdmin(request.get("adminId"));
      logger.info("Admin deleted successfully");
      return ResponseEntity.ok(Map.of("message", "Admin deleted successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error during admin deletion: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}