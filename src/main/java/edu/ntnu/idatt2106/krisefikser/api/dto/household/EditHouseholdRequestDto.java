package edu.ntnu.idatt2106.krisefikser.api.dto.household;

public class EditHouseholdRequestDto {
  private Long householdId;
  private String name;
  private String address;

  public String getAddress() {
    return address;
  }

  public String getName() {
    return name;
  }

  public Long getHouseholdId() {
    return householdId;
  }
}
