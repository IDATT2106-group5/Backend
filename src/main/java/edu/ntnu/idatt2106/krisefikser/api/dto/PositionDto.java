package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * The type Position dto.
 */
public class PositionDto {

  /**
   * The Longitude.
   */
  String longitude;
  /**
   * The Latitude.
   */
  String latitude;
  /**
   * The user token.
   */
  String token;

  /**
   * Instantiates a new Position dto.
   *
   * @param token     the token
   * @param longitude the longitude
   * @param latitude  the latitude
   */
  public PositionDto(String token, String longitude, String latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
    this.token = token;
  }

  /**
   * Instantiates a new Position dto.
   */
  public PositionDto() {
  }

  /**
   * Gets token.
   *
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * Sets token.
   *
   * @param token the token
   */
  public void setToken(String token) {
    this.token = token;
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
