package edu.ntnu.idatt2106.krisefikser.api.dto.item;

import edu.ntnu.idatt2106.krisefikser.persistance.entity.item.Item;
import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;

/**
 * A simple DTO for Item, to avoid infinite loops and expose only safe data.
 */

public class ItemResponseDto {

  private Long id;
  private String name;
  private int caloricAmount;
  private ItemType itemType;

  /**
   * Default constructor for ItemResponseDto.
   */

  public ItemResponseDto() {
  }

  /**
   * Constructor for ItemResponseDto.
   *
   * @param id            the id
   * @param name          the name
   * @param caloricAmount the caloric amount
   * @param itemType      the item type
   */

  public ItemResponseDto(Long id, String name, int caloricAmount, ItemType itemType) {
    this.id = id;
    this.name = name;
    this.caloricAmount = caloricAmount;
    this.itemType = itemType;
  }

  /**
   * Converts an Item entity to an ItemResponseDto.
   *
   * @param item the item entity
   * @return the ItemResponseDto
   */
  
  public static ItemResponseDto fromEntity(Item item) {
    return new ItemResponseDto(
        item.getId(),
        item.getName(),
        item.getCaloricAmount(),
        item.getItemType()
    );
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
