package com.example.digigoods.controller;

import com.example.digigoods.model.Discount;
import com.example.digigoods.service.DiscountService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for discount endpoints.
 */
@RestController
@RequestMapping("/discounts")
public class DiscountController {

  private final DiscountService discountService;

  public DiscountController(DiscountService discountService) {
    this.discountService = discountService;
  }

  /**
   * Get all discounts endpoint.
   *
   * @return list of all discounts
   */
  @GetMapping
  public ResponseEntity<List<Discount>> getAllDiscounts() {
    List<Discount> discounts = discountService.getAllDiscounts();
    return ResponseEntity.ok(discounts);
  }
}
