package edu.ntnu.idatt2106.krisefikser.persistance.entity.item;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.ItemType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type Item.
 */
@Entity
@Table(name = "item")
public class Item {

  /**
   * The item id.
   */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the item.
   */
  @Column
  private String name;

  /**
   * The type of item.
   */
  @Column
  @Enumerated(EnumType.STRING)
  private ItemType itemType;

  /**
   * The caloric value of the item.
   */
  @Column
  private int caloricAmount;

  /**
   * Instantiates a new Item.
   */
  public Item() {

  }

  /**
   * Instantiates a new Item with all fields.
   */
  public Item(String name, int caloricAmount, ItemType itemType) {
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

  public ItemType getItemType() {
    return itemType;
  }

  public void setItemType(ItemType itemType) {
    this.itemType = itemType;
  }

  public int getCaloricAmount() {
    return caloricAmount;
  }

  public void setCaloricAmount(int caloricAmount) {
    this.caloricAmount = caloricAmount;
  }
}
