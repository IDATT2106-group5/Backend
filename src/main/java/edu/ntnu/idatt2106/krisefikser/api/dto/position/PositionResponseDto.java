package edu.ntnu.idatt2106.krisefikser.api.dto.position;

/**
 * The type Position dto.
 */
public class PositionResponseDto {
  /**
   * The User id.
   */
  String userId;
  /**
   * The Full name.
   */
  String fullName;
  /**
   * The Longitude.
   */
  String longitude;
  /**
   * The Latitude.
   */
  String latitude;

  /**
   * Instantiates a new Position dto.
   *
   * @param userId    the user id
   * @param fullName  the full name
   * @param longitude the longitude
   * @param latitude  the latitude
   */
  public PositionResponseDto(String userId, String fullName, String longitude, String latitude) {
    this.userId = userId;
    this.fullName = fullName;
    this.longitude = longitude;
    this.latitude = latitude;
  }

  /**
   * Instantiates a new Position dto.
   */
  public PositionResponseDto() {
  }

  /**
   * Gets full name.
   *
   * @return the full name
   */
  public String getFullName() {
    return fullName;
  }

  /**
   * Sets full name.
   *
   * @param fullName the full name
   */
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  /**
   * Gets user id.
   *
   * @return the user id
   */
  public String getUserId() {
    return userId;
  }

  /**
   * Sets user id.
   *
   * @param userId the user id
   */
  public void setUserId(String userId) {
    this.userId = userId;
  }

  /**
   * Gets longitude.
   *
   * @return the longitude
   */
  public String getLongitude() {
    return longitude;
  }

  /**
   * Sets longitude.
   *
   * @param longitude the longitude
   */
  public void setLongitude(String longitude) {
    this.longitude = longitude;
  }

  /**
   * Gets latitude.
   *
   * @return the latitude
   */
  public String getLatitude() {
    return latitude;
  }

  /**
   * Sets latitude.
   *
   * @param latitude the latitude
   */
  public void setLatitude(String latitude) {
    this.latitude = latitude;
  }
}
