package edu.ntnu.idatt2106.krisefikser.api.dto.household;

public class DeleteHouseholdRequestDto {
  private Long householdId;
  private Long ownerId;

  public Long getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }
}
