package edu.ntnu.idatt2106.krisefikser.api.dto.auth;

/**
 * Data Transfer Object (DTO) for login requests. This class is used to encapsulate the data sent
 * from the client to the server when a user attempts to log in.
 *
 * @author Snake727
 */
public class LoginRequest {

  private String email;
  private String password;

  public LoginRequest() {
  }

  public LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}