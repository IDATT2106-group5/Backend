package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * Data Transfer Object for the response containing unregistered member information. Contains the ID
 * and full name of the unregistered member.
 */

public class UnregisteredMemberResponseDto {

  private Long id;
  private String fullName;

  public UnregisteredMemberResponseDto(Long id, String fullName) {
    this.id = id;
    this.fullName = fullName;
  }

  public Long getId() {

    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
