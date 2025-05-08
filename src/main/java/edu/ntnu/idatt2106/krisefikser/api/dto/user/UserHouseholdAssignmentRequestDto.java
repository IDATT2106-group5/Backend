package edu.ntnu.idatt2106.krisefikser.api.dto.user;

/**
 * Data Transfer Object for the request to assign a user to a household. Contains the email of the
 * user and the ID of the household to which the user is being assigned.
 */
public class UserHouseholdAssignmentRequestDto {

  private String userId;
  private String householdId;

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

}

