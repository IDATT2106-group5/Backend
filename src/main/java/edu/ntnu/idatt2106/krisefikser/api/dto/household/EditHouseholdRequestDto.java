package edu.ntnu.idatt2106.krisefikser.api.dto.household;

/**
 * The type Edit household request dto.
 */
public class EditHouseholdRequestDto {
  private String name;
  private String address;


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
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets address.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets address.
   *
   * @param address the address
   */
  public void setAddress(String address) {
    this.address = address;
  }
}