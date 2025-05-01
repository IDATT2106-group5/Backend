package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

import java.time.LocalDateTime;

public class NotificationDto {
  private String type;
  private String message;
  private Long recipientId;
  private LocalDateTime timestamp;
  private boolean read;

  public NotificationDto(String type, String message, Long recipientId, LocalDateTime timestamp,
                         boolean read) {
    this.type = type;
    this.message = message;
    this.recipientId = recipientId;
    this.timestamp = timestamp;
    this.read = read;
  }

  public NotificationDto() {
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
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
