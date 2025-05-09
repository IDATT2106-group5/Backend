package edu.ntnu.idatt2106.krisefikser.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for the JwtTokenProvider class.
 */

class JwtTokenProviderTest {

  @InjectMocks
  private JwtTokenProvider tokenProvider;

  @Mock
  private Authentication authentication;

  @Mock
  private HttpServletRequest request;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    ReflectionTestUtils.setField(tokenProvider, "jwtSecret",
        "testSecret123456789012345678901234567890");
    ReflectionTestUtils.setField(tokenProvider, "jwtExpirationMs", 3600000L);
    tokenProvider.init();
  }

  @Test
  void generateToken_shouldCreateValidToken() {
    // Arrange
    when(authentication.getName()).thenReturn("test@example.com");

    // Act
    String token = tokenProvider.generateToken(authentication);

    // Assert
    assertNotNull(token);
    assertTrue(tokenProvider.validateToken(token));
    assertEquals("test@example.com", tokenProvider.getUsernameFromToken(token));
  }

  @Test
  void validateToken_shouldReturnFalseForInvalidToken() {
    // Act & Assert
    assertFalse(tokenProvider.validateToken("invalid.token.string"));
  }

  @Test
  void resolveToken_shouldExtractTokenFromHeader() {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");

    // Act
    String token = tokenProvider.resolveToken(request);

    // Assert
    assertEquals("valid-token", token);
  }

  @Test
  void resolveToken_shouldReturnNullWhenNoAuthHeader() {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn(null);

    // Act
    String token = tokenProvider.resolveToken(request);

    // Assert
    assertNull(token);
  }

  @Test
  void resolveToken_shouldReturnNullWhenInvalidAuthHeader() {
    // Arrange
    when(request.getHeader("Authorization")).thenReturn("Invalid header");

    // Act
    String token = tokenProvider.resolveToken(request);

    // Assert
    assertNull(token);
  }
}