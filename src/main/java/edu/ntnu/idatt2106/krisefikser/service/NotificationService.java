package edu.ntnu.idatt2106.krisefikser.service;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.NotificationRepository;
import edu.ntnu.idatt2106.krisefikser.persistance.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
  private final SimpMessagingTemplate messagingTemplate;

  private final NotificationRepository notificationRepository;

  private final UserRepository userRepository;


  public NotificationService(SimpMessagingTemplate messagingTemplate,
                             NotificationRepository notificationRepository,
                             UserRepository userRepository) {
    this.messagingTemplate = messagingTemplate;
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
  }

  public void sendPrivateNotification(String userId, NotificationDto notification) {
    messagingTemplate.convertAndSendToUser(
        userId,
        "/queue/notifications",
        notification
    );
  }

  public void broadcastNotification(NotificationDto notification) {
    messagingTemplate.convertAndSend("/topic/notifications", notification);
  }

  public void sendHouseholdNotification(String householdId, NotificationDto notification) {
    messagingTemplate.convertAndSend("/topic/household/" + householdId, notification);
  }


}
