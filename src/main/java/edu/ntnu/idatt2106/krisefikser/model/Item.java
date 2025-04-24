package edu.ntnu.idatt2106.krisefikser.model;

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
   * The item id
   */

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * The name of the item
   */
  @Column
  private String name;

  /**
   * The type of item
   */
  @Column
  @Enumerated(EnumType.STRING)
  private ItemType itemType;

  /**
   * The caloric value of the item
   */
  @Column
  private int caloricAmount;
}
