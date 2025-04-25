package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for handling authentication-related operations.
 */
@Service
public class AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      EmailService emailService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
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
}
