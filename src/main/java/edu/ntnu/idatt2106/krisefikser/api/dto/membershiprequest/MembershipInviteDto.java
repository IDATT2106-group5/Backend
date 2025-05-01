package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

public class MembershipInviteDto {
  private Long householdId;
  private String email;

  public MembershipInviteDto(String email, Long householdId) {
    this.email = email;
    this.householdId = householdId;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Long getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }
}