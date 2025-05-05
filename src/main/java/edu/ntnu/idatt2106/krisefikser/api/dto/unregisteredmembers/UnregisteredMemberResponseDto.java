package edu.ntnu.idatt2106.krisefikser.api.dto.unregisteredmembers;

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

  public String getFullName() {
    return fullName;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}
