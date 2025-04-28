package edu.ntnu.idatt2106.krisefikser.persistance.entity;

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
   * The id of the unregistered household member.
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The full name of the unregistered member.
   */
  @Column(nullable = false)
  private String fullName;

  /**
   * The household of the unregistered member.
   */
  @JoinColumn(name = "household_id")
  @OneToOne(optional = false)
  private Household household;

  /**
   * Instantiates a new Unregistered household member.
   */
  public UnregisteredHouseholdMember() {
  }

  /**
   * Instantiates a new Unregistered household member.
   *
   * @param fullName  the full name
   * @param household the household
   */
  public UnregisteredHouseholdMember(String fullName, Household household) {
    this.fullName = fullName;
    this.household = household;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the id
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Gets the full name.
   *
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets the full name.
   *
   * @param fullName the full name
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Gets the household.
   *
   * @return the household
   */
  public Household getHousehold() {
    return household;
  }

  /**
   * Sets the household.
   *
   * @param household the household
   */
  public void setHousehold(Household household) {
    this.household = household;
  }
}
