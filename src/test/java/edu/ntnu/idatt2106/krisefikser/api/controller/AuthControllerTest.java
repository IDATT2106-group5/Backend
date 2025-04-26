package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Map;

import edu.ntnu.idatt2106.krisefikser.api.dto.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

class AuthControllerTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private AuthService authService;

  @InjectMocks
  private AuthController authController;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  class AuthenticateUserTests {

    @Test
    void shouldReturnToken() {
      // Arrange
      LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
      LoginResponse loginResponse = new LoginResponse("test-jwt-token");
      when(authService.loginUser(any(LoginRequest.class))).thenReturn(loginResponse);

      // Act
      ResponseEntity<Map<String, String>> response = authController.authenticateUser(loginRequest);

      // Assert
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(loginResponse.getToken(), response.getBody().get("token"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrown() {
      // Arrange
      String errorMessage = "Invalid credentials"; 
      LoginRequest loginRequest = new LoginRequest("invalid@example.com", "wrongpassword");
      when(authService.loginUser(any(LoginRequest.class)))
        .thenThrow(new IllegalArgumentException(errorMessage));

      // Act
      ResponseEntity<Map<String, String>> response = authController.authenticateUser(loginRequest);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void shouldReturnServerError_whenGenericExceptionThrown() {
      // Arrange
      LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
      when(authService.loginUser(any(LoginRequest.class)))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act
      ResponseEntity<Map<String, String>> response = authController.authenticateUser(loginRequest);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNotNull(response.getBody());
    }

    @Test
    void shouldHandleNullLoginRequest() {
        // Act
        ResponseEntity<Map<String, String>> response = authController.authenticateUser(null);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Request object is null", response.getBody().get("error"));
    }

    @Test
    void shouldValidateRequestParameters() {
      // Arrange
      LoginRequest emptyRequest = new LoginRequest("", "");
      when(authService.loginUser(any(LoginRequest.class)))
        .thenThrow(new IllegalArgumentException("Email and password cannot be empty"));

      // Act
      ResponseEntity<Map<String, String>> response = authController.authenticateUser(emptyRequest);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
  }
}
