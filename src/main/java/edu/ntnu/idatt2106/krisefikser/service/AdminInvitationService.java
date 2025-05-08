package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger logger = LoggerFactory.getLogger(AdminInvitationService.class);
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
    logger.info("AdminInvitationService initialized");
  }

  /**
   * Creates an invitation for a new admin user. This includes generating a unique token, creating a
   * new user with the token, and sending an invitation email.
   *
   * @param email    The email address of the new admin user.
   * @param fullName The full name of the new admin user.
   */
  public void createAdminInvitation(String email, String fullName) {
    logger.info("Creating admin invitation for email: {}", email);
    if (userRepository.existsByEmail(email)) {
      logger.warn("Admin invitation failed: User with email {} already exists", email);
      throw new IllegalArgumentException("A user with that email already exists");
    }

    String token = UUID.randomUUID().toString();
    logger.debug("Generated unique token for admin invitation");

    String randomPlaceholder = UUID.randomUUID().toString() + UUID.randomUUID();
    String encodedPlaceholder = passwordEncoder.encode(randomPlaceholder);
    logger.debug("Generated placeholder password for admin invitation");

    User adminUser = new User();
    adminUser.setEmail(email);
    adminUser.setFullName(fullName);
    adminUser.setPassword(encodedPlaceholder);
    adminUser.setRole(Role.ADMIN);
    adminUser.setConfirmationToken(token);
    adminUser.setConfirmed(false);

    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 1);
    adminUser.setTokenExpiry(calendar.getTime());
    logger.debug("Admin token will expire at: {}", adminUser.getTokenExpiry());

    userRepository.save(adminUser);
    logger.info("Admin user created in database for email: {}", email);

    try {
      String invitationLink =
          "http://localhost:5173/admin-registration?email=" + email + "&token=" + token;
      emailService.sendAdminInvitation(email, invitationLink);
      logger.info("Admin invitation email sent to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send admin invitation email to {}: {}", email, e.getMessage());
    }
  }

  /**
   * Validates the admin setup token. This includes checking if the token is valid, if the user is
   * an admin, and if the token has not expired.
   *
   * @param token The token to validate.
   * @return True if the token is valid, false otherwise.
   */
  public boolean validateAdminSetupToken(String token) {
    logger.info("Validating admin setup token");
    logger.debug("Token being validated: {}", token);

    return userRepository.findByConfirmationToken(token)
        .map(user -> {
          boolean isAdmin = user.getRole() == Role.ADMIN;
          boolean notConfirmed = !user.isConfirmed();
          boolean notExpired = user.getTokenExpiry() != null
              && new Date().before(user.getTokenExpiry());

          if (!isAdmin) {
            logger.warn("Token validation failed: User is not an admin. Email: {}",
                user.getEmail());
            return false;
          }
          if (!notConfirmed) {
            logger.warn("Token validation failed: User is already confirmed. Email: {}",
                user.getEmail());
            return false;
          }
          if (!notExpired) {
            logger.warn("Token validation failed: Token has expired. Email: {}", user.getEmail());
            return false;
          }

          logger.info("Admin token validation successful for user: {}", user.getEmail());
          return true;
        })
        .orElseGet(() -> {
          logger.warn("Token validation failed: Token not found in database");
          return false;
        });
  }

  /**
   * Completes the admin setup process. This includes verifying the invitation token and setting the
   * password.
   *
   * @param token    The token to validate.
   * @param password The password to set for the admin user.
   */
  public void completeAdminSetup(String token, String password) {
    logger.info("Completing admin setup with token");
    logger.debug("Processing admin setup for token: {}", token);

    User admin = userRepository.findByConfirmationToken(token)
        .orElseThrow(() -> {
          logger.warn("Admin setup failed: Invalid token provided");
          return new IllegalArgumentException("Invalid token");
        });

    if (admin.getTokenExpiry() == null || new Date().after(admin.getTokenExpiry())) {
      logger.warn("Admin setup failed: Token has expired for user: {}", admin.getEmail());
      throw new IllegalArgumentException("Token has expired");
    }

    if (!isValidPassword(password)) {
      logger.warn("Admin setup failed: Invalid password format for user: {}", admin.getEmail());
      throw new IllegalArgumentException(
          "Password must be at least 8 characters and include uppercase, "
              + "lowercase, number and special character");
    }

    admin.setPassword(passwordEncoder.encode(password));
    admin.setConfirmed(true);
    admin.setConfirmationToken(null);

    userRepository.save(admin);
    logger.info("Admin setup completed successfully for user: {}", admin.getEmail());
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
  public void deleteAdmin(String adminId) {
    logger.info("Attempting to delete admin with ID: {}", adminId);

    User admin = userRepository.findById(adminId)
        .orElseThrow(() -> {
          logger.warn("Admin deletion failed: User not found with ID: {}", adminId);
          return new IllegalArgumentException("Admin user not found");
        });

    if (admin.getRole() != Role.ADMIN) {
      logger.warn("Admin deletion failed: User with ID {} is not an admin. Actual role: {}",
          adminId, admin.getRole());
      throw new IllegalArgumentException("User is not an admin");
    }

    userRepository.delete(admin);
    logger.info("Admin user deleted successfully. ID: {}, Email: {}", adminId, admin.getEmail());
  }
}