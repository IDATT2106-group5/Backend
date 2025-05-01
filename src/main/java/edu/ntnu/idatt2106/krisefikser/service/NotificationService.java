package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.persistance.entity.Notification;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NotificationRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
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
   * Send household notification.
   *
   * @param householdId  the household id
   * @param notification the notification
   */
  public void sendHouseholdNotification(String householdId, NotificationDto notification) {
    messagingTemplate.convertAndSend("/topic/household/" + householdId, notification);
  }

  /**
   * Save user notification.
   *
   * @param notification the notification
   */
  public void saveUserNotification(NotificationDto notification) {
    Notification notificationEntity = new Notification();
    notificationEntity.setUser(userRepository.findById(notification.getRecipientId())
        .orElseThrow(() -> new IllegalArgumentException("User not found")));
    notificationEntity.setType(notification.getType());
    notificationEntity.setRead(notification.isRead());

    // Save the notification entity to the database
    notificationRepository.save(notificationEntity);

    // Send the notification to the user
    sendPrivateNotification(notification.getRecipientId().toString(), notification);
  }

  /**
   * Marks the notification as read.
   *
   * @param notificationId the notification id
   */
  public void markNotificationAsRead(Long notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
    notification.setRead(true);
    notificationRepository.save(notification);
  }


}
