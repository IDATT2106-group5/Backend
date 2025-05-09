package edu.ntnu.idatt2106.krisefikser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Primary
@Profile("test")
public class MockJwtTokenProvider extends JwtTokenProvider {
    
    private Algorithm algorithm;
    private JWTVerifier verifier;
    private final String jwtSecret = "test-secret-key-that-is-very-long-for-testing-purposes-only";
    private final long jwtExpirationMs = 86400000;
    
    @PostConstruct
    @Override
    public void init() {
        algorithm = Algorithm.HMAC256(jwtSecret.getBytes(StandardCharsets.UTF_8));
        verifier = JWT.require(algorithm).build();
    }
    
    @Override
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
    
    @Override
    public String getUsernameFromToken(String token) {
        DecodedJWT decoded = verifier.verify(token);
        return decoded.getSubject();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }
    
    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}