package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for notifications. Contains the ID, type of notification, recipient ID,
 * timestamp, and read status of the notification.
 */

public class NotificationResponseDto {

  private Long id;
  private NotificationType type;
  private Long recipientId;
  private LocalDateTime timestamp;
  private boolean read;

  /**
   * Constructor for creating a notification DTO. Contains the ID, type of notification,
   *
   * @param id          The ID of the notification.
   * @param type        The type of notification.
   * @param recipientId The ID of the recipient.
   * @param timestamp   The timestamp of the notification.
   * @param read        The read status of the notification.
   */
  public NotificationResponseDto(Long id, NotificationType type, Long recipientId,
      LocalDateTime timestamp,
      boolean read) {
    this.type = type;
    this.recipientId = recipientId;
    this.timestamp = timestamp;
    this.read = read;
    this.id = id;
  }

  public NotificationResponseDto() {
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(Long recipientId) {
    this.recipientId = recipientId;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

}
