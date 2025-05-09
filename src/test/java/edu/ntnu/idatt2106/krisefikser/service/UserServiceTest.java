package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;

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
}