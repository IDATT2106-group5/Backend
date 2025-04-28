package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * Data Transfer Object for admin invitation requests. Contains the required information to invite a
 * new admin.
 */
public class AdminInviteRequest {

  private String email;
  private String fullName;
  private String username; // Predefined admin username

  public AdminInviteRequest() {
  }

  public AdminInviteRequest(String email, String fullName, String username) {
    this.email = email;
    this.fullName = fullName;
    this.username = username;
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

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}