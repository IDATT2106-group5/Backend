package edu.ntnu.idatt2106.krisefikser.api.controller.websocket;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Controller for handling WebSocket notifications. This includes processing incoming notifications
 * and sending them to the appropriate recipients.
 */

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Controller
public class WebSocketController {

  private final NotificationService notificationService;

  public WebSocketController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**
   * Processes incoming notifications. If the notification has a recipient ID, it sends a private
   * notification to that user. Otherwise, it broadcasts the notification to all users.
   *
   * @param notification The notification to process.
   */
  
  @MessageMapping("/notification")
  public void processNotification(@Payload NotificationDto notification) {
    if (notification.getRecipientId() != null) {
      notificationService.sendPrivateNotification(
          notification.getRecipientId().toString(), notification);
    } else {
      notificationService.broadcastNotification(notification);
    }
  }
}