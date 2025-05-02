package edu.ntnu.idatt2106.krisefikser.api.dto.user;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;

/**
 * A Data Transfer Object for User.
 */
public class UserResponseDto {

  private final Long id;
  private final String email;
  private final String fullName;
  private final String tlf;
  private final Role role;

  /**
   * Constructor for UserResponseDto.
   *
   * @param id       The ID of the user.
   * @param email    The email of the user.
   * @param fullName The full name of the user.
   * @param tlf      The telephone number of the user.
   * @param role     The role of the user.
   */
  public UserResponseDto(Long id, String email, String fullName, String tlf,
      Role role) {
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.tlf = tlf;
    this.role = role;
  }

  public Long getId() {
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
