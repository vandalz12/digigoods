package com.example.digigoods.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.ErrorResponse;
import com.example.digigoods.exception.ExcessiveDiscountException;
import com.example.digigoods.exception.InsufficientStockException;
import com.example.digigoods.exception.InvalidDiscountException;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.exception.ProductNotFoundException;
import com.example.digigoods.exception.UnauthorizedAccessException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

  @Mock
  private HttpServletRequest httpServletRequest;

  @Mock
  private MethodArgumentNotValidException methodArgumentNotValidException;

  @Mock
  private BindingResult bindingResult;

  @InjectMocks
  private GlobalExceptionHandler globalExceptionHandler;

  @BeforeEach
  void setUp() {
    when(httpServletRequest.getRequestURI()).thenReturn("/test-endpoint");
  }

  @Test
  @DisplayName("Given ProductNotFoundException, when handleProductNotFoundException, then return 404 error response")
  void givenProductNotFoundException_whenHandleProductNotFoundException_thenReturn404ErrorResponse() {
    // Arrange
    ProductNotFoundException exception = new ProductNotFoundException("Product with ID 1 not found");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleProductNotFoundException(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(404, response.getBody().getStatus());
    assertEquals("Not Found", response.getBody().getError());
    assertEquals("Product with ID 1 not found", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given InvalidDiscountException, when handleBadRequestExceptions, then return 400 error response")
  void givenInvalidDiscountException_whenHandleBadRequestExceptions_thenReturn400ErrorResponse() {
    // Arrange
    InvalidDiscountException exception = new InvalidDiscountException("INVALID20", "discount code not found");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleBadRequestExceptions(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("Invalid discount code 'INVALID20': discount code not found", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given ExcessiveDiscountException, when handleBadRequestExceptions, then return 400 error response")
  void givenExcessiveDiscountException_whenHandleBadRequestExceptions_thenReturn400ErrorResponse() {
    // Arrange
    ExcessiveDiscountException exception = new ExcessiveDiscountException();

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleBadRequestExceptions(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("Total discount exceeds the maximum allowed 75% of the original subtotal", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given InsufficientStockException, when handleBadRequestExceptions, then return 400 error response")
  void givenInsufficientStockException_whenHandleBadRequestExceptions_thenReturn400ErrorResponse() {
    // Arrange
    InsufficientStockException exception = new InsufficientStockException("Product 'Test Product' has insufficient stock");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleBadRequestExceptions(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("Product 'Test Product' has insufficient stock", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given UnauthorizedAccessException, when handleUnauthorizedAccessException, then return 403 error response")
  void givenUnauthorizedAccessException_whenHandleUnauthorizedAccessException_thenReturn403ErrorResponse() {
    // Arrange
    UnauthorizedAccessException exception = new UnauthorizedAccessException("User cannot place order for another user");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleUnauthorizedAccessException(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(403, response.getBody().getStatus());
    assertEquals("Forbidden", response.getBody().getError());
    assertEquals("User cannot place order for another user", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given MissingJwtTokenException, when handleMissingJwtTokenException, then return 401 error response")
  void givenMissingJwtTokenException_whenHandleMissingJwtTokenException_thenReturn401ErrorResponse() {
    // Arrange
    MissingJwtTokenException exception = new MissingJwtTokenException();

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleMissingJwtTokenException(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().getStatus());
    assertEquals("Unauthorized", response.getBody().getError());
    assertEquals("JWT token is missing or invalid", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given BadCredentialsException, when handleBadCredentialsException, then return 401 error response")
  void givenBadCredentialsException_whenHandleBadCredentialsException_thenReturn401ErrorResponse() {
    // Arrange
    BadCredentialsException exception = new BadCredentialsException("Invalid username or password");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleBadCredentialsException(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().getStatus());
    assertEquals("Unauthorized", response.getBody().getError());
    assertEquals("Invalid username or password", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given MethodArgumentNotValidException, when handleValidationExceptions, then return 400 error response")
  void givenMethodArgumentNotValidException_whenHandleValidationExceptions_thenReturn400ErrorResponse() {
    // Arrange
    FieldError fieldError1 = new FieldError("checkoutRequest", "userId", "User ID is required");
    FieldError fieldError2 = new FieldError("checkoutRequest", "productIds", "Product IDs cannot be empty");

    when(methodArgumentNotValidException.getBindingResult()).thenReturn(bindingResult);
    when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleValidationExceptions(methodArgumentNotValidException, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().getStatus());
    assertEquals("Bad Request", response.getBody().getError());
    assertEquals("User ID is required, Product IDs cannot be empty", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }

  @Test
  @DisplayName("Given generic Exception, when handleGenericException, then return 500 error response")
  void givenGenericException_whenHandleGenericException_thenReturn500ErrorResponse() {
    // Arrange
    Exception exception = new Exception("Something went wrong");

    // Act
    ResponseEntity<ErrorResponse> response = globalExceptionHandler
        .handleGenericException(exception, httpServletRequest);

    // Assert
    assertNotNull(response);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().getStatus());
    assertEquals("Internal Server Error", response.getBody().getError());
    assertEquals("An unexpected error occurred", response.getBody().getMessage());
    assertEquals("/test-endpoint", response.getBody().getPath());
    assertNotNull(response.getBody().getTimestamp());
  }
}
