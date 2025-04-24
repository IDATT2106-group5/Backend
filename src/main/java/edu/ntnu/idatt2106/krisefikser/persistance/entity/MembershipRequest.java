package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.security.Timestamp;

@Entity
@Table(name = "membership_requests")
public class MembershipRequest {

  @Id
  private Long id;

  @JoinColumn(name = "household_id")
  @OneToOne(optional = false)
  private Household household;

  @JoinColumn(name = "sender_id")
  @OneToOne(optional = false)
  private User sender;

  @JoinColumn(name = "receiver_id")
  @OneToOne(optional = false)
  private User receiver;

  @Column
  @Enumerated(EnumType.STRING)
  private String type;

  @Column
  @Enumerated(EnumType.STRING)
  private String status;

  @Column
  private Timestamp created_at;
}
