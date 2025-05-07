package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

public class MembershipInviteDto {
  private String householdId;
  private String email;

  public MembershipInviteDto(String email, String householdId) {
    this.email = email;
    this.householdId = householdId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }
}