package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * A simple DTO for Household, to avoid infinite loops and expose only safe data.
 */
public class HouseholdResponseDto {
  private Long id;
  private String name;
  private String address;
  private UserResponseDto owner;

  public HouseholdResponseDto(Long id, String name, String address, UserResponseDto owner) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.owner = owner;
  }

  public Long getId() { return id; }
  public String getName() { return name; }
  public String getAddress() { return address; }
}
