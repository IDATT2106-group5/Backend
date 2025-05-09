package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private NotificationService notificationService;

  @InjectMocks
  private UserService userService;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  private User testUser;
  private Household testHousehold;

  @BeforeEach
  void setUp() {
    // Set up common test objects
    testUser = new User();
    testUser.setId("user-123");
    testUser.setEmail("user@example.com");
    testUser.setFullName("Test User");
    testUser.setTlf("12345678");
    testUser.setRole(Role.USER);

    User householdOwner = new User();
    householdOwner.setId("owner-123");
    householdOwner.setEmail("owner@example.com");
    householdOwner.setFullName("Household Owner");
    householdOwner.setTlf("87654321");
    householdOwner.setRole(Role.USER);

    testHousehold = new Household();
    testHousehold.setId("household-123");
    testHousehold.setName("Test Household");
    testHousehold.setAddress("123 Test Street");
    testHousehold.setNumberOfMembers(3);
    testHousehold.setOwner(householdOwner);

    testUser.setHousehold(testHousehold);

  }

  @Test
  void getCurrentUser_shouldReturnAuthenticatedUserAsDto() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user@example.com");

    // Arrange
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));

    // Act
    UserResponseDto result = userService.getCurrentUser();

    // Assert
    assertNotNull(result);
    assertEquals("user-123", result.getId());
    assertEquals("user@example.com", result.getEmail());
    assertEquals("Test User", result.getFullName());
    assertEquals("12345678", result.getTlf());
    assertEquals(Role.USER, result.getRole());

    verify(userRepository).getUserByEmail("user@example.com");
  }

  @Test
  void getCurrentUser_shouldThrowException_whenUserNotFound() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user@example.com");

    // Arrange
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.getCurrentUser();
    });

    assertEquals("No user logged in", exception.getMessage());
    verify(userRepository).getUserByEmail("user@example.com");
  }

  @Test
  void checkIfMailExists_shouldReturnUserId_whenEmailExists() {
    // No security context setup needed for this test

    // Arrange
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));

    // Act
    String result = userService.checkIfMailExists("user@example.com");

    // Assert
    assertEquals("user-123", result);
    verify(userRepository).getUserByEmail("user@example.com");
  }

  @Test
  void checkIfMailExists_shouldThrowException_whenEmailDoesNotExist() {
    // No security context setup needed for this test

    // Arrange
    when(userRepository.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.checkIfMailExists("nonexistent@example.com");
    });

    assertEquals("No user with this email", exception.getMessage());
    verify(userRepository).getUserByEmail("nonexistent@example.com");
  }

  @Test
  void getHousehold_shouldReturnUserHouseholdAsDto() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user@example.com");

    // Arrange
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));

    // Act
    HouseholdResponseDto result = userService.getHousehold();

    // Assert
    assertNotNull(result);
    assertEquals("household-123", result.getId());
    assertEquals("Test Household", result.getName());
    assertEquals("123 Test Street", result.getAddress());
    assertNotNull(result.getOwner());
    assertEquals("owner-123", result.getOwner().getId());

    verify(userRepository).getUserByEmail("user@example.com");
  }

  @Test
  void getHousehold_shouldThrowException_whenUserNotFound() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user@example.com");

    // Arrange
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.getHousehold();
    });

    assertEquals("No user logged in", exception.getMessage());
    verify(userRepository).getUserByEmail("user@example.com");
  }

  @Test
  void getAllAdmins_shouldReturnAllAdminUsers() {
    // Arrange
    User admin1 = new User();
    admin1.setId("admin-1");
    admin1.setEmail("admin1@example.com");
    admin1.setFullName("Admin One");
    admin1.setTlf("111111");
    admin1.setRole(Role.ADMIN);

    User admin2 = new User();
    admin2.setId("admin-2");
    admin2.setEmail("admin2@example.com");
    admin2.setFullName("Admin Two");
    admin2.setTlf("222222");
    admin2.setRole(Role.SUPERADMIN);

    User regularUser = new User();
    regularUser.setId("user-1");
    regularUser.setEmail("user1@example.com");
    regularUser.setFullName("User One");
    regularUser.setTlf("333333");
    regularUser.setRole(Role.USER);

    when(userRepository.findAll()).thenReturn(Arrays.asList(admin1, admin2, regularUser));

    // Act
    List<UserResponseDto> result = userService.getAllAdmins();

    // Assert
    assertEquals(2, result.size());

    boolean foundAdmin1 = false;
    boolean foundAdmin2 = false;

    for (UserResponseDto dto : result) {
      if (dto.getId().equals("admin-1") && dto.getRole() == Role.ADMIN) {
        foundAdmin1 = true;
      }
      if (dto.getId().equals("admin-2") && dto.getRole() == Role.SUPERADMIN) {
        foundAdmin2 = true;
      }
    }

    assertTrue(foundAdmin1, "Admin1 should be in the result");
    assertTrue(foundAdmin2, "Admin2 should be in the result");

    verify(userRepository).findAll();
  }

  @Test
  void updatePosition_shouldUpdateUserPositionAndNotifyHousehold() {
    // Arrange
    PositionDto positionDto = new PositionDto("user-123", "10.123", "60.456");

    when(userRepository.getUsersById("user-123")).thenReturn(Optional.of(testUser));

    // Act
    userService.updatePosition(positionDto);

    // Assert
    assertEquals("10.123", testUser.getLongitude());
    assertEquals("60.456", testUser.getLatitude());

    verify(userRepository).getUsersById("user-123");
    verify(userRepository).save(testUser);
    verify(notificationService).sendHouseholdPositionUpdate("household-123", positionDto);
  }

  @Test
  void updatePosition_shouldThrowException_whenUserNotFound() {
    // Arrange
    PositionDto positionDto = new PositionDto("nonexistent-123", "10.123", "60.456");

    when(userRepository.getUsersById("nonexistent-123")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      userService.updatePosition(positionDto);
    });

    assertEquals("No user found", exception.getMessage());
    verify(userRepository).getUsersById("nonexistent-123");
    verify(userRepository, never()).save(any());
    verify(notificationService, never()).sendHouseholdPositionUpdate(anyString(), any());
  }
}