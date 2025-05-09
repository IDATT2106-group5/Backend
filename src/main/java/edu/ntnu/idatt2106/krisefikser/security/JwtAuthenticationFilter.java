package edu.ntnu.idatt2106.krisefikser.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JwtAuthenticationFilter is responsible for filtering incoming requests to check for valid JWT
 * tokens.
 *
 * @author Snake727
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtTokenProvider tokenProvider;

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    // Get JWT from request
    String token = tokenProvider.resolveToken(request);
    if (token != null && tokenProvider.validateToken(token)) {
      // Get username from JWT
      String username = tokenProvider.getUsernameFromToken(token);

      // Load user details from database or in-memory
      UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      // Create authentication token
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      // Set the authentication in the SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // Continue filter chain
    filterChain.doFilter(request, response);
  }
}
