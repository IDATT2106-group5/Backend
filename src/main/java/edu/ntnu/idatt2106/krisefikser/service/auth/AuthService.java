package edu.ntnu.idatt2106.krisefikser.service.auth;

import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.user.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthService {

  private static final Pattern PASSWORD_PATTERN =
      Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final LoginAttemptService loginAttemptService;
  private final TwoFactorService twoFactorService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final EmailService emailService;
  private final CaptchaService captchaService;


  /**
   * Constructor for AuthService.
   *
   * @param userRepository        The repository for user-related operations.
   * @param passwordEncoder       The password encoder for hashing passwords.
   * @param emailService          The service for sending emails.
   * @param authenticationManager The authentication manager for handling authentication.
   * @param tokenProvider         The JWT token provider for generating and validating tokens.
   * @param loginAttemptService   The service for handling login attempts and blocking accounts.
   * @param twoFactorService      The service for handling two-factor authentication.
   */
  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      EmailService emailService,
      AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider,
      CaptchaService captchaService, LoginAttemptService loginAttemptService,
      TwoFactorService twoFactorService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.captchaService = captchaService;
    this.loginAttemptService = loginAttemptService;
    this.twoFactorService = twoFactorService;
    logger.info("AuthService initialized");
  }

  /**
   * Validates a password against security requirements.
   *
   * @param password the password to validate
   * @return true if the password meets all requirements, false otherwise
   */
  public boolean validatePassword(String password) {
    boolean isValid = PASSWORD_PATTERN.matcher(password).matches();
    logger.debug("Password validation result: {}", isValid);
    return isValid;
  }

  /**
   * Registers a new user.
   *
   * @param request the user to register
   */
  public void registerUser(RegisterRequestDto request) {
    logger.info("Processing user registration for email: {}", request.getEmail());

    if (!captchaService.verifyToken(request.gethCaptchaToken())) {
      logger.warn("hCaptcha validation failed for email: {}", request.getEmail());
      throw new IllegalArgumentException("hCaptcha verification failed. Please try again.");
    }
    logger.debug("hCaptcha validation successful for email: {}", request.getEmail());

    if (userRepository.existsByEmail(request.getEmail())) {
      logger.warn("Email already in use: {}", request.getEmail());
      throw new IllegalArgumentException("Email already in use");
    }
    logger.debug("Email availability check passed for: {}", request.getEmail());

    User user = new User();
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    user.setTlf(request.getTlf());

    String token = UUID.randomUUID().toString();
    user.setConfirmationToken(token);
    user.setConfirmed(false);

    logger.debug("Created user entity with confirmation token for email: {}", request.getEmail());
    userRepository.save(user);
    logger.info("User registered successfully: {}", user.getEmail());

    try {
      emailService.sendConfirmationEmail(user.getEmail(), token);
      logger.info("Confirmation email sent to: {}", user.getEmail());
    } catch (Exception e) {
      logger.error("Failed to send confirmation email to {}: {}", user.getEmail(), e.getMessage());
    }
  }

  /**
   * Confirms a user's email address using the confirmation token.
   *
   * @param token the confirmation token
   */
  public void confirmUser(String token) {
    logger.info("Processing user confirmation with token");
    logger.debug("Confirmation token being validated: {}", token);

    User user = userRepository.findByConfirmationToken(token)
        .orElseThrow(() -> {
          logger.warn("Invalid confirmation token: {}", token);
          return new IllegalArgumentException("Invalid confirmation token");
        });

    logger.debug("Found user for confirmation token: {}", user.getEmail());
    user.setConfirmed(true);
    user.setConfirmationToken(null);
    userRepository.save(user);
    logger.info("User confirmed successfully: {}", user.getEmail());
  }


  /**
   * Logs in a user and returns a JWT token. If the user is an admin, it returns a flag indicating
   * that 2FA is required.
   *
   * @param request the login request containing email and password.
   * @return a LoginResponse containing the JWT token.
   * @throws IllegalArgumentException if the email or password is invalid, or if the account is
   *                                  locked.
   */
  public LoginResponse loginUser(LoginRequest request) throws IllegalArgumentException {
    String email = request.getEmail();
    logger.info("Processing login request for email: {}", email);

    // Check if the account is locked due to too many failed attempts
    if (loginAttemptService.isBlocked(email)) {
      logger.warn("Account locked due to too many failed attempts: {}", email);
      throw new IllegalArgumentException(
          "Account is locked. Please try again later or reset password.");
    }
    logger.debug("Account not locked, proceeding with authentication for: {}", email);

    // Find user by email if they exist
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> {
          logger.warn("User not found during login attempt: {}", email);
          loginAttemptService.loginFailed(email);
          return new IllegalArgumentException("Invalid email or password");
        });
    logger.debug("User found in database: {}", email);

    // Checks if typed password matches encrypted
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      logger.warn("Wrong password for user: {}", email);
      loginAttemptService.loginFailed(email);
      throw new IllegalArgumentException("Invalid email or password");
    }
    logger.debug("Password validated successfully for user: {}", email);

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );
      logger.debug("User authenticated by AuthenticationManager: {}", email);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // For admin users, return a flag indicating 2FA is required
      if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN) {
        logger.info("2FA required for admin user: {}", email);
        LoginResponse response = new LoginResponse(null);
        response.setRequires2Fa(true);
        return response;
      }

      String jwt = tokenProvider.generateToken(authentication);
      loginAttemptService.loginSucceeded(email);

      logger.info("User logged in successfully: {}", email);
      return new LoginResponse(jwt);

    } catch (Exception e) {
      logger.warn("Login failed for user {}: {}", email, e.getMessage());
      loginAttemptService.loginFailed(email);
      throw new IllegalArgumentException("Invalid email or password");
    }
  }

  /**
   * Verifies a 2FA code and completes the login for admin users.
   *
   * @param email   the user's email
   * @param otpCode the one-time password code
   * @return a LoginResponse with JWT token
   * @throws IllegalArgumentException if the code is invalid or user is not an admin
   */
  public LoginResponse verify2Fa(String email, String otpCode) {
    logger.info("Verifying 2FA for user: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("User not found during 2FA verification: {}", email);
          return new IllegalArgumentException("User not found");
        });
    logger.debug("User found for 2FA verification: {}", email);

    // Only admin users require 2FA
    if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPERADMIN) {
      logger.warn("2FA attempted for non-admin user: {}", email);
      throw new IllegalArgumentException("2FA not required for this user");
    }
    logger.debug("User role verified for 2FA: {}", email);

    // Verify OTP code
    boolean isValidOtp = twoFactorService.verifyOtp(email, otpCode);
    if (!isValidOtp) {
      logger.warn("Invalid 2FA code provided for user: {}", email);
      loginAttemptService.loginFailed(email);
      throw new IllegalArgumentException("Invalid verification code");
    }
    logger.debug("2FA code verified successfully for user: {}", email);

    // Create authentication with the user's role as the authority
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        email, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);
    logger.debug("Authentication context set for user: {}", email);

    String jwt = tokenProvider.generateToken(authentication);
    loginAttemptService.loginSucceeded(email);

    logger.info("2FA verification successful, user logged in: {}", email);
    return new LoginResponse(jwt);
  }

  /**
   * Initiates the password reset process by generating a token and sending a reset email.
   *
   * @param email the user's email address
   */
  public void initiatePasswordReset(String email) {
    logger.info("Initiating password reset for email: {}", email);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("Password reset requested for non-existent email: {}", email);
          return new IllegalArgumentException("No user registered with that email.");
        });
    logger.debug("User found for password reset: {}", email);

    if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN) {
      logger.warn("Password reset through this link is not allowed for admin users: {}", email);
      throw new IllegalArgumentException(
          "Admin brukere må resette passord via link fra superadmin");
    }

    String token = UUID.randomUUID().toString();
    Date expiration = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));
    logger.debug("Generated password reset token with 1 hour expiration for user: {}", email);

    user.setResetPasswordToken(token);
    user.setResetPasswordTokenExpiration(expiration);
    userRepository.save(user);
    logger.debug("Password reset token saved to database for user: {}", email);

    try {
      emailService.sendPasswordResetEmail(user.getEmail(), token);
      logger.info("Password reset email sent to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send password reset email to {}: {}", email, e.getMessage());
    }
  }

  /**
   * Initiates the password reset process for admin users.
   *
   * @param email the admin user's email address
   */
  public void initiateAdminPasswordReset(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> {
          logger.warn("Admin reset requested for non-existent email: {}", email);
          return new IllegalArgumentException("No user registered with that email.");
        });

    if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPERADMIN) {
      logger.warn("Attempted to reset password for non-admin user via admin flow: {}", email);
      throw new IllegalArgumentException("Only admin users can be reset using this method.");
    }

    String token = UUID.randomUUID().toString();
    Date expiration = Date.from(Instant.now().plus(1, ChronoUnit.HOURS));

    user.setResetPasswordToken(token);
    user.setResetPasswordTokenExpiration(expiration);
    userRepository.save(user);

    try {
      emailService.sendPasswordResetEmail(user.getEmail(), token);
      logger.info("Admin password reset email sent to: {}", email);
    } catch (Exception e) {
      logger.error("Failed to send admin reset email to {}: {}", email, e.getMessage());
    }
  }

  /**
   * Validates the reset password token.
   *
   * @param token the reset password token
   */
  public void validateResetPasswordToken(String token) {
    logger.info("Validating password reset token");
    logger.debug("Reset token being validated: {}", token);

    User user = userRepository.findByResetPasswordToken(token)
        .orElseThrow(() -> {
          logger.warn("Invalid reset token received: {}", token);
          return new IllegalArgumentException("Invalid token");
        });
    logger.debug("Found user for reset token: {}", user.getEmail());

    if (user.getResetPasswordTokenExpiration().before(new Date())) {
      logger.warn("Reset token expired for user: {}", user.getEmail());
      throw new IllegalArgumentException("Token expired");
    }
    logger.info("Reset token validated successfully for user: {}", user.getEmail());
  }

  /**
   * Resets the user's password using the provided token and new password.
   *
   * @param token       the reset password token
   * @param newPassword the new password
   */
  public void resetPassword(String token, String newPassword) {
    logger.info("Processing password reset with token");
    logger.debug("Reset token being processed: {}", token);

    User user = userRepository.findByResetPasswordToken(token)
        .orElseThrow(() -> {
          logger.warn("Invalid reset token received: {}", token);
          return new IllegalArgumentException("Invalid token");
        });
    logger.debug("Found user for reset token: {}", user.getEmail());

    if (user.getResetPasswordTokenExpiration().before(new Date())) {
      logger.warn("Reset token expired for user: {}", user.getEmail());
      throw new IllegalArgumentException("Token expired");
    }
    logger.debug("Reset token is valid and not expired for user: {}", user.getEmail());

    user.setPassword(passwordEncoder.encode(newPassword));
    user.setResetPasswordToken(null);
    user.setResetPasswordTokenExpiration(null);

    userRepository.save(user);
    logger.info("Password successfully reset for user: {}", user.getEmail());
  }
}