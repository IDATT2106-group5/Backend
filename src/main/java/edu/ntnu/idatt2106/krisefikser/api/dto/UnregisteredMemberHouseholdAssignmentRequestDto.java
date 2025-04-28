package edu.ntnu.idatt2106.krisefikser.api.dto;

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
