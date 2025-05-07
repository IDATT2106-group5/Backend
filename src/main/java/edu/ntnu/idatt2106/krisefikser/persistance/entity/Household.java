package edu.ntnu.idatt2106.krisefikser.persistance.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Household.
 */
@Entity
@Table(name = "household")
public class Household {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private String id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String address;

  @Column(nullable = false)
  private int numberOfMembers;

  @OneToOne
  @JoinColumn(nullable = false)
  private User owner;

  @OneToMany(mappedBy = "household", cascade = CascadeType.ALL)
  private List<StorageItem> storageItems = new ArrayList<>();

  /**
   * Gets storage items.
   *
   * @return the storage items
   */
  public List<StorageItem> getStorageItems() {
    return storageItems;
  }

  /**
   * Sets storage items.
   *
   * @param storageItems the storage items
   */
  public void setStorageItems(
      List<StorageItem> storageItems) {
    this.storageItems = storageItems;
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets id.
   *
   * @param id the id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets name.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets address.
   *
   * @return the address
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets address.
   *
   * @param address the address
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Gets number of members.
   *
   * @return the number of members
   */
  public int getNumberOfMembers() {
    return numberOfMembers;
  }

  /**
   * Sets number of members.
   *
   * @param numberOfMembers the number of members
   */
  public void setNumberOfMembers(int numberOfMembers) {
    this.numberOfMembers = numberOfMembers;
  }

  /**
   * Gets owner.
   *
   * @return the owner
   */
  public User getOwner() {
    return owner;
  }

  /**
   * Sets owner.
   *
   * @param owner the owner
   */
  public void setOwner(User owner) {
    this.owner = owner;
  }

  /**
   * Instantiates a new Household.
   */
  public Household() {
  }

  /**
   * Instantiates a new Household.
   *
   * @param name            the name
   * @param address         the address
   * @param numberOfMembers the number of members
   * @param owner           the owner
   */
  public Household(String name, String address, int numberOfMembers, User owner) {
    this.name = name;
    this.address = address;
    this.numberOfMembers = numberOfMembers;
    this.owner = owner;
  }
}
