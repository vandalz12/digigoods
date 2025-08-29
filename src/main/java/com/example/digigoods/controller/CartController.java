package com.example.digigoods.controller;

import com.example.digigoods.dto.AddToCartRequest;
import com.example.digigoods.dto.AddToCartResponse;
import com.example.digigoods.dto.GetCartResponse;
import com.example.digigoods.exception.MissingJwtTokenException;
import com.example.digigoods.service.CartService;
import com.example.digigoods.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;
  private final JwtService jwtService;

  @PostMapping
  public ResponseEntity<AddToCartResponse> addToCart(@Valid @RequestBody AddToCartRequest addToCartRequest, HttpServletRequest request) {
    String token = extractTokenFromRequest(request);
    if (token == null) {
      throw new MissingJwtTokenException();
    }
    Long authenticatedUserId = jwtService.extractUserId(token);
    return ResponseEntity.ok(cartService.addToCart(addToCartRequest, authenticatedUserId));
  }

  @GetMapping
  public ResponseEntity<List<GetCartResponse>> getCart(HttpServletRequest request) {
    String token = extractTokenFromRequest(request);
    if (token == null) {
      throw new MissingJwtTokenException();
    }
    Long authenticatedUserId = jwtService.extractUserId(token);
    return ResponseEntity.ok(cartService.getCart(authenticatedUserId));
  }

  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
