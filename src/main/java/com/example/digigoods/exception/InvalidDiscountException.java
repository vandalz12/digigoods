package com.example.digigoods.exception;

/**
 * Exception thrown when a discount is invalid.
 */
public class InvalidDiscountException extends RuntimeException {

  public InvalidDiscountException(String message) {
    super(message);
  }

  public InvalidDiscountException(String discountCode, String reason) {
    super("Invalid discount code '" + discountCode + "': " + reason);
  }
}
