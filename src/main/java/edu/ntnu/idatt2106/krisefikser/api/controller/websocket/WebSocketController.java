package edu.ntnu.idatt2106.krisefikser.api.controller.websocket;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationDto;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Controller
public class WebSocketController {

    private final NotificationService notificationService;

    public WebSocketController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

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