package edu.ntnu.idatt2106.krisefikser.api.dto.membershiprequest;

/**
 * Data Transfer Object (DTO) for membership invite requests. This class is used to encapsulate the
 * data sent from the client to the server when a user invites another user to join a group.
 */
public class MembershipInviteDto {

  private String email;

  /**
   * Default constructor for MembershipInviteDto.
   */

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