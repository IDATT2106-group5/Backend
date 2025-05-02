package edu.ntnu.idatt2106.krisefikser.api.dto;

import java.time.LocalDateTime;

/**
 * A Data Transfer Object for StorageItem.
 */

public class StorageItemResponseDto {

  Long itemId;
  ItemResponseDto item;
  Long householdId;
  String unit;
  int amount;
  LocalDateTime expiration;

  /**
   * Constructor for StorageItemResponseDto.
   *
   * @param itemId      The ID of the storage item.
   * @param item        The item associated with the storage item.
   * @param householdId The ID of the household associated with the storage item.
   * @param unit        The unit of measurement for the storage item.
   * @param amount      The amount of the storage item.
   * @param expiration  The expiration date and time of the storage item.
   */
  public StorageItemResponseDto(Long itemId, ItemResponseDto item, Long householdId, String unit,
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

  public Long getHouseholdId() {
    return householdId;
  }

  public void setHouseholdId(Long householdId) {
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
