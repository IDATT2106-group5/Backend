package edu.ntnu.idatt2106.krisefikser.api.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Register request dto.
 */
public class RegisterRequestDto {

  private String fullName;
  private String email;
  private String password;
  private String tlf;
  @JsonProperty("hCaptchaToken")
  private String hCaptchaToken;

  /**
   * Gets a hCaptchaToken
   *
   * @return hCaptchaToken
   */
  public String getHCaptchaToken() {
    return hCaptchaToken;
  }

  /**
   * Sets a hCaptchaToken
   *
   * @param hCaptchaToken the hCaptchaToken
   */
  public void setHCaptchaToken(String hCaptchaToken) {
    this.hCaptchaToken = hCaptchaToken;
  }

  /**
   * Default constructor for RegisterRequestDto.
   *
   * @param fullName The full name of the user.
   * @param email    The email address of the user.
   * @param password The password for the user.
   * @param tlf      The telephone number of the user.
   */
  public RegisterRequestDto(String fullName, String email, String password, String tlf) {
    this.fullName = fullName;
    this.email = email;
    this.password = password;
    this.tlf = tlf;

  }

  /**
   * Gets tlf.
   *
   * @return the tlf
   */
  public String getTlf() {
    return tlf;
  }

  /**
   * Sets tlf.
   *
   * @param tlf the tlf
   */
  public void setTlf(String tlf) {
    this.tlf = tlf;
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
   * Gets email.
   *
   * @return the email
   */
  public String getEmail() {
    return email;
  }

  /**
   * Sets email.
   *
   * @param email the email
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Gets password.
   *
   * @return the password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Sets password.
   *
   * @param password the password
   */
  public void setPassword(String password) {
    this.password = password;
  }

}
