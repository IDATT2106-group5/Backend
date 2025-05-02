package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import edu.ntnu.idatt2106.krisefikser.security.CustomUserDetails;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class CustomUserDetailsServiceTest {

  private final String TEST_EMAIL = "test@example.com";
  private final String TEST_PASSWORD = "password123";
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private CustomUserDetailsService userDetailsService;
  private User testUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    testUser = new User();
    testUser.setId(1L);
    testUser.setEmail(TEST_EMAIL);
    testUser.setPassword(TEST_PASSWORD);
    testUser.setFullName("Test User");
    testUser.setRole(Role.USER);
    testUser.setConfirmed(true);
  }

  @Test
  void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
    // Arrange
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

    // Assert
    assertNotNull(userDetails);
    assertEquals(TEST_EMAIL, userDetails.getUsername());
    assertEquals(TEST_PASSWORD, userDetails.getPassword());
    assertTrue(userDetails.isEnabled());
    assertTrue(userDetails.isAccountNonExpired());
    assertTrue(userDetails.isAccountNonLocked());
    assertTrue(userDetails.isCredentialsNonExpired());

    // Check that the returned object is a CustomUserDetails
    assertInstanceOf(CustomUserDetails.class, userDetails);

    // Check that the user inside CustomUserDetails is the same as our test user
    User retrievedUser = ((CustomUserDetails) userDetails).getUser();
    assertEquals(testUser.getId(), retrievedUser.getId());
    assertEquals(testUser.getEmail(), retrievedUser.getEmail());
  }

  @Test
  void loadUserByUsername_shouldReturnCorrectAuthorities_forUserRole() {
    // Arrange
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

    // Assert
    assertEquals(1, userDetails.getAuthorities().size());

    GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
    assertEquals("ROLE_USER", authority.getAuthority());
  }

  @Test
  void loadUserByUsername_shouldReturnCorrectAuthorities_forAdminRole() {
    // Arrange
    testUser.setRole(Role.ADMIN);
    when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));

    // Act
    UserDetails userDetails = userDetailsService.loadUserByUsername(TEST_EMAIL);

    // Assert
    assertEquals(1, userDetails.getAuthorities().size());

    GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
    assertEquals("ROLE_ADMIN", authority.getAuthority());
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

  @Test
  void loadUserByUsername_shouldThrowExceptionWithCorrectMessage_whenUserNotFound() {
    // Arrange
    String nonExistentEmail = "nonexistent@example.com";
    when(userRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
      userDetailsService.loadUserByUsername(nonExistentEmail);
    });

    String expectedMessage = "User not found with email: " + nonExistentEmail;
    assertEquals(expectedMessage, exception.getMessage());
  }
}