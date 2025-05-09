package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.position.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.household.HouseholdResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.household.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.user.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.user.UserRepository;
import edu.ntnu.idatt2106.krisefikser.service.notification.NotificationService;
import edu.ntnu.idatt2106.krisefikser.service.user.UserService;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
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
    testUser = new User();
    testUser.setId("user-123");
    testUser.setEmail("user@example.com");
    testUser.setFullName("Test User");
    testUser.setTlf("12345678");
    testUser.setRole(Role.USER);

    testHousehold = new Household();
    testHousehold.setId("household-123");
    testHousehold.setName("Test Household");
    testHousehold.setAddress("123 Test Street");

    User owner = new User();
    owner.setId("owner-123");
    owner.setEmail("owner@example.com");
    owner.setFullName("Household Owner");
    testHousehold.setOwner(owner);

    testUser.setHousehold(testHousehold);
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
  void extractUserFromTokenSuccess() throws Exception {
    // Create a valid JWT token with base64-encoded payload
    JSONObject payload = new JSONObject();
    payload.put("sub", "user-123");
    String encodedPayload = Base64.getEncoder().encodeToString(payload.toString().getBytes());
    String token = "header." + encodedPayload + ".signature";

    // Use reflection to access private method
    java.lang.reflect.Method method = UserService.class.getDeclaredMethod("extractUserFromToken",
        String.class);
    method.setAccessible(true);

    // Act
    String result = (String) method.invoke(userService, token);

    // Assert
    assertEquals("user-123", result);
  }

  @Test
  void getCurrentUserSuccess() {
    // Use try-with-resources to restore the original security context after test
    try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(
        SecurityContextHolder.class)) {
      // Arrange
      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

      // Act
      UserResponseDto result = userService.getCurrentUser();

      // Assert
      assertNotNull(result);
      assertEquals("user-123", result.getId());
      assertEquals("user@example.com", result.getEmail());
      assertEquals("Test User", result.getFullName());
      assertEquals("12345678", result.getTlf());
      assertEquals(Role.USER, result.getRole());
      verify(userRepository).findByEmail("user@example.com");
    }
  }

  @Test
  void getCurrentUserUserNotFound() {
    // Use try-with-resources to restore the original security context after test
    try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(
        SecurityContextHolder.class)) {
      // Arrange
      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("unknown@example.com");
      when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> userService.getCurrentUser()
      );
      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).findByEmail("unknown@example.com");
    }
  }

  @Test
  void checkIfMailExistsSuccess() {
    // Arrange
    String email = "user@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

    // Act
    String result = userService.checkIfMailExists(email);

    // Assert
    assertEquals("user-123", result);
    verify(userRepository).findByEmail(email);
  }

  @Test
  void checkIfMailExistsEmailNotFound() {
    // Arrange
    String email = "nonexistent@example.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.checkIfMailExists(email)
    );
    assertEquals("No user with this email", exception.getMessage());
    verify(userRepository).findByEmail(email);
  }

  @Test
  void getHouseholdSuccess() {
    // Use try-with-resources to restore the original security context after test
    try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(
        SecurityContextHolder.class)) {
      // Arrange
      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("user@example.com");
      when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(testUser));

      // Act
      HouseholdResponseDto result = userService.getHousehold();

      // Assert
      assertNotNull(result);
      assertEquals("household-123", result.getId());
      assertEquals("Test Household", result.getName());
      assertEquals("123 Test Street", result.getAddress());
      assertEquals("owner-123", result.getOwner().getId());
      verify(userRepository).findByEmail("user@example.com");
    }
  }

  @Test
  void getHouseholdUserNotFound() {
    try (MockedStatic<SecurityContextHolder> mockedStatic = Mockito.mockStatic(
        SecurityContextHolder.class)) {
      // Arrange
      mockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getName()).thenReturn("unknown@example.com");
      when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      IllegalArgumentException exception = assertThrows(
          IllegalArgumentException.class,
          () -> userService.getHousehold()
      );
      assertEquals("No user logged in", exception.getMessage());
      verify(userRepository).findByEmail("unknown@example.com");
    }
  }

  @Test
  void updatePositionSuccess() {
    // Arrange
    PositionDto positionDto = new PositionDto();
    positionDto.setLatitude("63.4305");
    positionDto.setLongitude("10.3951");

    // Create valid JWT token
    JSONObject payload = new JSONObject();
    payload.put("sub", "user-123");
    String encodedPayload = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());
    String token = "header." + encodedPayload + ".signature";
    positionDto.setToken(token);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

    // Act
    userService.updatePosition(positionDto);

    // Assert
    assertEquals("63.4305", testUser.getLatitude());
    assertEquals("10.3951", testUser.getLongitude());
    verify(userRepository).save(testUser);
    verify(notificationService).sendHouseholdPositionUpdate(
        eq("user-123"),
        eq("household-123"),
        eq(positionDto)
    );
  }

  @Test
  void updatePositionInvalidToken() {
    // Arrange
    PositionDto positionDto = new PositionDto();
    positionDto.setLatitude("63.4305");
    positionDto.setLongitude("10.3951");
    positionDto.setToken("invalid-token");

    // Act & Assert
    Exception exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.updatePosition(positionDto)
    );

    // The message could vary depending on how the actual token processing fails
    // We should verify it's either one of these expected messages
    assertTrue(
        exception.getMessage().contains("Invalid JWT format")
            || exception.getMessage().contains("Failed to extract user from token"),
        "Expected exception message about invalid token, but got: " + exception.getMessage()
    );

    verify(userRepository, never()).findByEmail(anyString());
    verify(userRepository, never()).save(any());
    verify(notificationService, never()).sendHouseholdPositionUpdate(anyString(), anyString(),
        any());
  }

  @Test
  void updatePositionUserNotFound() {
    // Arrange
    PositionDto positionDto = new PositionDto();
    positionDto.setLatitude("63.4305");
    positionDto.setLongitude("10.3951");

    // Create valid JWT token
    JSONObject payload = new JSONObject();
    payload.put("sub", "user-123");
    String encodedPayload = Base64.getUrlEncoder().encodeToString(payload.toString().getBytes());
    String token = "header." + encodedPayload + ".signature";
    positionDto.setToken(token);

    when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
        IllegalArgumentException.class,
        () -> userService.updatePosition(positionDto)
    );
    assertEquals("No user found", exception.getMessage());
    verify(userRepository).findByEmail(anyString());
    verify(userRepository, never()).save(any());
    verify(notificationService, never()).sendHouseholdPositionUpdate(anyString(), anyString(),
        any());
  }
}