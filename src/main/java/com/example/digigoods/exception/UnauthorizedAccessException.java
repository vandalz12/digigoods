package com.example.digigoods.exception;

/**
 * Exception thrown when a user tries to access resources they don't own.
 */
public class UnauthorizedAccessException extends RuntimeException {

  public UnauthorizedAccessException(String message) {
    super(message);
  }

  public UnauthorizedAccessException() {
    super("User is not authorized to perform this action");
  }
}
