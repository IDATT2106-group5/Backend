package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;

public class ItemResponseDto {
  private Long id;
  private String name;
  private int caloricAmount;
  private ItemType itemType;

  public ItemResponseDto(Long id, String name, int caloricAmount, ItemType itemType) {
    this.id = id;
    this.name = name;
    this.caloricAmount = caloricAmount;
    this.itemType = itemType;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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
