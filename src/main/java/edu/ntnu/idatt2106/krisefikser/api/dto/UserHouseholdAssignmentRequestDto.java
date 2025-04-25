package edu.ntnu.idatt2106.krisefikser.api.dto;

public class UserHouseholdAssignmentRequestDto {
  private String email;
  private Long newHouseholdId;

  public String getEmail() {
    return email;
  }

  public Long getNewHouseholdId() {
    return newHouseholdId;
  }
}
