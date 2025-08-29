package com.example.digigoods.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for all exception classes to ensure 100% code coverage.
 */
class ExceptionClassesTest {

  @Nested
  @DisplayName("ExcessiveDiscountException Tests")
  class ExcessiveDiscountExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom excessive discount message";

      // Act
      ExcessiveDiscountException exception = new ExcessiveDiscountException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given no parameters, when creating exception, "
        + "then default message should be used")
    void givenNoParameters_whenCreatingException_thenDefaultMessageShouldBeUsed() {
      // Arrange & Act
      ExcessiveDiscountException exception = new ExcessiveDiscountException();

      // Assert
      String expectedMessage = "Total discount exceeds the maximum allowed 75% "
          + "of the original subtotal";
      assertEquals(expectedMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("InsufficientStockException Tests")
  class InsufficientStockExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom insufficient stock message";

      // Act
      InsufficientStockException exception = new InsufficientStockException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given product details, when creating exception, "
        + "then formatted message should be created")
    void givenProductDetails_whenCreatingException_thenFormattedMessageShouldBeCreated() {
      // Arrange
      Long productId = 123L;
      int requested = 10;
      int available = 5;

      // Act
      InsufficientStockException exception = new InsufficientStockException(
          productId, requested, available);

      // Assert
      String expectedMessage = "Insufficient stock for product 123. Requested: 10, Available: 5";
      assertEquals(expectedMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("InvalidDiscountException Tests")
  class InvalidDiscountExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom invalid discount message";

      // Act
      InvalidDiscountException exception = new InvalidDiscountException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given discount code and reason, when creating exception, "
        + "then formatted message should be created")
    void givenDiscountCodeAndReason_whenCreatingException_thenFormattedMessageShouldBeCreated() {
      // Arrange
      String discountCode = "INVALID20";
      String reason = "discount has expired";

      // Act
      InvalidDiscountException exception = new InvalidDiscountException(discountCode, reason);

      // Assert
      String expectedMessage = "Invalid discount code 'INVALID20': discount has expired";
      assertEquals(expectedMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("MissingJwtTokenException Tests")
  class MissingJwtTokenExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom JWT token missing message";

      // Act
      MissingJwtTokenException exception = new MissingJwtTokenException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given no parameters, when creating exception, "
        + "then default message should be used")
    void givenNoParameters_whenCreatingException_thenDefaultMessageShouldBeUsed() {
      // Arrange & Act
      MissingJwtTokenException exception = new MissingJwtTokenException();

      // Assert
      assertEquals("JWT token is missing or invalid", exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("ProductNotFoundException Tests")
  class ProductNotFoundExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom product not found message";

      // Act
      ProductNotFoundException exception = new ProductNotFoundException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given product ID, when creating exception, "
        + "then formatted message should be created")
    void givenProductId_whenCreatingException_thenFormattedMessageShouldBeCreated() {
      // Arrange
      Long productId = 456L;

      // Act
      ProductNotFoundException exception = new ProductNotFoundException(productId);

      // Assert
      String expectedMessage = "Product not found with ID: 456";
      assertEquals(expectedMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("UnauthorizedAccessException Tests")
  class UnauthorizedAccessExceptionTest {

    @Test
    @DisplayName("Given custom message, when creating exception, "
        + "then message should be set correctly")
    void givenCustomMessage_whenCreatingException_thenMessageShouldBeSetCorrectly() {
      // Arrange
      String customMessage = "Custom unauthorized access message";

      // Act
      UnauthorizedAccessException exception = new UnauthorizedAccessException(customMessage);

      // Assert
      assertEquals(customMessage, exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Given no parameters, when creating exception, "
        + "then default message should be used")
    void givenNoParameters_whenCreatingException_thenDefaultMessageShouldBeUsed() {
      // Arrange & Act
      UnauthorizedAccessException exception = new UnauthorizedAccessException();

      // Assert
      assertEquals("User is not authorized to perform this action", exception.getMessage());
      assertTrue(exception instanceof RuntimeException);
    }
  }

  @Nested
  @DisplayName("Exception Inheritance Tests")
  class ExceptionInheritanceTest {

    @Test
    @DisplayName("All custom exceptions should extend RuntimeException")
    void allCustomExceptionsShouldExtendRuntimeException() {
      // Arrange & Act & Assert
      assertTrue(new ExcessiveDiscountException() instanceof RuntimeException);
      assertTrue(new InsufficientStockException("test") instanceof RuntimeException);
      assertTrue(new InvalidDiscountException("test") instanceof RuntimeException);
      assertTrue(new MissingJwtTokenException() instanceof RuntimeException);
      assertTrue(new ProductNotFoundException("test") instanceof RuntimeException);
      assertTrue(new UnauthorizedAccessException() instanceof RuntimeException);
    }

    @Test
    @DisplayName("All custom exceptions should have non-null messages")
    void allCustomExceptionsShouldHaveNonNullMessages() {
      // Arrange & Act & Assert
      assertNotNull(new ExcessiveDiscountException().getMessage());
      assertNotNull(new InsufficientStockException("test").getMessage());
      assertNotNull(new InvalidDiscountException("test").getMessage());
      assertNotNull(new MissingJwtTokenException().getMessage());
      assertNotNull(new ProductNotFoundException("test").getMessage());
      assertNotNull(new UnauthorizedAccessException().getMessage());
    }
  }
}
