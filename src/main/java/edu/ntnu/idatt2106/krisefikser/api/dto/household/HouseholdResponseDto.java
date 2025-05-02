package edu.ntnu.idatt2106.krisefikser.api.dto.household;

import edu.ntnu.idatt2106.krisefikser.api.dto.user.UserResponseDto;

/**
 * A simple DTO for Household, to avoid infinite loops and expose only safe data.
 */
public class HouseholdResponseDto {

  private final Long id;
  private final String name;
  private final String address;
  private final UserResponseDto owner;

  /**
   * Constructor for HouseholdResponseDto.
   *
   * @param id      The ID of the household.
   * @param name    The name of the household.
   * @param address The address of the household.
   * @param owner   The owner of the household.
   */
  public HouseholdResponseDto(Long id, String name, String address, UserResponseDto owner) {
    this.id = id;
    this.name = name;
    this.address = address;
    this.owner = owner;
  }

  public Long getId() {
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
