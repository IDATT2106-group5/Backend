package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.Item;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class StorageItemResponseDto {
  Item item;
  Long householdId;
  String unit;
  int amount;
  LocalDateTime expiration;

  public StorageItemResponseDto(Item item, Long householdId, String unit, int amount,
                                LocalDateTime expiration) {
    this.item = item;
    this.householdId = householdId;
    this.unit = unit;
    this.amount = amount;
    this.expiration = expiration;
  }

  public Item getItem() {
    return item;
  }

  public Long getHouseholdId() {
    return householdId;
  }

  public String getUnit() {
    return unit;
  }

  public int getAmount() {
    return amount;
  }

  public LocalDateTime getExpiration() {
    return expiration;
  }

  public void setItem(Item item) {
    this.item = item;
  }

  public void setHouseholdId(Long householdId) {
    this.householdId = householdId;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public void setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
  }

}
