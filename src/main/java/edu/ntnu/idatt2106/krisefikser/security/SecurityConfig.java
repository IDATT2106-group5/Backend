package edu.ntnu.idatt2106.krisefikser.security;

import edu.ntnu.idatt2106.krisefikser.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for setting up JWT-based authentication and authorization. It
 * configures the security filter chain, authentication provider, and password encoder.
 *
 * @author Snake727
 */
@Configuration
public class SecurityConfig {

  private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;
  private final JwtAuthenticationFilter jwtAuthFilter;
  private final CustomUserDetailsService customUserDetailsService;

  /**
   * Constructor for SecurityConfig.
   *
   * @param jwtAuthEntryPoint        The JWT authentication entry point to handle unauthorized
   *                                 requests.
   * @param jwtAuthFilter            The JWT authentication filter to validate incoming requests.
   * @param customUserDetailsService The custom user details service for loading user-specific
   *                                 data.
   */
  public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthEntryPoint,
      JwtAuthenticationFilter jwtAuthFilter,
      CustomUserDetailsService customUserDetailsService) {
    this.jwtAuthEntryPoint = jwtAuthEntryPoint;
    this.jwtAuthFilter = jwtAuthFilter;
    this.customUserDetailsService = customUserDetailsService;
  }

  /**
   * Configures the security filter chain for the application.
   *
   * @param http The HttpSecurity object to configure security settings.
   * @return The configured SecurityFilterChain.
   * @throws Exception If a general error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // disable CSRF for stateless JWT-based authentication
        .csrf(AbstractHttpConfigurer::disable)

        // handle unauthorized attempts
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))

        // no session will be created or used by Spring Security
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // define authorization rules
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/household/**").permitAll()
            .anyRequest().authenticated()
        )

        // add JWT filter before the username/password filter
        .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /**
   * Configures the authentication provider for the application.
   *
   * @return The configured AuthenticationProvider.
   */

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(customUserDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
      throws Exception {
    return authConfig.getAuthenticationManager();
  }
}