package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestStatus;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.RequestType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.security.Timestamp;

/**
 * The type Membership request.
 */
@Entity
@Table(name = "membership_request")
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
  private RequestType type;

  @Column
  @Enumerated(EnumType.STRING)
  private RequestStatus status;

  @Column
  private Timestamp createdAt;
}
