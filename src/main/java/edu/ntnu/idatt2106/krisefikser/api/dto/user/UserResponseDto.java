package edu.ntnu.idatt2106.krisefikser.api.dto.user;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;

/**
 * A Data Transfer Object for User.
 */
public class UserResponseDto {

  private final String id;
  private final String email;
  private final String fullName;
  private final String tlf;
  private final Role role;

  /**
   * Instantiates a new User response dto.
   */

  public UserResponseDto(String id, String email, String fullName, String tlf,
      Role role) {
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.tlf = tlf;
    this.role = role;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getFullName() {
    return fullName;
  }

  public Role getRole() {
    return role;
  }

  public String getTlf() {
    return tlf;
  }

}
