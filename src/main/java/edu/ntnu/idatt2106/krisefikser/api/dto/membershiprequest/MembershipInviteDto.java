package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

public class MembershipInviteDto {
  private String email;

  public MembershipInviteDto(String email) {
    this.email = email;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

}