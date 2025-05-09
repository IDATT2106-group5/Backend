package edu.ntnu.idatt2106.krisefikser.persistance.entity.mapicon;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.MapIconType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * The type Map icon.
 */
@Entity
@Table(name = "map_icon")
public class MapIcon {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private MapIconType type;

  @Column
  private String address;

  @Column
  private Double latitude;

  @Column
  private Double longitude;

  @Column(length = 1000)
  private String description;

  @Column
  private String openingHours;

  @Column(length = 255)
  private String contactInfo;

  /**
   * Gets the ID of the map icon.
   *
   * @return the ID of the map icon
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the map icon.
   *
   * @param id the ID of the map icon
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the MapIconType of the map icon.
   *
   * @return the MapIconType of the map icon
   */
  public MapIconType getType() {
    return type;
  }

  /**
   * Sets the MapIconType of the map icon.
   *
   * @param type the MapIconType of the map icon
   */
  public void setType(MapIconType type) {
    this.type = type;
  }

  /**
   * Gets the address of the map icon.
   *
   * @return the address of the map icon
   */
  public String getAddress() {
    return address;
  }

  /**
   * Sets the address of the map icon.
   *
   * @param address the address of the map icon
   */
  public void setAddress(String address) {
    this.address = address;
  }

  /**
   * Gets the latitude of the map icon.
   *
   * @return the latitude of the map icon
   */
  public Double getLatitude() {
    return latitude;
  }

  /**
   * Sets the latitude of the map icon.
   *
   * @param latitude the latitude of the map icon
   */
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  /**
   * Gets the longitude of the map icon.
   *
   * @return the longitude of the map icon
   */
  public Double getLongitude() {
    return longitude;
  }

  /**
   * Sets the longitude of the map icon.
   *
   * @param longitude the longitude of the map icon
   */
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  /**
   * Gets the description of the map icon.
   *
   * @return the description of the map icon
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the description of the map icon.
   *
   * @param description the description of the map icon
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the opening hours of the map icon.
   *
   * @return the opening hours of the map icon
   */
  public String getOpeningHours() {
    return openingHours;
  }

  /**
   * Sets the opening hours of the map icon.
   *
   * @param openingHours the opening hours of the map icon
   */
  public void setOpeningHours(String openingHours) {
    this.openingHours = openingHours;
  }

  /**
   * Gets the contact information of the map icon.
   *
   * @return the contact information of the map icon
   */
  public String getContactInfo() {
    return contactInfo;
  }

  /**
   * Sets the contact information of the map icon.
   *
   * @param contactInfo the contact information of the map icon
   */
  public void setContactInfo(String contactInfo) {
    this.contactInfo = contactInfo;
  }
}
