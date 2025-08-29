package com.example.digigoods.exception;

/**
 * Exception thrown when JWT token is missing from the request.
 */
public class MissingJwtTokenException extends RuntimeException {

  public MissingJwtTokenException(String message) {
    super(message);
  }

  public MissingJwtTokenException() {
    super("JWT token is missing or invalid");
  }
}
