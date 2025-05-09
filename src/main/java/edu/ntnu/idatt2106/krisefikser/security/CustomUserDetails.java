package edu.ntnu.idatt2106.krisefikser.security;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * CustomUserDetails adapts our User entity to Spring Security's UserDetails interface. This class
 * serves as a bridge between the application's user model and Spring Security's authentication
 * system.
 */
public class CustomUserDetails implements UserDetails {

  private final User user;

  /**
   * Constructor for CustomUserDetails.
   *
   * @param user the User entity.
   */

  public CustomUserDetails(User user) {
    this.user = user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // Return the appropriate authority based on user's role
    return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    // Using email as the username for authentication
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Returns the underlying User entity.
   *
   * @return the User entity
   */
  public User getUser() {
    return user;
  }
}