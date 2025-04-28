package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * Data Transfer Object for the request to assign an unregistered member to a household. Contains
 * the full name of the unregistered member and the ID of the household to which the member is being
 * assigned.
 */
public class UnregisteredMemberHouseholdAssignmentRequestDto {

  private String fullName;
  private Long householdId;

  public String getFullName() {
    return fullName;
  }

  public Long getHouseholdId() {
    return householdId;
  }
}
