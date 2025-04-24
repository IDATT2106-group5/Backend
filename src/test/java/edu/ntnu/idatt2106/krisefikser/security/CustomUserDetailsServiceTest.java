package edu.ntnu.idatt2106.krisefikser.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private User user;

  @InjectMocks
  private CustomUserDetailsService userDetailsService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails() {
    // Arrange
    when(user.getEmail()).thenReturn("test@example.com");
    when(user.getPassword()).thenReturn("password");
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

    // Assert
    assertNotNull(userDetails);
    assertEquals("test@example.com", userDetails.getUsername());
    assertEquals("password", userDetails.getPassword());
    assertTrue(userDetails.isEnabled());
    assertInstanceOf(CustomUserDetails.class, userDetails);
    assertEquals(user, ((CustomUserDetails) userDetails).getUser());
  }

  @Test
  void loadUserByUsername_shouldThrowException_whenUserNotFound() {
    // Arrange
    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(UsernameNotFoundException.class, () -> {
      userDetailsService.loadUserByUsername("nonexistent@example.com");
    });
  }
}