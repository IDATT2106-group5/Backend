package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.AdminInviteRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.AdminSetupRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.TwoFactorVerifyRequest;
import edu.ntnu.idatt2106.krisefikser.service.AdminInvitationService;
import edu.ntnu.idatt2106.krisefikser.service.TwoFactorService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final AdminInvitationService adminInvitationService;
  private final TwoFactorService twoFactorService;

  public AdminController(AdminInvitationService adminInvitationService,
      TwoFactorService twoFactorService) {
    this.adminInvitationService = adminInvitationService;
    this.twoFactorService = twoFactorService;
  }

  @PostMapping("/invite")
  @PreAuthorize("hasRole('ADMIN_SUPERUSER')")
  public ResponseEntity<?> inviteAdmin(@RequestBody AdminInviteRequest request) {
    adminInvitationService.createAdminInvitation(request.getEmail(), request.getFullName());
    return ResponseEntity.ok(Map.of("message", "Admin invitation sent successfully"));
  }

  @PostMapping("/setup")
  public ResponseEntity<?> setupAdmin(@RequestBody AdminSetupRequest request) {
    try {
      adminInvitationService.completeAdminSetup(request.getToken(), request.getPassword());
      return ResponseEntity.ok(Map.of("message", "Admin account setup completed"));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/login/2fa/generate")
  public ResponseEntity<?> generateTwoFactorCode(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    twoFactorService.generateAndSendOtp(email);
    return ResponseEntity.ok(Map.of("message", "2FA code sent to your email"));
  }

  @PostMapping("/login/2fa/verify")
  public ResponseEntity<?> verifyTwoFactor(@RequestBody TwoFactorVerifyRequest request) {
    boolean isValid = twoFactorService.verifyOtp(request.getEmail(), request.getOtp());
    if (isValid) {
      return ResponseEntity.ok(Map.of("message", "2FA verification successful"));
    } else {
      return ResponseEntity.badRequest().body(Map.of("error", "Invalid 2FA code"));
    }
  }
}