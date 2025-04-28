package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import java.util.Collections;
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
      LoginAttemptService loginAttemptService, TwoFactorService twoFactorService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
    this.loginAttemptService = loginAttemptService;
    this.twoFactorService = twoFactorService;
  }

  public boolean validatePassword(String password) {
    return PASSWORD_PATTERN.matcher(password).matches();
  }

  /**
   * Registers a new user.
   *
   * @param request the user to register
   */
  public void registerUser(RegisterRequestDto request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      logger.warn("Email already in use: {}", request.getEmail());
      throw new IllegalArgumentException("Email already in use");
    }

    User user = new User();
    user.setFullName(request.getFullName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setRole(Role.USER);
    user.setTlf(request.getTlf());

    String token = UUID.randomUUID().toString();
    user.setConfirmationToken(token);
    user.setConfirmed(false);

    userRepository.save(user);
    logger.info("User registered successfully: {}", user.getEmail());

    emailService.sendConfirmationEmail(user.getEmail(), token);
  }

  /**
   * Confirms a user's email address using the confirmation token.
   *
   * @param token the confirmation token
   */
  public void confirmUser(String token) {
    User user = userRepository.findByConfirmationToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid confirmation token"));

    user.setConfirmed(true);
    user.setConfirmationToken(null);
    userRepository.save(user);
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

    // Check if the account is locked due to too many failed attempts
    if (loginAttemptService.isBlocked(email)) {
      logger.warn("Account locked due to too many failed attempts: {}", email);
      throw new IllegalArgumentException(
          "Account is locked. Please try again later or reset password.");
    }

    // Find user by email if they exist
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> {
          logger.warn("User not found during login attempt: {}", email);
          loginAttemptService.loginFailed(email);
          throw new IllegalArgumentException("Invalid email or password");
        });

    // Checks if typed password matches encrypted
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      logger.warn("Wrong password for user: {}", email);
      loginAttemptService.loginFailed(email);
      throw new IllegalArgumentException("Invalid email or password");
    }

    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // For admin users, return a flag indicating 2FA is required
      if (user.getRole() == Role.ADMIN || user.getRole() == Role.SUPERADMIN) {
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
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    // Only admin users require 2FA
    if (user.getRole() != Role.ADMIN && user.getRole() != Role.SUPERADMIN) {
      throw new IllegalArgumentException("2FA not required for this user");
    }

    // Verify OTP code
    boolean isValidOtp = twoFactorService.verifyOtp(email, otpCode);
    if (!isValidOtp) {
      loginAttemptService.loginFailed(email);
      throw new IllegalArgumentException("Invalid verification code");
    }

    // Create authentication with the user's role as the authority
    List<GrantedAuthority> authorities = Collections.singletonList(
        new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

    Authentication authentication = new UsernamePasswordAuthenticationToken(
        email, null, authorities);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String jwt = tokenProvider.generateToken(authentication);
    loginAttemptService.loginSucceeded(email);

    return new LoginResponse(jwt);
  }
}
