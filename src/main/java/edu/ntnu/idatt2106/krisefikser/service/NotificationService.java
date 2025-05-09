package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.PositionResponseDto;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    logger.info("NotificationService initialized");
  }

  /**
   * Send position update to a household.
   *
   * @param userId      the user id
   * @param householdId the household id
   * @param position    the position data
   */
  public void sendHouseholdPositionUpdate(String userId, String householdId, PositionDto position) {
    logger.info("Sending position update to household {}: latitude={}, longitude={}",
        householdId, position.getLatitude(), position.getLongitude());

    try {
      logger.debug("Looking up user with ID: {}", userId);
      User user = userRepository.findById(userId)
          .orElseThrow(() -> {
            logger.warn("User not found with ID: {}", userId);
            return new IllegalArgumentException("User does not exist");
          });
      logger.debug("Found user: {}", user.getFullName());

      PositionResponseDto response = new PositionResponseDto(
          user.getId(),
          user.getFullName(),
          position.getLongitude(),
          position.getLatitude()
      );
      logger.debug("Created position response for user: {}", user.getFullName());

      messagingTemplate.convertAndSend(
          "/topic/position/" + householdId,
          response
      );
      logger.info("Successfully sent position update to household {}", householdId);
    } catch (Exception e) {
      logger.error("Failed to send position update to household {}: {}",
          householdId, e.getMessage(), e);
    }
  }

  /**
   * Broadcast notification to all subscribers.
   *
   * @param notification the notification
   */
  public void broadcastNotification(NotificationDto notification) {
    logger.info("Broadcasting notification: type={}, message={}",
        notification.getType(), notification.getMessage());

    try {
      messagingTemplate.convertAndSend("/topic/notifications", notification);
      logger.debug("Notification broadcast successfully");
    } catch (Exception e) {
      logger.error("Failed to broadcast notification: {}", e.getMessage(), e);
    }
  }

  /**
   * Marks the notification as read.
   *
   * @param notificationId the notification id
   */
  public void markNotificationAsRead(Long notificationId) {
    logger.info("Marking notification as read: ID={}", notificationId);

    try {
      logger.debug("Looking up notification with ID: {}", notificationId);
      Notification notification = notificationRepository.findById(notificationId)
          .orElseThrow(() -> {
            logger.warn("Notification not found with ID: {}", notificationId);
            return new IllegalArgumentException("Notification not found");
          });

      notification.setIsRead(true);
      logger.debug("Updating notification read status");
      notificationRepository.save(notification);
      logger.info("Notification {} successfully marked as read", notificationId);
    } catch (Exception e) {
      logger.error("Failed to mark notification {} as read: {}", notificationId, e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Gets user notifications.
   *
   * @return the user notifications
   */
  public List<NotificationResponseDto> getUserNotifications() {
    logger.info("Fetching notifications for user");

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

    try {
      User user = userRepository.getUserByEmail(email)
          .orElseThrow(() -> {
            logger.warn("User not found with email: {}", email);
            return new IllegalArgumentException("User not found");
          });

      logger.debug("Retrieving notifications for user ordered by timestamp");
      List<Notification> notifications =
          notificationRepository.findAllByUserIdOrderByTimestampDesc(user.getId());
      logger.debug("Retrieved {} notifications for user", notifications.size());

      List<NotificationResponseDto> result = notifications.stream()
          .map(notification -> new NotificationResponseDto(
              notification.getId(),
              notification.getType(),
              notification.getUser().getId(),
              notification.getTimestamp(),
              notification.getMessage(),
              notification.getIsRead()))
          .toList();

      logger.info("Returning {} notifications for user {}", result.size(), user.getId());
      return result;
    } catch (Exception e) {
      logger.error("Error fetching notifications for user: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Send expiry notification.
   *
   * @param item the item
   */
  public void sendExpiryNotification(StorageItem item) {
    logger.info("Sending expiry notification for item: {} in household: {}",
        item.getItem().getName(), item.getHousehold().getId());

    long daysUntilExpiry = ChronoUnit.DAYS.between(
        LocalDateTime.now().toLocalDate(), item.getExpirationDate().toLocalDate());
    logger.debug("Item expires in {} days", daysUntilExpiry);

    NotificationDto notification = new NotificationDto();
    notification.setType(NotificationType.STOCK_CONTROL);
    notification.setMessage("Your item '" + item.getItem().getName()
        + "' is expiring in "
        + daysUntilExpiry + " days.");
    notification.setTimestamp(LocalDateTime.now());
    logger.debug("Created expiry notification: {}", notification.getMessage());

    saveHouseholdNotification(notification, item.getHousehold().getId());
    logger.info("Expiry notification sent for item: {}", item.getItem().getName());
  }

  /**
   * Save household notification and send to all household users.
   *
   * @param notification the notification
   * @param householdId  the household id
   */
  public void saveHouseholdNotification(NotificationDto notification, String householdId) {
    logger.info("Saving household notification for household {}: type={}, message={}",
        householdId, notification.getType(), notification.getMessage());

    try {
      logger.debug("Creating notification entity");
      Notification notificationEntity = new Notification();
      notificationEntity.setType(notification.getType());
      notificationEntity.setIsRead(notification.isRead());
      notificationEntity.setTimestamp(LocalDateTime.now());
      notificationEntity.setMessage(notification.getMessage());

      logger.debug("Fetching users for household: {}", householdId);
      List<User> users = userRepository.getUsersByHouseholdId(householdId);
      logger.debug("Found {} users in household", users.size());

      users.forEach(user -> {
        logger.info("Sending household notification to user {}: type={}, message={}",
            user.getId(), notification.getType(), notification.getMessage());

        notificationEntity.setUser(user);
        notificationRepository.save(notificationEntity);
        logger.debug("Saved notification for user: {}", user.getId());

        sendPrivateNotification(user.getId(), notification);
      });
      logger.info("Household notification processed for all {} users", users.size());
    } catch (Exception e) {
      logger.error("Failed to save household notification: {}", e.getMessage(), e);
      throw e;
    }
  }

  /**
   * Send private notification.
   *
   * @param userId       the user id
   * @param notification the notification
   */
  public void sendPrivateNotification(String userId, NotificationDto notification) {
    logger.info("Sending private notification to user {}: type={}, message={}, timestamp={}",
        userId, notification.getType(), notification.getMessage(),
        notification.getTimestamp());

    try {
      messagingTemplate.convertAndSendToUser(
          userId,
          "/queue/notifications",
          notification
      );
      logger.info("Successfully sent notification to user {}", userId);
    } catch (Exception e) {
      logger.error("Failed to send notification to user {}: {}", userId, e.getMessage(), e);
    }
  }

  /**
   * Send incident notification.
   *
   * @param message  the message
   * @param incident the incident
   */
  public void notifyIncident(String message, Incident incident) {
    logger.info("Notifying about incident: {} at coordinates ({}, {}), radius: {}km",
        message, incident.getLatitude(), incident.getLongitude(), incident.getImpactRadius());

    NotificationDto notification = new NotificationDto();
    notification.setType(NotificationType.INCIDENT);
    notification.setMessage(message);
    notification.setTimestamp(LocalDateTime.now());
    logger.debug("Created incident notification: {}", notification.getMessage());

    logger.debug("Finding users within incident radius");
    List<User> affectedUsers = findUsersWithinIncidentRadius(
        incident.getLatitude(), incident.getLongitude(), incident.getImpactRadius());
    logger.debug("Found {} users within incident radius", affectedUsers.size());

    affectedUsers.forEach(user -> {
      logger.debug("Sending incident notification to user: {}", user.getId());
      notification.setRecipientId(user.getId());
      saveNotification(notification);
      sendPrivateNotification(user.getId(), notification);
    });

    logger.info("Incident notifications sent to {} users", affectedUsers.size());
  }

  /**
   * Finds all users in an incidents' radius to notify.
   *
   * @param latitude  the latitude.
   * @param longitude the longitude.
   * @param radius    the radius.
   * @return the list
   */
  public List<User> findUsersWithinIncidentRadius(double latitude, double longitude,
      double radius) {
    logger.info("Finding users within {}km of coordinates [{}, {}]", radius, latitude, longitude);

    // Using a multiplier of 1.4 on the radius as in original code
    double adjustedRadius = radius * 1.4;
    logger.debug("Using adjusted radius of {}km for search", adjustedRadius);

    List<User> users = userRepository.findUsersWithinRadius(latitude, longitude, adjustedRadius);
    logger.info("Found {} users within {}km radius", users.size(), radius);

    return users;
  }

  /**
   * Save notification.
   *
   * @param notificationRequest the notification request
   */
  public void saveNotification(NotificationDto notificationRequest) {
    logger.info("Saving notification: type={}, recipient={}, message={}",
        notificationRequest.getType(), notificationRequest.getRecipientId(),
        notificationRequest.getMessage());

    try {
      logger.debug("Creating notification entity");
      Notification notification = new Notification();
      notification.setType(notificationRequest.getType());
      notification.setIsRead(false);
      notification.setTimestamp(notificationRequest.getTimestamp());
      notification.setMessage(notificationRequest.getMessage());

      logger.debug("Looking up recipient user: {}", notificationRequest.getRecipientId());
      User user = userRepository.findById(notificationRequest.getRecipientId())
          .orElseThrow(() -> {
            logger.warn("User not found with ID: {}", notificationRequest.getRecipientId());
            return new IllegalArgumentException("User not found");
          });
      notification.setUser(user);

      logger.debug("Saving notification to database");
      notificationRepository.save(notification);
      logger.info("Notification saved successfully");
    } catch (Exception e) {
      logger.error("Failed to save notification: {}", e.getMessage(), e);
      throw e;
    }
  }
}