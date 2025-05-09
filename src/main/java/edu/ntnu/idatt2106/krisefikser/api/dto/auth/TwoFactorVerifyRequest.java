package edu.ntnu.idatt2106.krisefikser.api.dto.auth;

/**
 * Data Transfer Object for two-factor authentication verification requests. Contains the email and
 * OTP code for verification.
 */
public class TwoFactorVerifyRequest {

  private String email;
  private String otp;

  /**
   * Instantiates a new TwoFactorVerifyRequest.
   */

  public TwoFactorVerifyRequest() {
  }

  /**
   * Instantiates a new TwoFactorVerifyRequest with the specified email and OTP.
   *
   * @param email The email address of the user.
   * @param otp   The one-time password (OTP) for verification.
   */
  public TwoFactorVerifyRequest(String email, String otp) {
    this.email = email;
    this.otp = otp;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getOtp() {
    return otp;
  }

  public void setOtp(String otp) {
    this.otp = otp;
  }
}