package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for notifications. This class is used to encapsulate the data sent
 * from the server to the client when a user receives a notification.
 */

public class NotificationDto {

  private NotificationType type;
  private String recipientId;
  private LocalDateTime timestamp;
  private boolean read;
  private String message;

  /**
   * Default constructor for NotificationDto.
   */

  public NotificationDto(NotificationType type, String recipientId, LocalDateTime timestamp,
      boolean read, String message) {
    this.type = type;
    this.recipientId = recipientId;
    this.timestamp = timestamp;
    this.read = read;
    this.message = message;
  }

  /**
   * Default constructor for NotificationDto.
   */

  public NotificationDto() {
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public NotificationType getType() {
    return type;
  }

  public void setType(NotificationType type) {
    this.type = type;
  }


  public String getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(String recipientId) {
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
