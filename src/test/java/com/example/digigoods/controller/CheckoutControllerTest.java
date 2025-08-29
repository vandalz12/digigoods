package com.example.digigoods.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.CheckoutRequest;
import com.example.digigoods.dto.OrderResponse;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.service.CheckoutService;
import com.example.digigoods.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutController Tests")
class CheckoutControllerTest {

  @Mock
  private CheckoutService checkoutService;

  @Mock
  private JwtService jwtService;

  @Mock
  private HttpServletRequest httpServletRequest;

  @InjectMocks
  private CheckoutController checkoutController;

  private CheckoutRequest checkoutRequest;
  private OrderResponse orderResponse;

  @BeforeEach
  void setUp() {
    checkoutRequest = new CheckoutRequest();
    checkoutRequest.setUserId(1L);
    checkoutRequest.setProductIds(List.of(1L, 2L));
    checkoutRequest.setDiscountCodes(List.of("DISCOUNT20"));

    orderResponse = new OrderResponse();
    orderResponse.setMessage("Order created successfully!");
    orderResponse.setFinalPrice(new BigDecimal("120.00"));
  }

  @Test
  @DisplayName("Given valid request with JWT token, when createOrder, then return order response")
  void givenValidRequestWithJwtToken_whenCreateOrder_thenReturnOrderResponse() {
    // Arrange
    String authHeader = "Bearer valid-jwt-token";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("valid-jwt-token")).thenReturn(userId);
    when(checkoutService.processCheckout(checkoutRequest, userId)).thenReturn(orderResponse);

    // Act
    ResponseEntity<OrderResponse> response = checkoutController.createOrder(checkoutRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Order created successfully!", response.getBody().getMessage());
    assertEquals(new BigDecimal("120.00"), response.getBody().getFinalPrice());

    verify(jwtService).extractUserId("valid-jwt-token");
    verify(checkoutService).processCheckout(checkoutRequest, userId);
  }

  @Test
  @DisplayName("Given request without Authorization header, when createOrder, then throw MissingJwtTokenException")
  void givenRequestWithoutAuthorizationHeader_whenCreateOrder_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> checkoutController.createOrder(checkoutRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with empty Authorization header, when createOrder, then throw MissingJwtTokenException")
  void givenRequestWithEmptyAuthorizationHeader_whenCreateOrder_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> checkoutController.createOrder(checkoutRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Authorization header not starting with Bearer, when createOrder, then throw MissingJwtTokenException")
  void givenRequestWithAuthorizationHeaderNotStartingWithBearer_whenCreateOrder_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic some-token");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> checkoutController.createOrder(checkoutRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Bearer but no token, when createOrder, then call jwtService with empty string")
  void givenRequestWithBearerButNoToken_whenCreateOrder_thenCallJwtServiceWithEmptyString() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");
    when(jwtService.extractUserId("")).thenThrow(new RuntimeException("Invalid token"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> checkoutController.createOrder(checkoutRequest, httpServletRequest));

    assertEquals("Invalid token", exception.getMessage());
    verify(jwtService).extractUserId("");
  }

  @Test
  @DisplayName("Given request with Bearer and token, when createOrder, then extract token correctly")
  void givenRequestWithBearerAndToken_whenCreateOrder_thenExtractTokenCorrectly() {
    // Arrange
    String authHeader = "Bearer jwt-token-value";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("jwt-token-value")).thenReturn(userId);
    when(checkoutService.processCheckout(checkoutRequest, userId)).thenReturn(orderResponse);

    // Act
    ResponseEntity<OrderResponse> response = checkoutController.createOrder(checkoutRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(jwtService).extractUserId("jwt-token-value");
    verify(checkoutService).processCheckout(checkoutRequest, userId);
  }

  @Test
  @DisplayName("Given valid request with different user ID, when createOrder, then pass correct user ID to service")
  void givenValidRequestWithDifferentUserId_whenCreateOrder_thenPassCorrectUserIdToService() {
    // Arrange
    String authHeader = "Bearer valid-jwt-token";
    Long jwtUserId = 2L;
    checkoutRequest.setUserId(1L); // Different from JWT user ID

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("valid-jwt-token")).thenReturn(jwtUserId);
    when(checkoutService.processCheckout(checkoutRequest, jwtUserId)).thenReturn(orderResponse);

    // Act
    ResponseEntity<OrderResponse> response = checkoutController.createOrder(checkoutRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(checkoutService).processCheckout(checkoutRequest, jwtUserId);
  }

  @Test
  @DisplayName("Given null checkout request, when createOrder, then handle gracefully")
  void givenNullCheckoutRequest_whenCreateOrder_thenHandleGracefully() {
    // Arrange
    String authHeader = "Bearer valid-jwt-token";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("valid-jwt-token")).thenReturn(userId);
    when(checkoutService.processCheckout(null, userId)).thenReturn(orderResponse);

    // Act
    ResponseEntity<OrderResponse> response = checkoutController.createOrder(null, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(checkoutService).processCheckout(null, userId);
  }

  @Test
  @DisplayName("Given request with extra spaces in Bearer token, when createOrder, then extract token correctly")
  void givenRequestWithExtraSpacesInBearerToken_whenCreateOrder_thenExtractTokenCorrectly() {
    // Arrange
    String authHeader = "Bearer   jwt-token-with-spaces   ";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("  jwt-token-with-spaces   ")).thenReturn(userId);
    when(checkoutService.processCheckout(checkoutRequest, userId)).thenReturn(orderResponse);

    // Act
    ResponseEntity<OrderResponse> response = checkoutController.createOrder(checkoutRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(jwtService).extractUserId("  jwt-token-with-spaces   ");
    verify(checkoutService).processCheckout(checkoutRequest, userId);
  }
}
