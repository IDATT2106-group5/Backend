package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * The type Membership request dto.
 */
public class MembershipRequestDto {
  private String userEmail;
  private String householdName;

  /**
   * Gets sender email.
   *
   * @return the sender email
   */
  public String getUserEmail() {
    return userEmail;
  }

  /**
   * Sets the sender email.
   *
   * @param userEmail the sender email
   */
  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  /**
   * Gets household name.
   *
   * @return the household name
   */
  public String getHouseholdName() {
    return householdName;
  }

  /**
   * Sets the household name.
   *
   * @param householdName the household name
   */
  public void setHouseholdName(String householdName) {
    this.householdName = householdName;
  }
}