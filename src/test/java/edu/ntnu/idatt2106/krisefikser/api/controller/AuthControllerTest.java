package edu.ntnu.idatt2106.krisefikser.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.controller.auth.AuthController;
import edu.ntnu.idatt2106.krisefikser.api.dto.PasswordResetRequestDto;
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

/**
 * Tests for the AuthController class.
 */
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

  /**
   * Tests for the user registration functionality.
   */
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

  /**
   * Tests for the email confirmation functionality.
   */
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

  /**
   * Tests for the password reset request functionality.
   */
  @Nested
  class RequestPasswordResetTests {

    @Test
    void shouldSendPasswordResetLink_whenEmailIsValid() {
      // Arrange
      String email = "reset@example.com";
      Map<String, String> request = Map.of("email", email);

      // Act
      ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Password reset link sent to your email", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenEmailIsMissing() {
      // Arrange
      Map<String, String> request = Map.of();

      // Act
      ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Email is required", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentThrown() {
      // Arrange
      String email = "notfound@example.com";
      Map<String, String> request = Map.of("email", email);
      doThrow(new IllegalArgumentException("No user registered with that email."))
          .when(authService).initiatePasswordReset(email);

      // Act
      ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("No user registered with that email.", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      // Arrange
      String email = "test@example.com";
      Map<String, String> request = Map.of("email", email);
      doThrow(new RuntimeException("Unexpected error"))
          .when(authService).initiatePasswordReset(email);

      // Act
      ResponseEntity<Map<String, String>> response = authController.requestPasswordReset(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Tests for the password reset token validation functionality.
   */
  @Nested
  class ValidateResetTokenTests {

    @Test
    void shouldReturnOk_whenTokenIsValid() {
      // Arrange
      String token = "valid-token";
      Map<String, String> request = Map.of("token", token);

      // Act
      ResponseEntity<Map<String, String>> response = authController.validateResetToken(request);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Token is valid", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenTokenIsMissing() {
      // Arrange
      Map<String, String> request = Map.of();

      // Act
      ResponseEntity<Map<String, String>> response = authController.validateResetToken(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Reset token is required", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequest_whenTokenIsInvalid() {
      // Arrange
      String token = "invalid-token";
      Map<String, String> request = Map.of("token", token);

      doThrow(new IllegalArgumentException("Invalid token"))
          .when(authService).validateResetPasswordToken(token);

      // Act
      ResponseEntity<Map<String, String>> response = authController.validateResetToken(request);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid token", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      // Arrange
      String token = "token";
      Map<String, String> request = Map.of("token", token);

      doThrow(new RuntimeException("Something went wrong"))
          .when(authService).validateResetPasswordToken(token);

      // Act
      ResponseEntity<Map<String, String>> response = authController.validateResetToken(request);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

  /**
   * Tests for the password reset functionality.
   */
  @Nested
  class ResetPasswordEndpointTests {

    @Test
    void shouldResetPasswordSuccessfully_whenTokenAndPasswordAreValid() {
      // Arrange
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto("valid-token",
          "NewPass123!");

      // Act
      ResponseEntity<Map<String, String>> response = authController.resetPassword(requestDto);

      // Assert
      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertEquals("Password reset successful", response.getBody().get("message"));
    }

    @Test
    void shouldReturnBadRequest_whenTokenIsMissing() {
      // Arrange
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto(null, "NewPass123!");

      // Act
      ResponseEntity<Map<String, String>> response = authController.resetPassword(requestDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Reset token is required", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequest_whenPasswordIsMissing() {
      // Arrange
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto("valid-token", null);

      // Act
      ResponseEntity<Map<String, String>> response = authController.resetPassword(requestDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("New password is required", response.getBody().get("error"));
    }

    @Test
    void shouldReturnBadRequest_whenIllegalArgumentThrown() {
      // Arrange
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto("invalid-token",
          "NewPass123!");
      doThrow(new IllegalArgumentException("Invalid token"))
          .when(authService).resetPassword("invalid-token", "NewPass123!");

      // Act
      ResponseEntity<Map<String, String>> response = authController.resetPassword(requestDto);

      // Assert
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertEquals("Invalid token", response.getBody().get("error"));
    }

    @Test
    void shouldReturnInternalServerError_whenUnexpectedExceptionThrown() {
      // Arrange
      PasswordResetRequestDto requestDto = new PasswordResetRequestDto("token", "NewPass123!");
      doThrow(new RuntimeException("Unexpected failure"))
          .when(authService).resetPassword("token", "NewPass123!");

      // Act
      ResponseEntity<Map<String, String>> response = authController.resetPassword(requestDto);

      // Assert
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertEquals("Internal server error", response.getBody().get("error"));
    }
  }

}
