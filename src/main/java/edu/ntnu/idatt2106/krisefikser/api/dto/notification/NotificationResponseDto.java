package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import java.time.LocalDateTime;

public class NotificationResponseDto {
  private Long id;
  private NotificationType type;
  private String recipientId;
  private LocalDateTime timestamp;
  private boolean read;
  private String message;

  public NotificationResponseDto(Long id, NotificationType type, String recipientId,
                                 LocalDateTime timestamp, String message, boolean read) {
    this.type = type;
    this.recipientId = recipientId;
    this.timestamp = timestamp;
    this.read = read;
    this.id = id;
    this.message = message;
  }

  public NotificationResponseDto() {
  }

  public NotificationType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
