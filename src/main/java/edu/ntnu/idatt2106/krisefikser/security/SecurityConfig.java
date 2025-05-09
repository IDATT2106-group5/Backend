package edu.ntnu.idatt2106.krisefikser.security;

import edu.ntnu.idatt2106.krisefikser.service.CustomUserDetailsService;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
   * Configures the security filter chain for the application. It sets up JWT-based authentication,
   * authorization rules, and exception handling.
   *
   * @param http The HttpSecurity object to configure.
   * @return The configured SecurityFilterChain.
   * @throws Exception If an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/ws/**").permitAll()
            .requestMatchers("/api/news/get/").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/scenarios/**").permitAll()
            .requestMatchers("/api/auth/**").permitAll()
            .requestMatchers("/api/admin/setup").permitAll()
            .requestMatchers("/api/admin/login/2fa/**").permitAll()
            .requestMatchers("/api/admin/invite").hasAuthority("ROLE_SUPERADMIN")
            .requestMatchers("/api/admin/**").hasAuthority("ROLE_SUPERADMIN")
            .requestMatchers(HttpMethod.GET, "/api/incidents/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/map-icons/**").permitAll()
            .anyRequest().authenticated()
        )
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

  @Bean
  public RoleHierarchy roleHierarchy() {
    Map<String, Set<String>> roleHierarchyMap = new HashMap<>();

    // Add ADMIN authorities to SUPERADMIN
    String adminRole = "ROLE_ADMIN";
    String superAdminRole = "ROLE_SUPERADMIN";

    roleHierarchyMap.put(
        superAdminRole,
        Collections.singleton(adminRole)
    );

    return new MapBasedRoleHierarchy(roleHierarchyMap);
  }
}
