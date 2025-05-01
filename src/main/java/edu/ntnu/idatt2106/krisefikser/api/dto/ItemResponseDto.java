package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;

public class ItemResponseDto {
  String name;
  ItemType itemType;
  int caloricAmount;

  public ItemResponseDto(String name, int caloricAmount, ItemType itemType) {
    this.name = name;
    this.caloricAmount = caloricAmount;
    this.itemType = itemType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCaloricAmount() {
    return caloricAmount;
  }

  public void setCaloricAmount(int caloricAmount) {
    this.caloricAmount = caloricAmount;
  }

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }
}
