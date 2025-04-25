package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

class AuthControllerTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void authenticateUser_shouldReturnToken() {
    // Arrange
    LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(authentication);
    when(tokenProvider.generateToken(authentication)).thenReturn("test-jwt-token");

    // Act
    ResponseEntity<?> response = authController.authenticateUser(loginRequest);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    LoginResponse loginResponse = (LoginResponse) response.getBody();
    assertEquals("test-jwt-token", loginResponse.getToken());
    assertEquals("Bearer", loginResponse.getType());
  }
}