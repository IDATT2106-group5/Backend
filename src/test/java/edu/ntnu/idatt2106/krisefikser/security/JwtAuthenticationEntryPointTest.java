package edu.ntnu.idatt2106.krisefikser.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;

/**
 * Unit tests for the JwtAuthenticationEntryPoint class.
 */

class JwtAuthenticationEntryPointTest {

  @InjectMocks
  private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

  @Mock
  private HttpServletRequest request;

  @Mock
  private AuthenticationException authException;

  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    response = new MockHttpServletResponse();
  }

  @Test
  void commence_shouldReturn401WithErrorMessage() throws IOException, ServletException {
    // Arrange
    when(authException.getMessage()).thenReturn("Access Denied");

    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertEquals("application/json", response.getContentType());

    String responseContent = response.getContentAsString();
    assertTrue(responseContent.contains("\"error\": \"Unauthorized\""));
    assertTrue(responseContent.contains("\"message\": \"Access Denied\""));
  }

  @Test
  void commence_shouldHandleNullMessage() throws IOException, ServletException {
    // Arrange
    when(authException.getMessage()).thenReturn(null);

    // Act
    jwtAuthenticationEntryPoint.commence(request, response, authException);

    // Assert
    assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
    assertEquals("application/json", response.getContentType());

    String responseContent = response.getContentAsString();
    assertTrue(responseContent.contains("\"error\": \"Unauthorized\""));
    assertTrue(responseContent.contains("\"message\": \"null\""));
  }
}