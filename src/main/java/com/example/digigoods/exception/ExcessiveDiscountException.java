package com.example.digigoods.exception;

/**
 * Exception thrown when the total discount exceeds the maximum allowed percentage.
 */
public class ExcessiveDiscountException extends RuntimeException {

  public ExcessiveDiscountException(String message) {
    super(message);
  }

  public ExcessiveDiscountException() {
    super("Total discount exceeds the maximum allowed 75% of the original subtotal");
  }
}
