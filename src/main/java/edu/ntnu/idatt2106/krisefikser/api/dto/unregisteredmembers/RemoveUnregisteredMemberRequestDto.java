package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * Data Transfer Object for removing an unregistered member. Contains the member ID.
 */

public class RemoveUnregisteredMemberRequestDto {

  private Long memberId;

  public Long getMemberId() {
    return memberId;
  }

  public void setId(Long id) {
    this.memberId = memberId;
  }
}
