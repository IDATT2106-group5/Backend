package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
  private final AuthenticationManager authenticationManager;
  private final JwtTokenProvider tokenProvider;
  private final EmailService emailService;

  public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService
                     AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.emailService = emailService;
    this.authenticationManager = authenticationManager;
    this.tokenProvider = tokenProvider;
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
   * Handles the login process for a user.
   *
   * @param request The login request containing the user's email and password.
   * @return        A {@link LoginResponse} containing a JWT token if the login is successful.
   * @throws IllegalArgumentException If the user is not found or the password is incorrect.
   */
  @PostMapping("/login")
  public LoginResponse loginUser(LoginRequest request) throws IllegalArgumentException {
    // Find user by email if they exist
    User user = userRepository.findByEmail(request.getEmail())
                              .orElseThrow(() -> {
                                logger.warn("User not found during login attempt: {}", request.getEmail());
                                throw new IllegalArgumentException("No user found with that email");
                              });
    
    if (user.getPassword() == passwordEncoder.encode(request.getPassword())) {
      logger.info("User logged in successfully: {}", user.getEmail());

      // Authenticate the user
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
          )
      );

      // Set the authentication object in the security context
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Generate JWT token
      String jwt = tokenProvider.generateToken(authentication);

      return new LoginResponse(jwt);
    } else {
      logger.warn("Wrong password for user: {}", request.getEmail());
      throw new IllegalArgumentException("Wrong password for user");
    }
  };
};
