package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Tests for the AuthService class.
 */
class AuthServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private EmailService emailService;

  @Mock
  private CaptchaService captchaService;

  @Mock
  private LoginAttemptService loginAttemptService;

  @Mock
  private TwoFactorService twoFactorService;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void validatePassword_shouldReturnTrue_whenPasswordMeetsRequirements() {
    // Valid password with number, letter and special char
    assertTrue(authService.validatePassword("Password1!"));

    // Valid password with all required elements
    assertTrue(authService.validatePassword("Abcdef1@"));

    // Valid complex password
    assertTrue(authService.validatePassword("C0mpl3x@P4ssw0rd!"));
  }

  @Test
  void validatePassword_shouldReturnFalse_whenPasswordDoesNotMeetRequirements() {
    // Missing number
    assertFalse(authService.validatePassword("Password!"));

    // Missing special character
    assertFalse(authService.validatePassword("Password123"));

    // Missing letter
    assertFalse(authService.validatePassword("12345678!"));

    // Too short
    assertFalse(authService.validatePassword("Pwd1!"));

    // With space
    assertFalse(authService.validatePassword("Password 123!"));
  }

  @Test
  void confirmUser_shouldConfirmUser_whenTokenValid() {
    // Arrange
    String token = UUID.randomUUID().toString();
    User user = new User();
    user.setEmail("test@example.com");
    user.setConfirmed(false);
    user.setConfirmationToken(token);

    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

    // Act
    authService.confirmUser(token);

    // Assert
    assertTrue(user.isConfirmed());
    assertNull(user.getConfirmationToken());
    verify(userRepository).save(user);
  }

  @Test
  void confirmUser_shouldThrowException_whenTokenInvalid() {
    // Arrange
    String token = "invalidToken";
    when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> authService.confirmUser(token));
    verify(userRepository).findByConfirmationToken(token);
    verifyNoMoreInteractions(userRepository);
  }

  @Nested
  class RegisterUserTests {

    @Test
    void registerUser_shouldCreateUser_whenValidData() {
      // Arrange
      RegisterRequestDto request = new RegisterRequestDto("Test User", "test@example.com",
          "Password123!", "12345678");
      request.setHCaptchaToken("validToken");

      when(userRepository.existsByEmail(anyString())).thenReturn(false);
      when(captchaService.verifyToken(anyString())).thenReturn(true);

      // Act
      authService.registerUser(request);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertEquals("Test User", savedUser.getFullName());
      assertEquals("test@example.com", savedUser.getEmail());
      assertEquals("12345678", savedUser.getTlf());
      assertEquals(Role.USER, savedUser.getRole());
      assertFalse(savedUser.isConfirmed());
      assertNotNull(savedUser.getConfirmationToken());

      verify(emailService).sendConfirmationEmail(eq("test@example.com"), anyString());
    }

    @Test
    void registerUser_shouldThrowException_whenCaptchaInvalid() {
      // Arrange
      RegisterRequestDto request = new RegisterRequestDto("Test User", "test@example.com",
          "Password123!", "12345678");
      request.setHCaptchaToken("invalidToken");

      when(captchaService.verifyToken("invalidToken")).thenReturn(false);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.registerUser(request));

      assertEquals("hCaptcha verification failed. Please try again.", exception.getMessage());
      verify(captchaService).verifyToken("invalidToken");
      verifyNoInteractions(userRepository);
      verifyNoInteractions(emailService);
    }

    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
      // Arrange
      RegisterRequestDto request = new RegisterRequestDto("Test User", "existing@example.com",
          "Password123!", "12345678");
      request.setHCaptchaToken("validToken");

      when(captchaService.verifyToken(anyString())).thenReturn(true);
      when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.registerUser(request));

      assertEquals("Email already in use", exception.getMessage());
      verify(userRepository).existsByEmail("existing@example.com");
      verifyNoMoreInteractions(userRepository);
      verifyNoInteractions(emailService);
    }
  }

  @Nested
  class LoginUserTests {

    @Test
    void loginUser_shouldReturnToken_whenCredentialsValidAndNotAdmin() {
      // Arrange
      String email = "test@example.com";
      String password = "password";
      String encodedPassword = "encodedPassword";
      String jwtToken = "test.jwt.token";

      User user = new User();
      user.setEmail(email);
      user.setPassword(encodedPassword);
      user.setRole(Role.USER);

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);
      when(tokenProvider.generateToken(authentication)).thenReturn(jwtToken);

      // Act
      LoginResponse response = authService.loginUser(loginRequest);

      // Assert
      assertNotNull(response);
      assertEquals(jwtToken, response.getToken());
      assertFalse(response.isRequires2Fa());
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(tokenProvider).generateToken(authentication);
      verify(loginAttemptService).loginSucceeded(email);
    }

    @Test
    void loginUser_shouldRequire2Fa_whenUserIsAdmin() {
      // Arrange
      String email = "admin@example.com";
      String password = "password";
      String encodedPassword = "encodedPassword";

      User user = new User();
      user.setEmail(email);
      user.setPassword(encodedPassword);
      user.setRole(Role.ADMIN);

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);

      // Act
      LoginResponse response = authService.loginUser(loginRequest);

      // Assert
      assertNotNull(response);
      assertNull(response.getToken());
      assertTrue(response.isRequires2Fa());
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void loginUser_shouldRequire2Fa_whenUserIsSuperAdmin() {
      // Arrange
      String email = "superadmin@example.com";
      String password = "password";
      String encodedPassword = "encodedPassword";

      User user = new User();
      user.setEmail(email);
      user.setPassword(encodedPassword);
      user.setRole(Role.SUPERADMIN);

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenReturn(authentication);

      // Act
      LoginResponse response = authService.loginUser(loginRequest);

      // Assert
      assertNotNull(response);
      assertNull(response.getToken());
      assertTrue(response.isRequires2Fa());
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void loginUser_shouldThrowException_whenAccountIsLocked() {
      // Arrange
      String email = "locked@example.com";
      String password = "password";

      LoginRequest loginRequest = new LoginRequest(email, password);
      when(loginAttemptService.isBlocked(email)).thenReturn(true);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("Account is locked. Please try again later or reset password.",
          exception.getMessage());
      verify(loginAttemptService).isBlocked(email);
      verifyNoMoreInteractions(loginAttemptService);
      verifyNoInteractions(userRepository);
      verifyNoInteractions(authenticationManager);
    }

    @Test
    void loginUser_shouldThrowException_whenUserNotFound() {
      // Arrange
      String email = "nonexistent@example.com";
      String password = "password";

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("Invalid email or password", exception.getMessage());
      verify(userRepository).findByEmail(email);
      verify(loginAttemptService).loginFailed(email);
      verifyNoInteractions(authenticationManager);
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void loginUser_shouldThrowException_whenPasswordIncorrect() {
      // Arrange
      String email = "test@example.com";
      String password = "wrongPassword";
      String encodedPassword = "encodedPassword";

      User user = new User();
      user.setEmail(email);
      user.setPassword(encodedPassword);

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("Invalid email or password", exception.getMessage());
      verify(userRepository).findByEmail(email);
      verify(loginAttemptService).loginFailed(email);
      verifyNoInteractions(authenticationManager);
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void loginUser_shouldThrowException_whenAuthenticationFails() {
      // Arrange
      String email = "test@example.com";
      String password = "password";
      String encodedPassword = "encodedPassword";

      User user = new User();
      user.setEmail(email);
      user.setPassword(encodedPassword);

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(loginAttemptService.isBlocked(email)).thenReturn(false);
      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(new RuntimeException("Authentication failed"));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("Invalid email or password", exception.getMessage());
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(loginAttemptService).loginFailed(email);
      verifyNoInteractions(tokenProvider);
    }
  }

  @Nested
  class TwoFactorAuthTests {

    @Test
    void verify2Fa_shouldReturnToken_whenOtpValid() {
      // Arrange
      String email = "admin@example.com";
      String otpCode = "123456";
      String jwtToken = "test.jwt.token";

      User user = new User();
      user.setEmail(email);
      user.setRole(Role.ADMIN);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(twoFactorService.verifyOtp(email, otpCode)).thenReturn(true);
      when(tokenProvider.generateToken(any(Authentication.class))).thenReturn(jwtToken);

      // Act
      LoginResponse response = authService.verify2Fa(email, otpCode);

      // Assert
      assertNotNull(response);
      assertEquals(jwtToken, response.getToken());
      verify(twoFactorService).verifyOtp(email, otpCode);
      verify(tokenProvider).generateToken(any(Authentication.class));
      verify(loginAttemptService).loginSucceeded(email);
    }

    @Test
    void verify2Fa_shouldThrowException_whenUserNotFound() {
      // Arrange
      String email = "nonexistent@example.com";
      String otpCode = "123456";

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.verify2Fa(email, otpCode));

      assertEquals("User not found", exception.getMessage());
      verifyNoInteractions(twoFactorService);
      verifyNoInteractions(tokenProvider);
    }
  }

  @Nested
  class Verify2FATests {

    @Test
    void verify2Fa_shouldThrowException_whenUserNotAdmin() {
      // Arrange
      String email = "user@example.com";
      String otpCode = "123456";

      User user = new User();
      user.setEmail(email);
      user.setRole(Role.USER);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.verify2Fa(email, otpCode));

      assertEquals("2FA not required for this user", exception.getMessage());
      verifyNoInteractions(twoFactorService);
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void verify2Fa_shouldThrowException_whenOtpInvalid() {
      // Arrange
      String email = "admin@example.com";
      String otpCode = "invalid";

      User user = new User();
      user.setEmail(email);
      user.setRole(Role.ADMIN);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(twoFactorService.verifyOtp(email, otpCode)).thenReturn(false);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.verify2Fa(email, otpCode));

      assertEquals("Invalid verification code", exception.getMessage());
      verify(twoFactorService).verifyOtp(email, otpCode);
      verify(loginAttemptService).loginFailed(email);
      verifyNoInteractions(tokenProvider);
    }
  }

  /**
   * Tests for the initiate password reset functionality.
   */
  @Nested
  class InitiatePasswordResetTests {

    private final String email = "user@example.com";
    private final String adminEmail = "admin@example.com";

    @Test
    void initiatePasswordReset_shouldGenerateTokenAndSendEmail_whenUserExists() {
      // Arrange
      User user = new User();
      user.setEmail(email);
      user.setRole(Role.USER);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

      // Act
      authService.initiatePasswordReset(email);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertNotNull(savedUser.getResetPasswordToken(), "Reset token should be generated");
      assertNotNull(savedUser.getResetPasswordTokenExpiration(), "Token expiration should be set");

      verify(emailService).sendPasswordResetEmail(eq(email), anyString());
      verify(userRepository).findByEmail(email);
    }

    @Test
    void initiatePasswordReset_shouldThrowException_whenUserDoesNotExist() {
      // Arrange
      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.initiatePasswordReset(email));

      assertEquals("No user registered with that email.", exception.getMessage());
      verify(userRepository).findByEmail(email);
      verifyNoInteractions(emailService);
    }

    @Test
    void initiatePasswordReset_shouldThrowException_whenUserIsAdmin() {
      // Arrange
      User adminUser = new User();
      adminUser.setEmail(adminEmail);
      adminUser.setRole(Role.ADMIN);

      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.initiatePasswordReset(adminEmail));

      assertEquals("Admin brukere må resette passord via link fra superadmin",
          exception.getMessage());
      verify(userRepository).findByEmail(adminEmail);
      verifyNoInteractions(emailService);
    }

    @Test
    void initiatePasswordReset_shouldSetTokenExpirationToOneHour() {
      // Arrange
      User user = new User();
      user.setEmail(email);
      user.setRole(Role.USER);

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

      // Act
      authService.initiatePasswordReset(email);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      Date expiration = savedUser.getResetPasswordTokenExpiration();

      long differenceInMillis = expiration.getTime() - System.currentTimeMillis();
      // Allow a small margin for test execution time
      assertTrue(differenceInMillis > 59 * 60 * 1000 && differenceInMillis <= 60 * 60 * 1000,
          "Token expiration should be set to approximately 1 hour in the future");
    }
  }

  @Nested
  class InitiateAdminPasswordResetTests {

    private final String adminEmail = "admin@example.com";
    private final String userEmail = "user@example.com";

    @Test
    void initiateAdminPasswordReset_shouldGenerateTokenAndSendEmail_whenAdminExists() {
      // Arrange
      User adminUser = new User();
      adminUser.setEmail(adminEmail);
      adminUser.setRole(Role.ADMIN);

      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));

      // Act
      authService.initiateAdminPasswordReset(adminEmail);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertNotNull(savedUser.getResetPasswordToken(), "Reset token should be generated");
      assertNotNull(savedUser.getResetPasswordTokenExpiration(), "Token expiration should be set");

      verify(emailService).sendPasswordResetEmail(eq(adminEmail), anyString());
      verify(userRepository).findByEmail(adminEmail);
    }

    @Test
    void initiateAdminPasswordReset_shouldAlsoWorkForSuperadmin() {
      // Arrange
      User superAdminUser = new User();
      superAdminUser.setEmail(adminEmail);
      superAdminUser.setRole(Role.SUPERADMIN);

      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(superAdminUser));

      // Act
      authService.initiateAdminPasswordReset(adminEmail);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertNotNull(savedUser.getResetPasswordToken(), "Reset token should be generated");

      verify(emailService).sendPasswordResetEmail(eq(adminEmail), anyString());
    }

    @Test
    void initiateAdminPasswordReset_shouldThrowException_whenUserDoesNotExist() {
      // Arrange
      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.initiateAdminPasswordReset(adminEmail));

      assertEquals("No user registered with that email.", exception.getMessage());
      verify(userRepository).findByEmail(adminEmail);
      verifyNoInteractions(emailService);
    }

    @Test
    void initiateAdminPasswordReset_shouldThrowException_whenUserIsNotAdmin() {
      // Arrange
      User regularUser = new User();
      regularUser.setEmail(userEmail);
      regularUser.setRole(Role.USER);

      when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(regularUser));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.initiateAdminPasswordReset(userEmail));

      assertEquals("Only admin users can be reset using this method.", exception.getMessage());
      verify(userRepository).findByEmail(userEmail);
      verifyNoInteractions(emailService);
    }

    @Test
    void initiateAdminPasswordReset_shouldSetTokenExpirationToOneHour() {
      // Arrange
      User adminUser = new User();
      adminUser.setEmail(adminEmail);
      adminUser.setRole(Role.ADMIN);

      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));

      // Act
      authService.initiateAdminPasswordReset(adminEmail);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      Date expiration = savedUser.getResetPasswordTokenExpiration();

      long differenceInMillis = expiration.getTime() - System.currentTimeMillis();
      // Allow a small margin for test execution time
      assertTrue(differenceInMillis > 59 * 60 * 1000 && differenceInMillis <= 60 * 60 * 1000,
          "Token expiration should be set to approximately 1 hour in the future");
    }

    @Test
    void initiateAdminPasswordReset_shouldHandleEmailSendingExceptions() {
      // Arrange
      User adminUser = new User();
      adminUser.setEmail(adminEmail);
      adminUser.setRole(Role.ADMIN);

      when(userRepository.findByEmail(adminEmail)).thenReturn(Optional.of(adminUser));

      // Fix: Use doThrow().when() syntax for void methods
      doThrow(new RuntimeException("Email service error"))
          .when(emailService).sendPasswordResetEmail(eq(adminEmail), anyString());

      // Act - should not throw exception even if email sending fails
      authService.initiateAdminPasswordReset(adminEmail);

      // Assert
      verify(userRepository).save(any(User.class));
      verify(emailService).sendPasswordResetEmail(eq(adminEmail), anyString());
    }
  }


  /**
   * Tests for the password reset token validation.
   */
  @Nested
  class ValidateResetPasswordTokenTests {

    @Test
    void validateResetPasswordToken_shouldPass_whenTokenIsValid() {
      // Arrange
      String token = UUID.randomUUID().toString();
      User user = new User();
      user.setResetPasswordToken(token);
      user.setResetPasswordTokenExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));

      // Act
      authService.validateResetPasswordToken(token);

      // Assert
      verify(userRepository).findByResetPasswordToken(token);
    }

    @Test
    void validateResetPasswordToken_shouldThrowException_whenTokenIsInvalid() {
      // Arrange
      String token = "invalid-token";
      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.validateResetPasswordToken(token));

      assertEquals("Invalid token", exception.getMessage());
      verify(userRepository).findByResetPasswordToken(token);
    }

    @Test
    void validateResetPasswordToken_shouldThrowException_whenTokenIsExpired() {
      // Arrange
      String token = UUID.randomUUID().toString();
      User user = new User();
      user.setResetPasswordToken(token);
      user.setResetPasswordTokenExpiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));

      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.validateResetPasswordToken(token));

      assertEquals("Token expired", exception.getMessage());
      verify(userRepository).findByResetPasswordToken(token);
    }
  }

  /**
   * Tests for the password reset functionality.
   */
  @Nested
  class ResetPasswordMethodTests {

    @Test
    void resetPassword_shouldUpdatePasswordAndClearToken_whenValidToken() {
      // Arrange
      String token = UUID.randomUUID().toString();
      String newPassword = "newSecurePassword123!";
      String encodedPassword = "encodedPassword123!";

      User user = new User();
      user.setEmail("test@example.com");
      user.setResetPasswordToken(token);
      user.setResetPasswordTokenExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)));

      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));
      when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

      // Act
      authService.resetPassword(token, newPassword);

      // Assert
      assertEquals(encodedPassword, user.getPassword());
      assertNull(user.getResetPasswordToken());
      assertNull(user.getResetPasswordTokenExpiration());
      verify(userRepository).save(user);
    }

    @Test
    void resetPassword_shouldThrowException_whenTokenIsInvalid() {
      // Arrange
      String token = "invalid-token";
      String newPassword = "newPassword!";
      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.resetPassword(token, newPassword));

      assertEquals("Invalid token", exception.getMessage());
      verify(userRepository).findByResetPasswordToken(token);
    }

    @Test
    void resetPassword_shouldThrowException_whenTokenIsExpired() {
      // Arrange
      String token = UUID.randomUUID().toString();
      String newPassword = "newPassword!";
      User user = new User();
      user.setEmail("expired@example.com");
      user.setResetPasswordToken(token);
      user.setResetPasswordTokenExpiration(Date.from(Instant.now().minus(1, ChronoUnit.HOURS)));

      when(userRepository.findByResetPasswordToken(token)).thenReturn(Optional.of(user));

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.resetPassword(token, newPassword));

      assertEquals("Token expired", exception.getMessage());
      verify(userRepository).findByResetPasswordToken(token);
    }
  }
}