package com.example.digigoods.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.AddToCartRequest;
import com.example.digigoods.dto.AddToCartResponse;
import com.example.digigoods.dto.GetCartResponse;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.service.CartService;
import com.example.digigoods.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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

/**
 * Unit tests for CartController to ensure 100% code coverage.
 */
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

  @Mock
  private CartService cartService;

  @Mock
  private JwtService jwtService;

  @Mock
  private HttpServletRequest httpServletRequest;

  @InjectMocks
  private CartController cartController;

  private AddToCartRequest addToCartRequest;
  private AddToCartResponse addToCartResponse;
  private GetCartResponse getCartResponse;

  @BeforeEach
  void setUp() {
    addToCartRequest = new AddToCartRequest(1L, 2);
    addToCartResponse = new AddToCartResponse("Product added to cart successfully!", "Test Product");
    getCartResponse = GetCartResponse.builder()
        .productId(1L)
        .productName("Test Product")
        .quantity(2)
        .build();
  }

  @Test
  @DisplayName("Given valid request with Bearer token, when addToCart, then return success response")
  void givenValidRequestWithBearerToken_whenAddToCart_thenReturnSuccessResponse() {
    // Arrange
    String authHeader = "Bearer valid-jwt-token";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("valid-jwt-token")).thenReturn(userId);
    when(cartService.addToCart(addToCartRequest, userId)).thenReturn(addToCartResponse);

    // Act
    ResponseEntity<AddToCartResponse> response = cartController.addToCart(addToCartRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(addToCartResponse, response.getBody());
    verify(jwtService).extractUserId("valid-jwt-token");
    verify(cartService).addToCart(addToCartRequest, userId);
  }

  @Test
  @DisplayName("Given request without Authorization header, when addToCart, then throw MissingJwtTokenException")
  void givenRequestWithoutAuthorizationHeader_whenAddToCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.addToCart(addToCartRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with empty Authorization header, when addToCart, then throw MissingJwtTokenException")
  void givenRequestWithEmptyAuthorizationHeader_whenAddToCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.addToCart(addToCartRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Authorization header not starting with Bearer, when addToCart, then throw MissingJwtTokenException")
  void givenRequestWithAuthorizationHeaderNotStartingWithBearer_whenAddToCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic some-token");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.addToCart(addToCartRequest, httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Bearer but no token, when addToCart, then call jwtService with empty string")
  void givenRequestWithBearerButNoToken_whenAddToCart_thenCallJwtServiceWithEmptyString() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");
    when(jwtService.extractUserId("")).thenThrow(new RuntimeException("Invalid token"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> cartController.addToCart(addToCartRequest, httpServletRequest));

    assertEquals("Invalid token", exception.getMessage());
    verify(jwtService).extractUserId("");
  }

  @Test
  @DisplayName("Given valid request with Bearer token, when getCart, then return cart items")
  void givenValidRequestWithBearerToken_whenGetCart_thenReturnCartItems() {
    // Arrange
    String authHeader = "Bearer valid-jwt-token";
    Long userId = 1L;
    List<GetCartResponse> cartItems = List.of(getCartResponse);

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("valid-jwt-token")).thenReturn(userId);
    when(cartService.getCart(userId)).thenReturn(cartItems);

    // Act
    ResponseEntity<List<GetCartResponse>> response = cartController.getCart(httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(cartItems, response.getBody());
    verify(jwtService).extractUserId("valid-jwt-token");
    verify(cartService).getCart(userId);
  }

  @Test
  @DisplayName("Given request without Authorization header, when getCart, then throw MissingJwtTokenException")
  void givenRequestWithoutAuthorizationHeader_whenGetCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn(null);

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.getCart(httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with empty Authorization header, when getCart, then throw MissingJwtTokenException")
  void givenRequestWithEmptyAuthorizationHeader_whenGetCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.getCart(httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Authorization header not starting with Bearer, when getCart, then throw MissingJwtTokenException")
  void givenRequestWithAuthorizationHeaderNotStartingWithBearer_whenGetCart_thenThrowMissingJwtTokenException() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Basic some-token");

    // Act & Assert
    MissingJwtTokenException exception = assertThrows(MissingJwtTokenException.class,
        () -> cartController.getCart(httpServletRequest));

    assertEquals("JWT token is missing or invalid", exception.getMessage());
  }

  @Test
  @DisplayName("Given request with Bearer but no token, when getCart, then call jwtService with empty string")
  void givenRequestWithBearerButNoToken_whenGetCart_thenCallJwtServiceWithEmptyString() {
    // Arrange
    when(httpServletRequest.getHeader("Authorization")).thenReturn("Bearer ");
    when(jwtService.extractUserId("")).thenThrow(new RuntimeException("Invalid token"));

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> cartController.getCart(httpServletRequest));

    assertEquals("Invalid token", exception.getMessage());
    verify(jwtService).extractUserId("");
  }

  @Test
  @DisplayName("Given request with Bearer and token, when addToCart, then extract token correctly")
  void givenRequestWithBearerAndToken_whenAddToCart_thenExtractTokenCorrectly() {
    // Arrange
    String authHeader = "Bearer jwt-token-value";
    Long userId = 1L;

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("jwt-token-value")).thenReturn(userId);
    when(cartService.addToCart(addToCartRequest, userId)).thenReturn(addToCartResponse);

    // Act
    ResponseEntity<AddToCartResponse> response = cartController.addToCart(addToCartRequest, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(addToCartResponse, response.getBody());
    verify(jwtService).extractUserId("jwt-token-value");
    verify(cartService).addToCart(addToCartRequest, userId);
  }

  @Test
  @DisplayName("Given request with Bearer and token, when getCart, then extract token correctly")
  void givenRequestWithBearerAndToken_whenGetCart_thenExtractTokenCorrectly() {
    // Arrange
    String authHeader = "Bearer jwt-token-value";
    Long userId = 1L;
    List<GetCartResponse> cartItems = List.of(getCartResponse);

    when(httpServletRequest.getHeader("Authorization")).thenReturn(authHeader);
    when(jwtService.extractUserId("jwt-token-value")).thenReturn(userId);
    when(cartService.getCart(userId)).thenReturn(cartItems);

    // Act
    ResponseEntity<List<GetCartResponse>> response = cartController.getCart(httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(cartItems, response.getBody());
    verify(jwtService).extractUserId("jwt-token-value");
    verify(cartService).getCart(userId);
  }
}
