package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.AdminInviteRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.AdminSetupRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.TwoFactorVerifyRequest;
import edu.ntnu.idatt2106.krisefikser.service.AdminInvitationService;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import edu.ntnu.idatt2106.krisefikser.service.TwoFactorService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class AdminController {

  private final AdminInvitationService adminInvitationService;
  private final TwoFactorService twoFactorService;
  private final AuthService authService;

  /**
   * Constructor for AdminController.
   *
   * @param adminInvitationService The service for handling admin invitations.
   * @param twoFactorService       The service for handling two-factor authentication.
   * @param authService            The service for handling authentication.
   */
  public AdminController(AdminInvitationService adminInvitationService,
      TwoFactorService twoFactorService, AuthService authService) {
    this.adminInvitationService = adminInvitationService;
    this.twoFactorService = twoFactorService;
    this.authService = authService;
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
  public ResponseEntity<?> setupAdmin(@RequestBody AdminSetupRequest request) {
    try {
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
}