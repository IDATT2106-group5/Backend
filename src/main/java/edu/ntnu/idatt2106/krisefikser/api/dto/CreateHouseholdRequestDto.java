package edu.ntnu.idatt2106.krisefikser.api.dto;

public class CreateHouseholdRequestDto {
  private String name;
  private String address;
  private Long ownerId; // The email of the user creating the household

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public Long getOwnerId() {
    return ownerId;
  }
}
