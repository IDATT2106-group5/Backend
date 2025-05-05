package edu.ntnu.idatt2106.krisefikser.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for password reset requests.
 */
public class PasswordResetRequestDto {

  @NotBlank
  private String token;

  @NotBlank
  private String newPassword;

  /**
   * Constructor for PasswordResetRequestDto.
   *
   * @param token       the password reset token
   * @param newPassword the new password
   */
  public PasswordResetRequestDto(String token, String newPassword) {
    this.token = token;
    this.newPassword = newPassword;
  }

  // Getters and setters
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(String newPassword) {
    this.newPassword = newPassword;
  }
}
