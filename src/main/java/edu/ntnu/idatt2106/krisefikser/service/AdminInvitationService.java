package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminInvitationService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public AdminInvitationService(UserRepository userRepository,
      EmailService emailService,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
  }

  public void createAdminInvitation(String email, String fullName) {
    // Generate unique token
    String token = UUID.randomUUID().toString();

    // Create admin user with token
    User adminUser = new User();
    adminUser.setEmail(email);
    adminUser.setFullName(fullName);
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmationToken(token);
    adminUser.setConfirmed(false);

    // Set token expiration (1 hour)
    // Store expiration time in database
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());

    userRepository.save(adminUser);

    // Send invitation email
    // At the moment, this is hardcoded, and should be switched out with the frontend URL
    // when the frontend is ready.
    String invitationLink = "http://yourapp.com/admin/setup?token=" + token;
    emailService.sendAdminInvitation(email, invitationLink);
  }

  public boolean validateAdminSetupToken(String token) {
    return userRepository.findByConfirmationToken(token)
        .map(user -> {
          // Check if user is admin, not confirmed, and token hasn't expired
          boolean isAdmin = user.getRole() == Role.ADMIN;
          boolean notConfirmed = !user.isConfirmed();
          boolean notExpired = user.getTokenExpiry() != null &&
              new Date().before(user.getTokenExpiry());

          return isAdmin && notConfirmed && notExpired;
        })
        .orElse(false);
  }

  public void completeAdminSetup(String token, String password) {
    User admin = userRepository.findByConfirmationToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    // Check if token has expired
    if (admin.getTokenExpiry() == null || new Date().after(admin.getTokenExpiry())) {
      throw new IllegalArgumentException("Token has expired");
    }
    
    // Validate password
    if (!isValidPassword(password)) {
      throw new IllegalArgumentException(
          "Password must be at least 8 characters and include uppercase, lowercase, number and special character");
    }

    // Update admin user
    admin.setPassword(passwordEncoder.encode(password));
    admin.setConfirmed(true);
    admin.setConfirmationToken(null);

    userRepository.save(admin);
  }

  private boolean isValidPassword(String password) {
    return password.length() >= 8 &&
        password.matches(".*[A-Z].*") &&
        password.matches(".*[a-z].*") &&
        password.matches(".*[0-9].*") &&
        password.matches(".*[@#$%^&+=!].*");
  }
}