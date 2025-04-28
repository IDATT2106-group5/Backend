package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * The type Register request dto.
 */
public class RegisterRequestDto {
  private String fullName;
  private String email;
  private String password;
  private String tlf;

  /**
   * Gets a hCaptchaToken
   * @return hCaptchaToken
   */
  public String getHcaptchaToken() {
    return hCaptchaToken;
  }

  /**
   * Sets a hCaptchaToken
   * @param hCaptchaToken the hCaptchaToken
   */
  public void setHcaptchaToken(String hCaptchaToken) {
    this.hCaptchaToken = hCaptchaToken;
  }

  private String hCaptchaToken;

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
