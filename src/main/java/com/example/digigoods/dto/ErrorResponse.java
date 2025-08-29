package com.example.digigoods.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for error response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  @JsonSerialize(using = LocalDateTimeSerializer.class)
  private LocalDateTime timestamp;

  private int status;
  private String error;
  private String message;
  private String path;

  /**
   * Constructor with status, error, message and path.
   *
   * @param status HTTP status code
   * @param error error type
   * @param message error message
   * @param path request path
   */
  public ErrorResponse(int status, String error, String message, String path) {
    this.timestamp = LocalDateTime.now();
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }
}

