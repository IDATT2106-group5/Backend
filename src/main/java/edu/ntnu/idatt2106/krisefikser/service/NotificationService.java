package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.CreateNotificationRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NotificationRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * The type Notification service.
 */

@Service
public class NotificationService {

  private final SimpMessagingTemplate messagingTemplate;

  private final NotificationRepository notificationRepository;

  private final UserRepository userRepository;


  /**
   * Instantiates a new Notification service.
   *
   * @param messagingTemplate      the messaging template
   * @param notificationRepository the notification repository
   * @param userRepository         the user repository
   */
  public NotificationService(SimpMessagingTemplate messagingTemplate,
      NotificationRepository notificationRepository,
      UserRepository userRepository) {
    this.messagingTemplate = messagingTemplate;
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
  }

  /**
   * Send private notification.
   *
   * @param userId       the user id
   * @param notification the notification
   */
  public void sendPrivateNotification(String userId, NotificationDto notification) {
    messagingTemplate.convertAndSendToUser(
        userId,
        "/queue/notifications",
        notification
    );
  }

  /**
   * Broadcast notification.
   *
   * @param notification the notification
   */
  public void broadcastNotification(NotificationDto notification) {
    messagingTemplate.convertAndSend("/topic/notifications", notification);
  }

  /**
   * Save user notification.
   *
   * @param notification the notification
   */
  public void saveUserNotification(NotificationDto notification) {

  }

  /**
   * Marks the notification as read.
   *
   * @param notificationId the notification id
   */
  public void markNotificationAsRead(Long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    notification.setIsRead(true);
    notificationRepository.save(notification);
  }

  /**
   * Save household notification.
   *
   * @param notification the notification
   * @param householdId  the household id
   */
  public void saveHouseholdNotification(NotificationDto notification, Long householdId) {
    Notification notificationEntity = new Notification();
    notificationEntity.setType(notification.getType());
    notificationEntity.setIsRead(notification.isRead());
    List<User> users = userRepository.getUsersByHouseholdId(householdId);

    users.forEach(user -> {
      notificationEntity.setUser(user);
      notificationRepository.save(notificationEntity);
      sendHouseholdNotification(notification.getRecipientId().toString(), notification);
    });
  }

  /**
   * Send household notification.
   *
   * @param householdId  the household id
   * @param notification the notification
   */
  public void sendHouseholdNotification(String householdId, NotificationDto notification) {
    messagingTemplate.convertAndSend("/topic/household/" + householdId, notification);
  }

  /**
   * Get user notifications.
   *
   * @param userId The user id.
   * @return The list of notifications.
   */

  public List<NotificationResponseDto> getUserNotifications(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    List<Notification> notifications = notificationRepository.findAllByUserIdOrderByTimestampDesc(
        userId);
    return notifications.stream()
        .map(notification -> new NotificationResponseDto(
            notification.getId(),
            notification.getType(),
            notification.getUser().getId(),
            notification.getTimestamp(),
            notification.getIsRead()))
        .toList();
  }

  /**
   * Save notification.
   *
   * @param notificationRequest The notification request.
   * @param type                The notification type.
   */
  public void saveNotification(CreateNotificationRequestDto notificationRequest,
      NotificationType type) {
    Notification notification = new Notification();
    notification.setType(type);
    notification.setIsRead(false);
    notification.setTimestamp(LocalDateTime.now());
    notification.setUser(userRepository.findById(notificationRequest.getRecipientId())
        .orElseThrow(() -> new IllegalArgumentException("User not found")));

    notificationRepository.save(notification);
  }

}
