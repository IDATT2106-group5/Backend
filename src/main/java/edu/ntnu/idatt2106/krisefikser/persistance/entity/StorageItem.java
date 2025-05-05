package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * The type Storage item.
 */

@Entity
@Table(name = "storage")
public class StorageItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "household_id", nullable = false)
  private Household household;

  @ManyToOne
  @JoinColumn(name = "item_id", nullable = false)
  private Item item;

  @Column(nullable = false)
  private String unit;

  @Column(nullable = false)
  private Integer amount;

//  @Column(name = "expiration_date")
  private LocalDateTime expirationDate;

  @Column(name = "date_added", nullable = false)
  private LocalDateTime dateAdded = LocalDateTime.now();

  /**
   * Instantiates a new Storage item.
   */
  public StorageItem() {
  }

  /**
   * Instantiates a new Storage item with all fields.
   *
   * @param household      the household
   * @param item           the item
   * @param unit           the unit
   * @param amount         the amount
   * @param expirationDate the expiration date
   * @param dateAdded      the date the item was added
   */
  public StorageItem(Household household, Item item, String unit, Integer amount,
      LocalDateTime expirationDate, LocalDateTime dateAdded) {
    this.household = household;
    this.item = item;
    this.unit = unit;
    this.amount = amount;
    this.expirationDate = expirationDate;
    this.dateAdded = dateAdded;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets household.
   *
   * @return the household
   */
  public Household getHousehold() {
    return household;
  }

  /**
   * Sets household.
   *
   * @param household the household
   */
  public void setHousehold(Household household) {
    this.household = household;
  }

  /**
   * Gets item.
   *
   * @return the item
   */
  public Item getItem() {
    return item;
  }

  /**
   * Sets item.
   *
   * @param item the item
   */
  public void setItem(Item item) {
    this.item = item;
  }

  /**
   * Gets unit.
   *
   * @return the unit
   */
  public String getUnit() {
    return unit;
  }

  /**
   * Sets unit.
   *
   * @param unit the unit
   */
  public void setUnit(String unit) {
    this.unit = unit;
  }

  /**
   * Gets amount.
   *
   * @return the amount
   */
  public Integer getAmount() {
    return amount;
  }

  /**
   * Sets amount.
   *
   * @param amount the amount
   */
  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  /**
   * Gets expiration date.
   *
   * @return the expiration date
   */
  public LocalDateTime getExpirationDate() {
    return expirationDate;
  }

  /**
   * Sets expiration date.
   *
   * @param expirationDate the expiration date
   */
  public void setExpirationDate(LocalDateTime expirationDate) {
    this.expirationDate = expirationDate;
  }

  /**
   * Gets date added.
   *
   * @return the date added
   */
  public LocalDateTime getDateAdded() {
    return dateAdded;
  }

  /**
   * Sets date added.
   *
   * @param dateAdded the date added
   */
  public void setDateAdded(LocalDateTime dateAdded) {
    this.dateAdded = dateAdded;
  }
}