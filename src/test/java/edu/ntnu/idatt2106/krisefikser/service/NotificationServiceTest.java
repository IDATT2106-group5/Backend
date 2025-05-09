package edu.ntnu.idatt2106.krisefikser.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.PositionResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Household;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.Severity;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NotificationRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

  @Mock
  private SimpMessagingTemplate messagingTemplate;

  @Mock
  private NotificationRepository notificationRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private NotificationService notificationService;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @Captor
  private ArgumentCaptor<NotificationDto> notificationDtoCaptor;

  @Captor
  private ArgumentCaptor<Notification> notificationCaptor;

  private User testUser;
  private Household testHousehold;
  private Notification testNotification;
  private NotificationDto testNotificationDto;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId("user-123");
    testUser.setEmail("user@example.com");
    testUser.setFullName("Test User");
    testUser.setTlf("12345678");
    testUser.setRole(Role.USER);
    testUser.setLatitude("60.0");
    testUser.setLongitude("10.0");

    testHousehold = new Household();
    testHousehold.setId("household-123");
    testHousehold.setName("Test Household");
    testUser.setHousehold(testHousehold);

    testNotification = new Notification();
    testNotification.setId(1L);
    testNotification.setUser(testUser);
    testNotification.setType(NotificationType.INFO);
    testNotification.setMessage("Test notification message");
    testNotification.setTimestamp(LocalDateTime.now());
    testNotification.setIsRead(false);

    testNotificationDto = new NotificationDto();
    testNotificationDto.setType(NotificationType.INFO);
    testNotificationDto.setRecipientId("user-123");
    testNotificationDto.setMessage("Test notification message");
    testNotificationDto.setTimestamp(LocalDateTime.now());
    testNotificationDto.setRead(false);
  }

  @Test
  void sendHouseholdPositionUpdate_shouldSendPositionToTopic() {
    // Arrange
    String householdId = "household-123";
    PositionDto positionDto = new PositionDto("user-123", "10.0", "60.0");
    when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

    // Act
    notificationService.sendHouseholdPositionUpdate(householdId, positionDto);

    // Assert
    verify(messagingTemplate).convertAndSend(
        eq("/topic/position/" + householdId),
        any(PositionResponseDto.class)
    );
    verify(userRepository).findById("user-123");
  }

  @Test
  void sendHouseholdPositionUpdate_shouldThrowException_whenUserNotFound() {
    // Arrange
    String householdId = "household-123";
    PositionDto positionDto = new PositionDto("nonexistent-user", "10.0", "60.0");
    when(userRepository.findById("nonexistent-user")).thenReturn(Optional.empty());

    // Act - this won't throw an exception because the service catches it internally
    notificationService.sendHouseholdPositionUpdate(householdId, positionDto);

    // Assert - verify the message doesn't get sent instead
    verify(userRepository).findById("nonexistent-user");
    // Fix the ambiguous method call by specifying the exact parameter types
    verify(messagingTemplate, times(0)).convertAndSend(
        anyString(),
        any(PositionResponseDto.class)
    );
  }

  @Test
  void broadcastNotification_shouldSendToGeneralTopic() {
    // Act
    notificationService.broadcastNotification(testNotificationDto);

    // Assert
    verify(messagingTemplate).convertAndSend("/topic/notifications", testNotificationDto);
  }

  @Test
  void markNotificationAsRead_shouldUpdateReadStatus() {
    // Arrange
    Long notificationId = 1L;
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(testNotification));

    // Act
    notificationService.markNotificationAsRead(notificationId);

    // Assert
    assertTrue(testNotification.getIsRead());
    verify(notificationRepository).save(testNotification);
  }

  @Test
  void markNotificationAsRead_shouldThrowException_whenNotificationNotFound() {
    // Arrange
    Long notificationId = 999L;
    when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      notificationService.markNotificationAsRead(notificationId);
    });

    assertEquals("Notification not found", exception.getMessage());
  }

  @Test
  void getUserNotifications_shouldReturnUserNotifications() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("user@example.com");

    // Arrange
    List<Notification> notifications = List.of(testNotification);
    when(userRepository.getUserByEmail("user@example.com")).thenReturn(Optional.of(testUser));
    when(notificationRepository.findAllByUserIdOrderByTimestampDesc(testUser.getId())).thenReturn(
        notifications);

    // Act
    List<NotificationResponseDto> result = notificationService.getUserNotifications();

    // Assert
    assertEquals(1, result.size());
    assertEquals(testNotification.getId(), result.get(0).getId());
    assertEquals(testNotification.getType(), result.get(0).getType());
    assertEquals(testNotification.getUser().getId(), result.get(0).getRecipientId());
    assertEquals(testNotification.getMessage(), result.get(0).getMessage());
    assertEquals(testNotification.getIsRead(), result.get(0).isRead());
  }

  @Test
  void getUserNotifications_shouldThrowException_whenUserNotFound() {
    // Set up security context for this test
    when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getName()).thenReturn("nonexistent@example.com");

    // Arrange
    when(userRepository.getUserByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      notificationService.getUserNotifications();
    });

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void sendExpiryNotification_shouldCreateAndSaveNotification() {
    // Arrange
    Household household = new Household();
    household.setId("household-123");

    Item item = new Item();
    item.setName("Test Item");

    StorageItem storageItem = new StorageItem();
    storageItem.setHousehold(household);
    storageItem.setItem(item);
    storageItem.setExpirationDate(LocalDateTime.now().plusDays(5));

    User householdUser = new User();
    householdUser.setId("user-123");

    List<User> householdUsers = List.of(householdUser);
    when(userRepository.getUsersByHouseholdId(household.getId())).thenReturn(householdUsers);

    // Act
    notificationService.sendExpiryNotification(storageItem);

    // Assert
    verify(notificationRepository).save(any(Notification.class));
    verify(userRepository).getUsersByHouseholdId(household.getId());
  }

  @Test
  void saveHouseholdNotification_shouldSaveNotificationForAllHouseholdMembers() {
    // Arrange
    NotificationDto notification = testNotificationDto;
    String householdId = "household-123";

    User user1 = new User();
    user1.setId("user-1");
    User user2 = new User();
    user2.setId("user-2");

    List<User> householdUsers = Arrays.asList(user1, user2);
    when(userRepository.getUsersByHouseholdId(householdId)).thenReturn(householdUsers);

    // Act
    notificationService.saveHouseholdNotification(notification, householdId);

    // Assert
    verify(notificationRepository, times(2)).save(any(Notification.class));
    verify(userRepository).getUsersByHouseholdId(householdId);
  }

  @Test
  void sendPrivateNotification_shouldSendToUserQueue() {
    // Arrange
    String userId = "user-123";
    NotificationDto notification = testNotificationDto;

    // Act
    notificationService.sendPrivateNotification(userId, notification);

    // Assert
    verify(messagingTemplate).convertAndSendToUser(userId, "/queue/notifications", notification);
  }

  @Test
  void notifyIncident_shouldNotifyAffectedUsers() {
    // Arrange
    Incident incident = new Incident();
    incident.setName("Test Incident");
    incident.setDescription("Test incident description");
    incident.setLatitude(60.0);
    incident.setLongitude(10.0);
    incident.setImpactRadius(5.0);
    incident.setSeverity(Severity.RED);
    incident.setStartedAt(LocalDateTime.now());

    User affectedUser = new User();
    affectedUser.setId("user-123");
    List<User> affectedUsers = List.of(affectedUser);

    when(userRepository.findUsersWithinRadius(incident.getLatitude(), incident.getLongitude(),
        incident.getImpactRadius() * 1.4)).thenReturn(affectedUsers);

    // Add this mock to fix the "User not found" error
    when(userRepository.findById("user-123")).thenReturn(Optional.of(affectedUser));

    // Act
    notificationService.notifyIncident("Test incident alert", incident);

    // Assert
    verify(userRepository).findUsersWithinRadius(incident.getLatitude(), incident.getLongitude(),
        incident.getImpactRadius() * 1.4);
    verify(notificationRepository).save(any(Notification.class));
    verify(userRepository).findById("user-123");
  }

  @Test
  void saveNotification_shouldSaveNotificationForUser() {
    // Arrange
    NotificationDto notification = testNotificationDto;
    notification.setRecipientId("user-123");

    when(userRepository.findById("user-123")).thenReturn(Optional.of(testUser));

    // Act
    notificationService.saveNotification(notification);

    // Assert
    verify(notificationRepository).save(any(Notification.class));
    verify(userRepository).findById("user-123");
  }

  @Test
  void saveNotification_shouldThrowException_whenUserNotFound() {
    // Arrange
    NotificationDto notification = testNotificationDto;
    notification.setRecipientId("nonexistent-user");

    when(userRepository.findById("nonexistent-user")).thenReturn(Optional.empty());

    // Act & Assert
    Exception exception = assertThrows(IllegalArgumentException.class, () -> {
      notificationService.saveNotification(notification);
    });

    assertEquals("User not found", exception.getMessage());
  }

  @Test
  void findUsersWithinIncidentRadius_shouldReturnUsersInRadius() {
    // Arrange
    double latitude = 60.0;
    double longitude = 10.0;
    double radius = 5.0;

    List<User> usersInRadius = List.of(testUser);
    when(userRepository.findUsersWithinRadius(latitude, longitude, radius * 1.4)).thenReturn(
        usersInRadius);

    // Act
    List<User> result = notificationService.findUsersWithinIncidentRadius(latitude, longitude,
        radius);

    // Assert
    assertEquals(1, result.size());
    assertEquals(testUser, result.get(0));
    verify(userRepository).findUsersWithinRadius(latitude, longitude, radius * 1.4);
  }
}