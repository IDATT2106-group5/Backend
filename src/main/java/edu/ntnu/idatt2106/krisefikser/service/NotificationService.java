package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Incident;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.StorageItem;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.User;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NotificationRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private final Logger logger = LoggerFactory.getLogger(NotificationService.class.getName());


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
  public void sendPrivateNotification(Long userId, NotificationDto notification) {
    logger.info("Sending private notification to user {}: type={}, message={}, timestamp={}",
        userId, notification.getType(), notification.getMessage(),
        notification.getTimestamp());

    try {
      messagingTemplate.convertAndSendToUser(
          String.valueOf(userId),
          "/queue/notifications",
          notification
      );
      logger.info("Successfully sent notification to user {}", userId);
    } catch (Exception e) {
      logger.error("Failed to send notification to user {}: {}", userId, e.getMessage(), e);
    }
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
    notificationEntity.setTimestamp(LocalDateTime.now());
    notificationEntity.setMessage(notification.getMessage());
    List<User> users = userRepository.getUsersByHouseholdId(householdId);

    users.forEach(user -> {
      logger.info("Sending household notification to user {}: type={}, message={}, timestamp={}",
          user.getId(), notification.getType(), notification.getMessage(),
          notification.getTimestamp());

      notificationEntity.setUser(user);
      notificationRepository.save(notificationEntity);
      sendPrivateNotification(user.getId(), notification);
    });
  }

  /**
   * Gets user notifications.
   *
   * @param userId the user id
   * @return the user notifications
   */
  public List<NotificationResponseDto> getUserNotifications(Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found"));
    List<Notification> notifications =
        notificationRepository.findAllByUserIdOrderByTimestampDesc(userId);
    return notifications.stream()
        .map(notification -> new NotificationResponseDto(
            notification.getId(),
            notification.getType(),
            notification.getUser().getId(),
            notification.getTimestamp(),
            notification.getMessage(),
            notification.getIsRead()))
        .toList();
  }

  /**
   * Save notification.
   *
   * @param notificationRequest the notification request
   */
  public void saveNotification(NotificationDto notificationRequest) {
    Notification notification = new Notification();
    notification.setType(notificationRequest.getType());
    notification.setIsRead(false);
    notification.setTimestamp(notificationRequest.getTimestamp());
    notification.setMessage(notificationRequest.getMessage());
    notification.setUser(userRepository.findById(notificationRequest.getRecipientId())
        .orElseThrow(() -> new IllegalArgumentException("User not found")));

    notificationRepository.save(notification);
  }

  /**
   * Send expiry notification.
   *
   * @param item the item
   */
  public void sendExpiryNotification(StorageItem item) {
    NotificationDto notification = new NotificationDto();
    notification.setType(NotificationType.STOCK_CONTROL);
    notification.setMessage("Your item '" + item.getItem().getName() + "' is expiring in " +
        ChronoUnit.DAYS.between(LocalDateTime.now().toLocalDate(),
            item.getExpirationDate().toLocalDate()) + " days.");

    saveHouseholdNotification(notification, item.getHousehold().getId());
  }

  /**
   * Send incident notification.
   *
   * @param message the message
   */
  public void notifyIncident(String message, Incident incident) {
    NotificationDto notification = new NotificationDto();
    notification.setType(NotificationType.INCIDENT);
    notification.setMessage(message);
    notification.setTimestamp(LocalDateTime.now());

    findUsersWithinIncidentRadius(incident.getLatitude(), incident.getLongitude(),
        incident.getImpactRadius()).forEach(user -> {
      notification.setRecipientId(user.getId());
      saveNotification(notification);
      sendPrivateNotification(user.getId(), notification);
    });
  }

  /**
   * Finds all users in an incidents' radius to notify
   *
   * @param latitude  the latitude.
   * @param longitude the longitude.
   * @param radius    the radius.
   */
  public List<User> findUsersWithinIncidentRadius(double latitude, double longitude,
                                                  double radius) {
    logger.info("Finding users within {}km of coordinates [{}, {}]", radius, latitude, longitude);
    return userRepository.findUsersWithinRadius(latitude, longitude, radius * 1.4);
  }
}
