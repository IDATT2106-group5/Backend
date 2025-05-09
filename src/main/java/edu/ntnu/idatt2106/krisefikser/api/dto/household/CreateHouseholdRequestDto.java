package edu.ntnu.idatt2106.krisefikser.api.dto.household;

/**
 * Data Transfer Object for creating a household. Contains the name, address, and owner ID of the
 * household.
 */
public class CreateHouseholdRequestDto {

  private String name;
  private String address;

  /**
   * Gets address.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param testHousehold the test household
   */
  public void setName(String testHousehold) {
    this.name = testHousehold;
  }

  /**
   * Sets address.
   *
   * @param testAddress the test address
   */
  public void setAddress(String testAddress) {
    this.address = testAddress;
  }
}
