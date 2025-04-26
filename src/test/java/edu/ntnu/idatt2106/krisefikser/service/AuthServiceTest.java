package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import java.util.Optional;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
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
  }
}