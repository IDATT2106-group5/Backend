package edu.ntnu.idatt2106.krisefikser.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Returns a 401 Unauthorized response when authentication fails or is missing.
 *
 * @author Snake727
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException, ServletException {
    // Set response status and content type
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");

    // Craft a simple JSON error response
    String message = String.format(
        "{\"error\": \"Unauthorized\", \"message\": \"%s\"}",
        authException.getMessage());

    response.getOutputStream().println(message);
  }
}
