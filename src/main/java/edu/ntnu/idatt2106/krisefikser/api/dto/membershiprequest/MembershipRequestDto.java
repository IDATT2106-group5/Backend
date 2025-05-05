package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

/**
 * The type Membership request dto.
 */
public class MembershipRequestDto {
  private Long userId;
  private Long householdId;

  /**
   * Gets sender email.
   *
   * @return the sender email
   */
  public Long getUserId() {
    return userId;
  }

  /**
   * Sets the sender email.
   *
   * @param userId the user id
   */
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  /**
   * Gets household name.
   *
   * @return the household name
   */
  public Long getHouseholdId() {
    return householdId;
  }

  /**
   * Sets the household name.
   *
   * @param householdId the household id
   */
  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }
}