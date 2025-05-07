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

  @PostMapping("/invite")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> inviteAdmin(@RequestBody AdminInviteRequest request) {
    adminInvitationService.createAdminInvitation(request.getEmail(), request.getFullName());
    return ResponseEntity.ok(Map.of("message", "Admin invitation sent successfully"));
  }

  /**
   * Completes the admin setup process. This includes verifying the invitation token and setting the
   * password.
   *
   * @param request The request containing the token and password.
   * @return A response entity indicating the result of the operation.
   */
  @PostMapping("/setup")
  @PreAuthorize("isAnonymous()")  // This allows only unauthenticated users
  public ResponseEntity<?> setupAdmin(@RequestBody AdminSetupRequest request) {
    try {
      logger.info("Setup admin request from frontend: {}", request.getToken());
      adminInvitationService.completeAdminSetup(request.getToken(), request.getPassword());
      return ResponseEntity.ok(Map.of("message", "Admin account setup completed"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Generates a two-factor authentication code and sends it to the user's email.
   *
   * @param request The request containing the email address.
   * @return A response entity indicating the result of the operation.
   */
  @PostMapping("/login/2fa/generate")
  public ResponseEntity<?> generateTwoFactorCode(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    twoFactorService.generateAndSendOtp(email);
    return ResponseEntity.ok(Map.of("message", "2FA code sent to your email"));
  }

  /**
   * Verifies the two-factor authentication code provided by the user.
   *
   * @param request The request containing the email address and OTP code.
   * @return A response entity with the JWT token if verification is successful.
   */
  @PostMapping("/login/2fa/verify")
  public ResponseEntity<?> verifyTwoFactor(@RequestBody TwoFactorVerifyRequest request) {
    try {
      LoginResponse response = authService.verify2Fa(request.getEmail(), request.getOtp());
      return ResponseEntity.ok(Map.of(
          "token", response.getToken(),
          "message", "2FA verification successful"
      ));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Initiates a password reset for an admin user. Only accessible by SUPERADMIN users.
   *
   * @param request The request containing the admin's email
   * @return ResponseEntity indicating the result of the operation
   */
  @PostMapping("/reset-password/initiate")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> initiateAdminPasswordReset(@RequestBody Map<String, String> request) {
    try {
      String email = request.get("email");
      authService.initiatePasswordReset(email);
      return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  /**
   * Gets a list of all admin and superadmin users. Only accessible by SUPERADMIN users.
   *
   * @return ResponseEntity containing the list of admin and superadmin users
   */
  @GetMapping
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> getAllAdmins() {
    try {
      List<UserResponseDto> admins = userService.getAllAdmins();
      return ResponseEntity.ok(admins);
    } catch (Exception e) {
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
  @PostMapping("/delete")
  @PreAuthorize("hasRole('SUPERADMIN')")
  public ResponseEntity<?> deleteAdmin(@RequestBody Map<String, Long> request) {
    try {
      adminInvitationService.deleteAdmin(request.get("adminId"));
      return ResponseEntity.ok(Map.of("message", "Admin deleted successfully"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }
}