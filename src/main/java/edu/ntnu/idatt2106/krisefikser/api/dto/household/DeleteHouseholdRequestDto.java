package edu.ntnu.idatt2106.krisefikser.api.dto.household;

public class DeleteHouseholdRequestDto {
  private String  householdId;
  private Long ownerId;

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }
}
