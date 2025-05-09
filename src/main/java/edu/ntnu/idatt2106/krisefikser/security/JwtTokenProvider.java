package edu.ntnu.idatt2106.krisefikser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * JwtTokenProvider is responsible for generating, validating, and parsing JWT tokens. It uses the
 * HMAC256 algorithm for signing and verifying tokens.
 *
 * @author Snake727
 * @version 0.1
 */
@Component
public class JwtTokenProvider {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms}")
  private long jwtExpirationMs;

  private Algorithm algorithm;
  private JWTVerifier verifier;

  /**
   * Initializes the JwtTokenProvider by creating the HMAC256 algorithm and JWT verifier.
   */

  @PostConstruct
  public void init() {
    // Create HMAC256 algorithm for token signing and verification
    algorithm = Algorithm.HMAC256(jwtSecret.getBytes(StandardCharsets.UTF_8));
    verifier = JWT.require(algorithm).build();
  }

  /**
   * Generate a JWT token based on authenticated user's details.
   *
   * @param authentication the authentication object containing user details
   * @return the generated JWT token as a string
   */
  public String generateToken(Authentication authentication) {
    String username = authentication.getName();
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    return JWT.create()
        .withSubject(username)
        .withIssuedAt(now)
        .withExpiresAt(expiryDate)
        .sign(algorithm);
  }

  /**
   * Parse and get username (subject) from JWT token.
   *
   * @param token the JWT token to parse
   * @return the username (subject) extracted from the token
   * @throws JWTVerificationException if the token is invalid or expired
   */
  public String getUsernameFromToken(String token) {
    DecodedJWT decoded = verifier.verify(token);
    return decoded.getSubject();
  }

  /**
   * Validate the JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   * @throws JWTVerificationException if the token is invalid or expired
   */
  public boolean validateToken(String token) {
    try {
      verifier.verify(token);
      return true;
    } catch (JWTVerificationException ex) {
      // Log the exception details here if needed
      return false;
    }
  }

  /**
   * Resolve token from Authorization header.
   *
   * @param request the HTTP request containing the Authorization header
   * @return the token extracted from the header, or null if not present
   * @throws IllegalArgumentException if the header is malformed
   */
  public String resolveToken(HttpServletRequest request) {
    String bearer = request.getHeader("Authorization");
    if (bearer != null && bearer.startsWith("Bearer ")) {
      return bearer.substring(7);
    }
    return null;
  }
}
