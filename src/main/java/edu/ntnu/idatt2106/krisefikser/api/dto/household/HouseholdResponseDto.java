package edu.ntnu.idatt2106.krisefikser.api.dto.household;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;

/**
 * A simple DTO for Household, to avoid infinite loops and expose only safe data.
 */
public class HouseholdResponseDto {
  private String id;
  private String name;
  private String address;
  private UserResponseDto owner;

  public HouseholdResponseDto(String id, String name, String address, UserResponseDto owner) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.owner = owner;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getAddress() {
    return address;
  }

  public UserResponseDto getOwner() {
    return owner;
  }
}
