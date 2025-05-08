package edu.ntnu.idatt2106.krisefikser.api.dto.household;

/**
 * Data Transfer Object for creating a household. Contains the name, address, and owner ID of the
 * household.
 */
public class CreateHouseholdRequestDto {

  private String name;
  private String address;

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

}
