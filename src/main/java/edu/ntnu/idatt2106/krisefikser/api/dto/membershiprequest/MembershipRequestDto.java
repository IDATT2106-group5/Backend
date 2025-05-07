package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

/**
 * The type Membership request dto.
 */
public class MembershipRequestDto {
  private String userId;
  private String householdId;

  /**
   * Gets sender email.
   *
   * @return the sender email
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets the sender email.
   *
   * @param userId the user id
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets household name.
   *
   * @return the household name
   */
  public String getHouseholdId() {
    return householdId;
  }

  /**
   * Sets the household name.
   *
   * @param householdId the household id
   */
  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }
}