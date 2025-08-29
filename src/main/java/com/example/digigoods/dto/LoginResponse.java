package com.example.digigoods.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for login response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

  private String token;
  private String type = "Bearer";
  private Long userId;
  private String username;

  public LoginResponse(String token, Long userId, String username) {
    this.token = token;
    this.userId = userId;
    this.username = username;
  }
}
