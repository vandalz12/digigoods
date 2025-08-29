package com.example.digigoods.controller;

import com.example.digigoods.dto.CheckoutRequest;
import com.example.digigoods.dto.OrderResponse;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.service.CheckoutService;
import com.example.digigoods.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for checkout endpoints.
 */
@RestController
@RequestMapping("/orders")
public class CheckoutController {

  private final CheckoutService checkoutService;
  private final JwtService jwtService;

  public CheckoutController(CheckoutService checkoutService, JwtService jwtService) {
    this.checkoutService = checkoutService;
    this.jwtService = jwtService;
  }

  /**
   * Create order endpoint.
   *
   * @param checkoutRequest the checkout request
   * @param request the HTTP servlet request
   * @return order response
   */
  @PostMapping
  public ResponseEntity<OrderResponse> createOrder(
      @Valid @RequestBody CheckoutRequest checkoutRequest,
      HttpServletRequest request) {
    // Extract user ID from JWT token
    String token = extractTokenFromRequest(request);
    if (token == null) {
      throw new MissingJwtTokenException();
    }
    Long authenticatedUserId = jwtService.extractUserId(token);

    OrderResponse response = checkoutService.processCheckout(checkoutRequest, authenticatedUserId);
    return ResponseEntity.ok(response);
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
