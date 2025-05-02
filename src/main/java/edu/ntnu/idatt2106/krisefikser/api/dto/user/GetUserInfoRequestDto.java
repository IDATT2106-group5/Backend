package edu.ntnu.idatt2106.krisefikser.api.dto.user;

/**
 * Data Transfer Object for the request to get user information. Contains the user ID.
 */
public class GetUserInfoRequestDto {

  private Long userId;

  public Long getUserId() {
    return userId;
  }
}
