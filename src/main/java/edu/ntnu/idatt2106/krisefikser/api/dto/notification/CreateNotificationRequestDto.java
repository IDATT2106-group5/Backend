package edu.ntnu.idatt2106.krisefikser.api.dto.notification;

import java.time.LocalDateTime;

public class CreateNotificationRequestDto {
  private Long recipientId;
  private boolean read;

  public CreateNotificationRequestDto() {
  }

  public Long getRecipientId() {
    return recipientId;
  }

  public boolean isRead() {
    return read;
  }

  public void setRead(boolean read) {
    this.read = read;
  }

  public void setRecipientId(Long recipientId) {
    this.recipientId = recipientId;
  }
}
