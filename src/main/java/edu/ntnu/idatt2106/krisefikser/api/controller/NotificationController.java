package edu.ntnu.idatt2106.krisefikser.api.controller;

import edu.ntnu.idatt2106.krisefikser.api.dto.notification.CreateNotificationRequestDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.notification.NotificationResponseDto;
import edu.ntnu.idatt2106.krisefikser.api.dto.user.GetUserInfoRequestDto;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import edu.ntnu.idatt2106.krisefikser.service.NotificationService;
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
 * Controller for handling notification-related requests. This includes creating, updating,
 * deleting, and retrieving notifications.
 */

@Tag(name = "Notification", description = "Endpoints for managing notifications")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;
  private final Logger logger = LoggerFactory.getLogger(NotificationController.class);

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  /**
   * Retrieves notifications for a user.
   *
   * @param request the request containing the user ID
   * @return a list of all notifications for the user
   */

  @PostMapping("/get")
  public ResponseEntity<?> getNotifications(@RequestBody GetUserInfoRequestDto request) {
    try {
      List<NotificationResponseDto> notifications =
          notificationService.getUserNotifications(request.getUserId());
      logger.info("Retrieved notifications for user: {}", request.getUserId());
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
   * Creates a new notification for a user.
   *
   * @param notification the notification metadata.
   * @return a response entity with a message
   */

  @PostMapping("/create/stock-control")
  public ResponseEntity<?> createUserNotification(
      @RequestBody CreateNotificationRequestDto notification) {
    try {
      notificationService.saveNotification(notification, NotificationType.STOCK_CONTROL);
      logger.info("Created notification for user: {}", notification.getRecipientId());
      return ResponseEntity.ok(Map.of("message", "Notification created successfully"));
    } catch (IllegalArgumentException e) {
      logger.warn("Validation error creating notification: {}", e.getMessage());
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      logger.error("Unexpected error creating notification: {}", e.getMessage(), e);
      return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
    }
  }

  /**
   * Marks a notification as read. This is used to update the notification status in the database.
   *
   * @param notificationId The ID of the notification to mark as read.
   * @return A response entity with a message.
   */
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
