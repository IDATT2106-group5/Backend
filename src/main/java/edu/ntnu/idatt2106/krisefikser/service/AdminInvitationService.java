package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling admin invitation and setup. This includes creating an invitation for a new
 * admin, validating the invitation token, and completing the admin setup. It also includes password
 * validation.
 */

@Service
public class AdminInvitationService {

  private final UserRepository userRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  /**
   * Constructor for AdminInvitationService.
   *
   * @param userRepository  The repository for user-related operations.
   * @param emailService    The service for sending emails.
   * @param passwordEncoder The password encoder for hashing passwords.
   */
  @Autowired
  public AdminInvitationService(UserRepository userRepository,
      EmailService emailService,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.emailService = emailService;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Creates an invitation for a new admin user. This includes generating a unique token, creating a
   * new user with the token, and sending an invitation email.
   *
   * @param email    The email address of the new admin user.
   * @param fullName The full name of the new admin user.
   */
  public void createAdminInvitation(String email, String fullName) {
    // Generate unique token
    String token = UUID.randomUUID().toString();

    // Generate a random placeholder password
    String randomPlaceholder = UUID.randomUUID().toString() + UUID.randomUUID().toString();
    String encodedPlaceholder = passwordEncoder.encode(randomPlaceholder);

    // Create admin user with token
    User adminUser = new User();
    adminUser.setEmail(email);
    adminUser.setFullName(fullName);
    adminUser.setPassword(encodedPlaceholder);
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
    String invitationLink = "http://localhost:5173/admin-registration?email=" + email + "&token=" + token;
    emailService.sendAdminInvitation(email, invitationLink);
  }

  /**
   * Validates the admin setup token. This includes checking if the token is valid, if the user is
   * an admin, and if the token has not expired.
   *
   * @param token The token to validate.
   * @return True if the token is valid, false otherwise.
   */
  public boolean validateAdminSetupToken(String token) {
    return userRepository.findByConfirmationToken(token)
        .map(user -> {
          // Check if user is admin, not confirmed, and token hasn't expired
          boolean isAdmin = user.getRole() == Role.ADMIN;
          boolean notConfirmed = !user.isConfirmed();
          boolean notExpired = user.getTokenExpiry() != null
              && new Date().before(user.getTokenExpiry());

          return isAdmin && notConfirmed && notExpired;
        })
        .orElse(false);
  }

  /**
   * Completes the admin setup process. This includes verifying the invitation token and setting the
   * password.
   *
   * @param token    The token to validate.
   * @param password The password to set for the admin user.
   */
  public void completeAdminSetup(String token, String password) {
    User admin = userRepository.findByConfirmationToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

    // Check if token has expired
    if (admin.getTokenExpiry() == null
        || new Date().after(admin.getTokenExpiry())) {
      throw new IllegalArgumentException("Token has expired");
    }

    // Validate password
    if (!isValidPassword(password)) {
      throw new IllegalArgumentException(
          "Password must be at least 8 characters and include uppercase, "
              + "lowercase, number and special character");
    }

    // Update admin user
    admin.setPassword(passwordEncoder.encode(password));
    admin.setConfirmed(true);
    admin.setConfirmationToken(null);

    userRepository.save(admin);
  }

  private boolean isValidPassword(String password) {
    return password.length() >= 8
        && password.matches(".*[A-Z].*")
        && password.matches(".*[a-z].*")
        && password.matches(".*[0-9].*")
        && password.matches(".*[@#$%^&+=!].*");
  }

  /**
   * Deletes an admin user by their ID. Only users with ADMIN role can be deleted with this method.
   *
   * @param adminId The ID of the admin user to delete
   * @throws IllegalArgumentException if the user doesn't exist or isn't an admin
   */
  @Transactional
  public void deleteAdmin(Long adminId) {
    User admin = userRepository.findById(adminId)
        .orElseThrow(() -> new IllegalArgumentException("Admin user not found"));

    // Check if user is actually an admin
    if (admin.getRole() != Role.ADMIN) {
      throw new IllegalArgumentException("User is not an admin");
    }

    // Delete the admin user
    userRepository.delete(admin);
  }
}