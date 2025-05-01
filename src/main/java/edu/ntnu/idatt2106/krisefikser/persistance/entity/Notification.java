package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.NotificationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

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
  private NotificationType type;

  /**
   * The read status of the notification.
   */
  private boolean read;

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

  /**
   * Is read boolean.
   *
   * @return the boolean
   */
  public boolean isRead() {
    return read;
  }

  /**
   * Sets read.
   *
   * @param read the read
   */
  public void setRead(boolean read) {
    this.read = read;
  }


}
