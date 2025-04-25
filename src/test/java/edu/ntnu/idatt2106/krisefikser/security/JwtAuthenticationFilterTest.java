package edu.ntnu.idatt2106.krisefikser.security;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

class JwtAuthenticationFilterTest {

  @InjectMocks
  private JwtAuthenticationFilter jwtAuthenticationFilter;

  @Mock
  private JwtTokenProvider tokenProvider;

  @Mock
  private UserDetailsService userDetailsService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private UserDetails userDetails;

  @Mock
  private SecurityContext securityContext;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterInternal_withValidToken_shouldSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    String token = "valid.jwt.token";
    when(tokenProvider.resolveToken(request)).thenReturn(token);
    when(tokenProvider.validateToken(token)).thenReturn(true);
    when(tokenProvider.getUsernameFromToken(token)).thenReturn("test@example.com");
    when(userDetailsService.loadUserByUsername("test@example.com")).thenReturn(userDetails);
    when(userDetails.getAuthorities()).thenReturn(java.util.Collections.emptyList());

    // Mock the SecurityContext to verify authentication is set
    SecurityContextHolder.setContext(securityContext);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(securityContext, times(1)).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withNullToken_shouldNotSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    when(tokenProvider.resolveToken(request)).thenReturn(null);

    SecurityContextHolder.setContext(securityContext);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void doFilterInternal_withInvalidToken_shouldNotSetAuthentication()
      throws ServletException, IOException {
    // Arrange
    String token = "invalid.jwt.token";
    when(tokenProvider.resolveToken(request)).thenReturn(token);
    when(tokenProvider.validateToken(token)).thenReturn(false);

    SecurityContextHolder.setContext(securityContext);

    // Act
    jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(securityContext, never()).setAuthentication(any());
    verify(filterChain).doFilter(request, response);
    verify(tokenProvider, never()).getUsernameFromToken(anyString());
  }
}