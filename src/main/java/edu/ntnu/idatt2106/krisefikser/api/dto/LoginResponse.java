package edu.ntnu.idatt2106.krisefikser.api.dto;

import lombok.Getter;
import lombok.Setter;

public class LoginResponse {

  private String token;
  private String type = "Bearer";

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

}
