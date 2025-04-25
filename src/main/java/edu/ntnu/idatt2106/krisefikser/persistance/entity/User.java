package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * The type User.
 */
@Entity
@Table(name = "user")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String fullName;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @ManyToOne
  @JoinColumn(name = "household_id")
  private Household household;

  @Column(name = "tlf")
  private String tlf;

  @Column(nullable = false)
  private boolean confirmed = false;

  @Column(name = "confirmation_token", unique = true)
  private String confirmationToken;


  /**
   * Instantiates a new User.
   */
  public User() {
  }

  /**
   * Instantiates a new User.
   *
   * @param email     the email
   * @param password  the password
   * @param fullName  the full name
   * @param role      the role
   * @param household the household
   * @param tlf       the tlf
   */
  public User(String email, String password, String fullName, Role role, Household household,
      String tlf, boolean confirmed) {
    this.email = email;
    this.password = password;
    this.fullName = fullName;
    this.role = role;
    this.household = household;
    this.tlf = tlf;
    this.confirmed = confirmed;
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
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets full name.
   *
   * @param fullName the full name
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Gets role.
   *
   * @return the role
   */
  public Role getRole() {
    return role;
  }

  /**
   * Sets role.
   *
   * @param role the role
   */
  public void setRole(Role role) {
    this.role = role;
  }

  /**
   * Gets household.
   *
   * @return the household
   */
  public Household getHousehold() {
    return household;
  }

  /**
   * Sets household.
   *
   * @param household the household
   */
  public void setHousehold(Household household) {
    this.household = household;
  }

  /**
   * Gets tlf.
   *
   * @return the tlf
   */
  public String getTlf() {
    return tlf;
  }

  /**
   * Sets tlf.
   *
   * @param tlf the tlf
   */
  public void setTlf(String tlf) {
    this.tlf = tlf;
  }

  /**
   * Checks if user is confirmed.
   *
   * @return the boolean
   */
  public boolean isConfirmed() {
    return confirmed;
  }

  /**
   * Sets confirmed.
   *
   * @param confirmed the confirmed
   */
  public void setConfirmed(boolean confirmed) {
    this.confirmed = confirmed;
  }

  /**
   * Gets confirmation token.
   *
   * @return the confirmation token
   */
  public String getConfirmationToken() {
    return confirmationToken;
  }

  /**
   * Sets confirmation token.
   *
   * @param confirmationToken the confirmation token
   */
  public void setConfirmationToken(String confirmationToken) {
    this.confirmationToken = confirmationToken;
  }
}