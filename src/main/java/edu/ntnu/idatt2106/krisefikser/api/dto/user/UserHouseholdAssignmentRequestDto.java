package edu.ntnu.idatt2106.krisefikser.api.dto.user;

/**
 * Data Transfer Object for the request to assign a user to a household. Contains the email of the
 * user and the ID of the household to which the user is being assigned.
 */
public class UserHouseholdAssignmentRequestDto {

  private Long userId;
  private Long householdId;

  public Long getUserId() {
    return userId;
  }

  public Long getHouseholdId() {
    return householdId;
  }
}
