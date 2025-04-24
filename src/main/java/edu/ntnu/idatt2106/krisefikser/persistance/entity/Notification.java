package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification")
public class Notification {
  /**
   * The notification id
   */
  @Id
  private Long id;

  /**
   * The user id
   */
  @JoinColumn(name = "user_id")
  @OneToOne(optional = false)
  private User user;

  /**
   * The type of notification
   */
  private String type;

  /**
   * The read status of the notification
   */
  private boolean read;
}
