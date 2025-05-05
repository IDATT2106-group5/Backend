package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * The type Notification.
 */
@Entity
@Table(name = "notification")
public class Notification {

  /**
   * The notification id.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The user id.
   */
  @JoinColumn(name = "user_id")
  @OneToOne(optional = false)
  private User user;

  /**
   * The type of notification.
   */
  @Column(name = "type")
  @Enumerated(EnumType.STRING)
  private NotificationType type;


  @Column(name = "is_read")
  private Boolean isRead;

  @Column(name = "message")
  private String message;


  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  // Update getters and setters
  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  /**
   * The timestamp of the notification.
   */
  @Column(name = "timestamp", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime timestamp;

  /**
   * Gets timestamp.
   *
   * @return the timestamp
   */
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  /**
   * Sets timestamp.
   *
   * @param timestamp the timestamp
   */
  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets user.
   *
   * @return the user
   */
  public User getUser() {
    return user;
  }

  /**
   * Sets user.
   *
   * @param user the user
   */
  public void setUser(User user) {
    this.user = user;
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public NotificationType getType() {
    return type;
  }

  /**
   * Sets type.
   *
   * @param type the type
   */
  public void setType(NotificationType type) {
    this.type = type;
  }

}
