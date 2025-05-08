package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.GetUserInfoRequestDto;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling notification related requests.
 */
@Tag(name = "Notification", description = "Endpoints for managing notifications")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final Logger logger = LoggerFactory.getLogger(NotificationController.class);

  /**
   * Constructor for NotificationController.
   *
   * @param notificationService the service for managing notifications
   */
  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**
   * Retrieves notifications for a user.
   *
   * @return a list of all notifications for the user
   */
  @Operation(summary = "Gets notifications for a user",
      description = "Gets notifications for a user with a given id")
  @PostMapping("/get")
  public ResponseEntity<?> getNotifications() {
    try {
      List<NotificationResponseDto> notifications =
          notificationService.getUserNotifications();
      logger.info("Retrieved notifications for user");
      return ResponseEntity.ok(notifications);
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error retrieving notifications: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error retrieving notifications: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Marks a notification as read.
   *
   * @param notificationId the ID of the notification to mark as read
   * @return a response entity indicating the result of the operation
   */
  @Operation(summary = "Marks a notification as read",
      description = "Marks a notification with a given id as read")
  @PutMapping("/{notificationId}/read")
  public ResponseEntity<?> markNotificationAsRead(@PathVariable Long notificationId) {
    try {
      notificationService.markNotificationAsRead(notificationId);
      logger.info("Marked notification as read: {}", notificationId);
      return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error marking notification as read: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error marking notification as read: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }
}
