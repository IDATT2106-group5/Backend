package edu.ntnu.idatt2106.krisefikser.api.controller.websocket;

import edu.ntnu.idatt2106.krisefikser.api.dto.PositionDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
import edu.ntnu.idatt2106.krisefikser.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Controller
public class WebSocketController {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(WebSocketController.class);

  private final NotificationService notificationService;
  private final UserService userService;

  public WebSocketController(NotificationService notificationService, UserService userService,
                             UserService userService1) {
    this.notificationService = notificationService;
    this.userService = userService1;
  }

  @MessageMapping("/notification")
  public void processNotification(@Payload NotificationDto notification) {
    if (notification.getRecipientId() != null) {
      notificationService.sendPrivateNotification(
          notification.getRecipientId(), notification);
    } else {
      notificationService.broadcastNotification(notification);
    }
  }

  /**
   * Updates a user's position.
   *
   * @param position
   */

  @MessageMapping("/position")
  public void updatePosition(@Payload PositionDto position) {
    try {
      logger.info("Received position update: {}", position);
      userService.updatePosition(position);
    } catch (IllegalArgumentException e) {
      logger.error("Error updating position: {}", e.getMessage());
    } catch (Exception e) {
      logger.error("Error updating position", e);
    }
  }
}