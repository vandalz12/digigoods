package com.example.digigoods.exception;

/**
 * Exception thrown when there is insufficient stock for a product.
 */
public class InsufficientStockException extends RuntimeException {

  public InsufficientStockException(String message) {
    super(message);
  }

  public InsufficientStockException(Long productId, int requested, int available) {
    super("Insufficient stock for product " + productId + ". Requested: " + requested 
        + ", Available: " + available);
  }
}
