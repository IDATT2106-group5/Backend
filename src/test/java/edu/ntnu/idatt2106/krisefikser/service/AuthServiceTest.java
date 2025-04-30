package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.LoginResponse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;


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
  private Authentication authentication;

  @InjectMocks
  private AuthService authService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class LoginUserTests {

    @Test
    void loginUser_shouldReturnToken_whenCredentialsValid() {
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
      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verify(tokenProvider).generateToken(authentication);
    }

    @Test
    void loginUser_shouldThrowException_whenUserNotFound() {
      // Arrange
      String email = "nonexistent@example.com";
      String password = "password";

      LoginRequest loginRequest = new LoginRequest(email, password);

      when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("No user found with that email", exception.getMessage());
      verify(userRepository).findByEmail(email);
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

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.loginUser(loginRequest));

      assertEquals("Wrong password for user", exception.getMessage());
      verify(userRepository).findByEmail(email);
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

      when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
      when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

      when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
          .thenThrow(new RuntimeException("Authentication failed"));

      // Act & Assert
      assertThrows(RuntimeException.class, () -> authService.loginUser(loginRequest));

      verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
      verifyNoInteractions(tokenProvider);
    }

    @Test
    void registerUser_shouldRegisterUser_whenEmailNotExists() {
      // Arrange
      String email = "newuser@example.com";
      String password = "password";
      String encodedPassword = "encodedPassword";
      String fullName = "New User";
      String tlf = "12345678";

      RegisterRequestDto registerRequest = new RegisterRequestDto(fullName, email, password, tlf);

      when(userRepository.existsByEmail(email)).thenReturn(false);
      when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

      // Act
      authService.registerUser(registerRequest);

      // Assert
      verify(userRepository).existsByEmail(email);
      verify(passwordEncoder).encode(password);
      verify(userRepository).save(any(User.class));
      verify(emailService).sendConfirmationEmail(any(String.class), any(String.class));
    }

    @Test
    void registerUser_shouldThrowException_whenEmailAlreadyExists() {
      // Arrange
      String email = "existinguser@example.com";
      String password = "password";
      String fullName = "Existing User";
      String tlf = "12345678";

      RegisterRequestDto registerRequest = new RegisterRequestDto(fullName, email, password, tlf);

      when(userRepository.existsByEmail(email)).thenReturn(true);

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.registerUser(registerRequest));

      assertEquals("Email already in use", exception.getMessage());

      verify(userRepository).existsByEmail(email);
      verifyNoInteractions(passwordEncoder);
      verify(userRepository).existsByEmail(email);
      verifyNoMoreInteractions(userRepository);
      verifyNoInteractions(emailService);
    }

    @Test
    void confirmUser_shouldConfirmUser_whenTokenIsValid() {
      // Arrange
      String token = "valid-token";
      User user = new User();
      user.setConfirmed(false);
      user.setConfirmationToken(token);

      when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.of(user));

      // Act
      authService.confirmUser(token);

      // Assert
      assertTrue(user.isConfirmed());
      assertNull(user.getConfirmationToken());
      verify(userRepository).findByConfirmationToken(token);
      verify(userRepository).save(user);
    }

    @Test
    void confirmUser_shouldThrowException_whenTokenIsInvalid() {
      // Arrange
      String token = "invalid-token";

      when(userRepository.findByConfirmationToken(token)).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
          () -> authService.confirmUser(token));

      assertEquals("Invalid confirmation token", exception.getMessage());
      verify(userRepository).findByConfirmationToken(token);
      verifyNoMoreInteractions(userRepository);
    }
  }
}