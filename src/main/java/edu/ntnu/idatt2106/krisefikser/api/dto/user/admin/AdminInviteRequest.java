package edu.ntnu.idatt2106.krisefikser.api.dto.user.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for admin invitation requests. Contains the required information to invite a
 * new admin.
 */
public class AdminInviteRequest {

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Full name is required")
  private String fullName;

  /**
   * Default constructor for AdminInviteRequest.
   */
  
  public AdminInviteRequest() {
  }

  /**
   * Constructor for AdminInviteRequest.
   *
   * @param email    The email address of the new admin.
   * @param fullName The full name of the new admin.
   */
  public AdminInviteRequest(String email, String fullName) {
    this.email = email;
    this.fullName = fullName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }
}