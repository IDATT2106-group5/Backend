package edu.ntnu.idatt2106.krisefikser.api.dto;

/**
 * Data Transfer Object for login requests. This class is used to encapsulate the data sent from the
 * client to the server when a user attempts to log in.
 *
 * @author Snake727
 */
public class LoginResponse {

  private String token;
  private String type = "Bearer";
  private boolean requires2FA;

  public LoginResponse(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isRequires2FA() {
    return requires2FA;
  }

  public void setRequires2FA(boolean requires2FA) {
    this.requires2FA = requires2FA;
  }

}
