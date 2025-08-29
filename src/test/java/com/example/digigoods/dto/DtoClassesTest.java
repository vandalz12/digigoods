package com.example.digigoods.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DTO Classes Tests")
class DtoClassesTest {

  @Nested
  @DisplayName("CheckoutRequest DTO Tests")
  class CheckoutRequestTest {

    @Test
    @DisplayName("Given CheckoutRequest constructor with parameters, when creating request, then set all fields correctly")
    void givenCheckoutRequestConstructorWithParameters_whenCreatingRequest_thenSetAllFieldsCorrectly() {
      // Arrange
      List<Long> productIds = List.of(1L, 2L, 3L);
      List<String> discountCodes = List.of("DISCOUNT20", "SUMMER15");

      // Act
      CheckoutRequest request = new CheckoutRequest(1L, productIds, discountCodes);

      // Assert
      assertEquals(1L, request.getUserId());
      assertEquals(productIds, request.getProductIds());
      assertEquals(discountCodes, request.getDiscountCodes());
    }

    @Test
    @DisplayName("Given CheckoutRequest no-args constructor, when creating request, then all fields are null")
    void givenCheckoutRequestNoArgsConstructor_whenCreatingRequest_thenAllFieldsAreNull() {
      // Act
      CheckoutRequest request = new CheckoutRequest();

      // Assert
      assertNull(request.getUserId());
      assertNull(request.getProductIds());
      assertNull(request.getDiscountCodes());
    }

    @Test
    @DisplayName("Given CheckoutRequest setters, when setting values, then getters return correct values")
    void givenCheckoutRequestSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      CheckoutRequest request = new CheckoutRequest();
      List<Long> productIds = List.of(4L, 5L);
      List<String> discountCodes = List.of("WINTER10");

      // Act
      request.setUserId(2L);
      request.setProductIds(productIds);
      request.setDiscountCodes(discountCodes);

      // Assert
      assertEquals(2L, request.getUserId());
      assertEquals(productIds, request.getProductIds());
      assertEquals(discountCodes, request.getDiscountCodes());
    }

    @Test
    @DisplayName("Given two CheckoutRequests with same data, when comparing, then they are equal")
    void givenTwoCheckoutRequestsWithSameData_whenComparing_thenTheyAreEqual() {
      // Arrange
      List<Long> productIds = List.of(1L, 2L);
      List<String> discountCodes = List.of("TEST20");
      CheckoutRequest request1 = new CheckoutRequest(1L, productIds, discountCodes);
      CheckoutRequest request2 = new CheckoutRequest(1L, productIds, discountCodes);

      // Assert
      assertEquals(request1, request2);
      assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Given CheckoutRequest toString, when called, then return string representation")
    void givenCheckoutRequestToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      CheckoutRequest request = new CheckoutRequest(1L, List.of(1L, 2L), List.of("TEST20"));

      // Act
      String result = request.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("1"));
      assertTrue(result.contains("TEST20"));
    }
  }

  @Nested
  @DisplayName("LoginRequest DTO Tests")
  class LoginRequestTest {

    @Test
    @DisplayName("Given LoginRequest constructor with parameters, when creating request, then set all fields correctly")
    void givenLoginRequestConstructorWithParameters_whenCreatingRequest_thenSetAllFieldsCorrectly() {
      // Act
      LoginRequest request = new LoginRequest("testuser", "password");

      // Assert
      assertEquals("testuser", request.getUsername());
      assertEquals("password", request.getPassword());
    }

    @Test
    @DisplayName("Given LoginRequest no-args constructor, when creating request, then all fields are null")
    void givenLoginRequestNoArgsConstructor_whenCreatingRequest_thenAllFieldsAreNull() {
      // Act
      LoginRequest request = new LoginRequest();

      // Assert
      assertNull(request.getUsername());
      assertNull(request.getPassword());
    }

    @Test
    @DisplayName("Given LoginRequest setters, when setting values, then getters return correct values")
    void givenLoginRequestSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      LoginRequest request = new LoginRequest();

      // Act
      request.setUsername("newuser");
      request.setPassword("newpassword");

      // Assert
      assertEquals("newuser", request.getUsername());
      assertEquals("newpassword", request.getPassword());
    }

    @Test
    @DisplayName("Given two LoginRequests with same data, when comparing, then they are equal")
    void givenTwoLoginRequestsWithSameData_whenComparing_thenTheyAreEqual() {
      // Arrange
      LoginRequest request1 = new LoginRequest("testuser", "password");
      LoginRequest request2 = new LoginRequest("testuser", "password");

      // Assert
      assertEquals(request1, request2);
      assertEquals(request1.hashCode(), request2.hashCode());
    }

    @Test
    @DisplayName("Given LoginRequest toString, when called, then return string representation")
    void givenLoginRequestToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      LoginRequest request = new LoginRequest("testuser", "password");

      // Act
      String result = request.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("testuser"));
      assertTrue(result.contains("password"));
    }
  }

  @Nested
  @DisplayName("LoginResponse DTO Tests")
  class LoginResponseTest {

    @Test
    @DisplayName("Given LoginResponse constructor with all parameters, when creating response, then set all fields correctly")
    void givenLoginResponseConstructorWithAllParameters_whenCreatingResponse_thenSetAllFieldsCorrectly() {
      // Act
      LoginResponse response = new LoginResponse("jwt-token", "Bearer", 1L, "testuser");

      // Assert
      assertEquals("jwt-token", response.getToken());
      assertEquals("Bearer", response.getType());
      assertEquals(1L, response.getUserId());
      assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("Given LoginResponse constructor with token, userId and username, when creating response, then set type to Bearer")
    void givenLoginResponseConstructorWithTokenUserIdAndUsername_whenCreatingResponse_thenSetTypeToBearer() {
      // Act
      LoginResponse response = new LoginResponse("jwt-token", 1L, "testuser");

      // Assert
      assertEquals("jwt-token", response.getToken());
      assertEquals("Bearer", response.getType());
      assertEquals(1L, response.getUserId());
      assertEquals("testuser", response.getUsername());
    }

    @Test
    @DisplayName("Given LoginResponse no-args constructor, when creating response, then type defaults to Bearer")
    void givenLoginResponseNoArgsConstructor_whenCreatingResponse_thenTypeDefaultsToBearer() {
      // Act
      LoginResponse response = new LoginResponse();

      // Assert
      assertNull(response.getToken());
      assertEquals("Bearer", response.getType());
      assertNull(response.getUserId());
      assertNull(response.getUsername());
    }

    @Test
    @DisplayName("Given LoginResponse setters, when setting values, then getters return correct values")
    void givenLoginResponseSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      LoginResponse response = new LoginResponse();

      // Act
      response.setToken("new-jwt-token");
      response.setType("Custom");
      response.setUserId(2L);
      response.setUsername("newuser");

      // Assert
      assertEquals("new-jwt-token", response.getToken());
      assertEquals("Custom", response.getType());
      assertEquals(2L, response.getUserId());
      assertEquals("newuser", response.getUsername());
    }

    @Test
    @DisplayName("Given LoginResponse toString, when called, then return string representation")
    void givenLoginResponseToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      LoginResponse response = new LoginResponse("jwt-token", 1L, "testuser");

      // Act
      String result = response.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("jwt-token"));
      assertTrue(result.contains("Bearer"));
      assertTrue(result.contains("testuser"));
    }
  }

  @Nested
  @DisplayName("OrderResponse DTO Tests")
  class OrderResponseTest {

    @Test
    @DisplayName("Given OrderResponse constructor with parameters, when creating response, then set all fields correctly")
    void givenOrderResponseConstructorWithParameters_whenCreatingResponse_thenSetAllFieldsCorrectly() {
      // Act
      OrderResponse response = new OrderResponse("Order created successfully!", new BigDecimal("120.00"));

      // Assert
      assertEquals("Order created successfully!", response.getMessage());
      assertEquals(new BigDecimal("120.00"), response.getFinalPrice());
    }

    @Test
    @DisplayName("Given OrderResponse no-args constructor, when creating response, then all fields are null")
    void givenOrderResponseNoArgsConstructor_whenCreatingResponse_thenAllFieldsAreNull() {
      // Act
      OrderResponse response = new OrderResponse();

      // Assert
      assertNull(response.getMessage());
      assertNull(response.getFinalPrice());
    }

    @Test
    @DisplayName("Given OrderResponse setters, when setting values, then getters return correct values")
    void givenOrderResponseSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      OrderResponse response = new OrderResponse();

      // Act
      response.setMessage("Order processed!");
      response.setFinalPrice(new BigDecimal("99.99"));

      // Assert
      assertEquals("Order processed!", response.getMessage());
      assertEquals(new BigDecimal("99.99"), response.getFinalPrice());
    }

    @Test
    @DisplayName("Given two OrderResponses with same data, when comparing, then they are equal")
    void givenTwoOrderResponsesWithSameData_whenComparing_thenTheyAreEqual() {
      // Arrange
      OrderResponse response1 = new OrderResponse("Success", new BigDecimal("100.00"));
      OrderResponse response2 = new OrderResponse("Success", new BigDecimal("100.00"));

      // Assert
      assertEquals(response1, response2);
      assertEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    @DisplayName("Given OrderResponse toString, when called, then return string representation")
    void givenOrderResponseToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      OrderResponse response = new OrderResponse("Order created!", new BigDecimal("150.00"));

      // Act
      String result = response.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("Order created!"));
      assertTrue(result.contains("150.00"));
    }
  }

  @Nested
  @DisplayName("ErrorResponse DTO Tests")
  class ErrorResponseTest {

    @Test
    @DisplayName("Given ErrorResponse constructor with all parameters, when creating response, then set all fields correctly")
    void givenErrorResponseConstructorWithAllParameters_whenCreatingResponse_thenSetAllFieldsCorrectly() {
      // Arrange
      LocalDateTime timestamp = LocalDateTime.now();

      // Act
      ErrorResponse response = new ErrorResponse(timestamp, 400, "Bad Request", "Invalid input", "/api/test");

      // Assert
      assertEquals(timestamp, response.getTimestamp());
      assertEquals(400, response.getStatus());
      assertEquals("Bad Request", response.getError());
      assertEquals("Invalid input", response.getMessage());
      assertEquals("/api/test", response.getPath());
    }

    @Test
    @DisplayName("Given ErrorResponse constructor with status, error, message and path, when creating response, then set timestamp automatically")
    void givenErrorResponseConstructorWithStatusErrorMessageAndPath_whenCreatingResponse_thenSetTimestampAutomatically() {
      // Arrange
      LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

      // Act
      ErrorResponse response = new ErrorResponse(404, "Not Found", "Resource not found", "/api/resource");
      LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

      // Assert
      assertNotNull(response.getTimestamp());
      assertTrue(response.getTimestamp().isAfter(beforeCreate));
      assertTrue(response.getTimestamp().isBefore(afterCreate));
      assertEquals(404, response.getStatus());
      assertEquals("Not Found", response.getError());
      assertEquals("Resource not found", response.getMessage());
      assertEquals("/api/resource", response.getPath());
    }

    @Test
    @DisplayName("Given ErrorResponse no-args constructor, when creating response, then all fields are null or zero")
    void givenErrorResponseNoArgsConstructor_whenCreatingResponse_thenAllFieldsAreNullOrZero() {
      // Act
      ErrorResponse response = new ErrorResponse();

      // Assert
      assertNull(response.getTimestamp());
      assertEquals(0, response.getStatus());
      assertNull(response.getError());
      assertNull(response.getMessage());
      assertNull(response.getPath());
    }

    @Test
    @DisplayName("Given ErrorResponse setters, when setting values, then getters return correct values")
    void givenErrorResponseSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      ErrorResponse response = new ErrorResponse();
      LocalDateTime timestamp = LocalDateTime.now();

      // Act
      response.setTimestamp(timestamp);
      response.setStatus(500);
      response.setError("Internal Server Error");
      response.setMessage("Something went wrong");
      response.setPath("/api/error");

      // Assert
      assertEquals(timestamp, response.getTimestamp());
      assertEquals(500, response.getStatus());
      assertEquals("Internal Server Error", response.getError());
      assertEquals("Something went wrong", response.getMessage());
      assertEquals("/api/error", response.getPath());
    }

    @Test
    @DisplayName("Given ErrorResponse toString, when called, then return string representation")
    void givenErrorResponseToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      ErrorResponse response = new ErrorResponse(400, "Bad Request", "Invalid data", "/api/test");

      // Act
      String result = response.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("400"));
      assertTrue(result.contains("Bad Request"));
      assertTrue(result.contains("Invalid data"));
      assertTrue(result.contains("/api/test"));
    }
  }
}
