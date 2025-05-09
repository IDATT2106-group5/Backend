package edu.ntnu.idatt2106.krisefikser.api.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for storage items. This class is used to encapsulate the data sent
 * from the server to the client when a user requests information about a storage item.
 */

public class StorageItemResponseDto {

  Long itemId;
  ItemResponseDto item;
  String householdId;
  String unit;
  int amount;
  LocalDateTime expiration;

  /**
   * Instantiates a new Storage item response dto.
   */
  
  public StorageItemResponseDto(Long itemId, ItemResponseDto item, String householdId, String unit,
      int amount,
      LocalDateTime expiration) {
    this.itemId = itemId;
    this.item = item;
    this.householdId = householdId;
    this.unit = unit;
    this.amount = amount;
    this.expiration = expiration;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public ItemResponseDto getItem() {
    return item;
  }

  public void setItem(ItemResponseDto item) {
    this.item = item;
  }

  public String getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(String householdId) {
    this.householdId = householdId;
  }

  public String getUnit() {
    return unit;
  }

  public void setUnit(String unit) {
    this.unit = unit;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public LocalDateTime getExpiration() {
    return expiration;
  }

  public void setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
  }

}
