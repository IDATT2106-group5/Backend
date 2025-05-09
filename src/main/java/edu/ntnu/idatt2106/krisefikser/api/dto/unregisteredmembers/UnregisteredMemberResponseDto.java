package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

/**
 * The type Remove unregistered member request dto.
 */

public class UnregisteredMemberResponseDto {

  private Long id;
  private String fullName;

  /**
   * Instantiates a new Unregistered member response dto.
   */
  
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
