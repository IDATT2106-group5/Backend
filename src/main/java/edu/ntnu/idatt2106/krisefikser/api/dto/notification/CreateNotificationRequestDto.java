package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

/**
 * Data Transfer Object for creating a notification. Contains the recipient ID and read status of
 * the notification.
 */
public class CreateNotificationRequestDto {

  private Long recipientId;
  private boolean read;

  public CreateNotificationRequestDto() {
  }

  public Long getRecipientId() {
    return recipientId;
  }

  public void setRecipientId(Long recipientId) {
    this.recipientId = recipientId;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }
}
