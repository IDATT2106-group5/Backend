package edu.ntnu.idatt2106.krisefikser.api.dto;

import edu.ntnu.idatt2106.krisefikser.persistance.enums.Role;

/**
 * A Data Transfer Object for User.
 */
public class UserResponseDto {
  private Long id;
  private String email;
  private String fullName;
  private Role role;

  public UserResponseDto(Long id, String email, String fullName, Role role) {
    this.id = id;
    this.email = email;
    this.fullName = fullName;
    this.role = role;
  }

  public Long getId() { return id; }
  public String getEmail() { return email; }
  public String getFullName() { return fullName; }
  public Role getRole() { return role; }
}
