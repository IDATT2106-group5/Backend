package edu.ntnu.idatt2106.krisefikser.api.dto.household;

public class DeleteHouseholdRequestDto {
  private String  householdId;
  private String ownerId;

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }
}
