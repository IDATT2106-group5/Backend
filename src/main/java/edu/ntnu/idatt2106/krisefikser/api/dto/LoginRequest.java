package edu.ntnu.idatt2106.krisefikser.api.dto;

import lombok.Getter;
import lombok.Setter;

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

  public String getPassword() {
    return password;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}