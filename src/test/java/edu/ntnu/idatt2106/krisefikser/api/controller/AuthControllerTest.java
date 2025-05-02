package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginRequest;
import edu.ntnu.idatt2106.krisefikser.api.dto.auth.LoginResponse;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.RegisterRequestDto;
import edu.ntnu.idatt2106.krisefikser.security.JwtTokenProvider;
import edu.ntnu.idatt2106.krisefikser.service.AuthService;
import java.util.Map;
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

  @Nested
  class RegisterUserTests {

    @Test
    void shouldRegisterUserSuccessfully() {
      // Arrange
      RegisterRequestDto registerRequest = new RegisterRequestDto("John Doe", "john@example.com",
          "password123", "12345678");

      // Act
      ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

      // Assert
      assertEquals(HttpStatus.CREATED, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("User registered successfully", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentExceptionThrownDuringRegister() {
      // Arrange
      RegisterRequestDto registerRequest = new RegisterRequestDto("Jane Doe", "jane@example.com",
          "password123", "87654321");
      String errorMessage = "Email already in use";

      doThrow(new IllegalArgumentException(errorMessage))
          .when(authService)
          .registerUser(any(RegisterRequestDto.class));

      // Act
      ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals(errorMessage, response.getBody().get("error"));
    }

    @Test
    void shouldReturnServerError_whenUnexpectedExceptionThrownDuringRegister() {
      // Arrange
      RegisterRequestDto registerRequest = new RegisterRequestDto("Jake Smith", "jake@example.com",
          "password123", "87651234");

      doThrow(new RuntimeException("Unexpected database error"))
          .when(authService)
          .registerUser(any(RegisterRequestDto.class));

      // Act
      ResponseEntity<Map<String, String>> response = authController.register(registerRequest);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  @Nested
  class ConfirmEmailTests {

    @Test
    void shouldRedirectToSuccess_whenTokenIsValid() {
      // Arrange
      String token = "valid-token";

      // Act
      ResponseEntity<Map<String, String>> response = authController.confirmEmail(token);

      // Assert
      assertEquals(HttpStatus.FOUND, response.getStatusCode());
      assertEquals("http://localhost:5173/register-success",
          response.getHeaders().getLocation().toString());
    }

    @Test
    void shouldRedirectToFailed_whenTokenIsInvalid() {
      // Arrange
      String token = "invalid-token";

      doThrow(new IllegalArgumentException("Invalid token"))
          .when(authService)
          .confirmUser(token);

      // Act
      ResponseEntity<Map<String, String>> response = authController.confirmEmail(token);

      // Assert
      assertEquals(HttpStatus.FOUND, response.getStatusCode());
      assertEquals("http://localhost:5173/register-failed",
          response.getHeaders().getLocation().toString());
    }
  }
}
