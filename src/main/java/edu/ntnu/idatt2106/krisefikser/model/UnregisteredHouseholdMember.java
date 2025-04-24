package edu.ntnu.idatt2106.krisefikser.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

/**
 * The type Unregistered household member.
 */
@Entity
@Table(name = "unregistered_household_member")
public class UnregisteredHouseholdMember {
  /**
   * The id of the unregistered household member
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The full name of the unregistered member
   */
  @Column(nullable = false)
  private String fullName;

  /**
   * The household of the unregistered member
   */
  @JoinColumn(name = "household_id")
  @OneToOne(optional = false)
  private Household household;
}
