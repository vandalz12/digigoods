package com.example.digigoods.service;

import com.example.digigoods.exception.InvalidDiscountException;
import com.example.digigoods.model.Discount;
import com.example.digigoods.repository.DiscountRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service for discount operations.
 */
@Service
public class DiscountService {

  private final DiscountRepository discountRepository;

  public DiscountService(DiscountRepository discountRepository) {
    this.discountRepository = discountRepository;
  }

  /**
   * Get all discounts from the database.
   *
   * @return list of all discounts
   */
  public List<Discount> getAllDiscounts() {
    return discountRepository.findAll();
  }

  /**
   * Validate and get discounts by their codes.
   *
   * @param discountCodes the list of discount codes
   * @return list of valid discounts
   * @throws InvalidDiscountException if any discount is invalid
   */
  public List<Discount> validateAndGetDiscounts(List<String> discountCodes) {
    if (discountCodes == null || discountCodes.isEmpty()) {
      return List.of();
    }

    List<Discount> discounts = discountRepository.findAllByCodeIn(discountCodes);

    // Check if all codes were found
    if (discounts.size() != discountCodes.size()) {
      List<String> foundCodes = discounts.stream()
          .map(Discount::getCode)
          .toList();

      String missingCode = discountCodes.stream()
          .filter(code -> !foundCodes.contains(code))
          .findFirst()
          .orElse("unknown");

      throw new InvalidDiscountException(missingCode, "discount code not found");
    }

    // Validate each discount
    LocalDate today = LocalDate.now();
    for (Discount discount : discounts) {
      validateDiscount(discount, today);
    }

    return discounts;
  }

  /**
   * Update remaining uses for discounts.
   *
   * @param discounts the list of discounts to update
   */
  public void updateDiscountUsage(List<Discount> discounts) {
    for (Discount discount : discounts) {
      discount.setRemainingUses(discount.getRemainingUses() - 1);
      discountRepository.save(discount);
    }
  }

  private void validateDiscount(Discount discount, LocalDate today) {
    // Check if discount is expired
    if (today.isBefore(discount.getValidFrom())) {
      throw new InvalidDiscountException(discount.getCode(), "discount is not yet valid");
    }

    if (today.isAfter(discount.getValidUntil())) {
      throw new InvalidDiscountException(discount.getCode(), "discount has expired");
    }

    // Check if discount has remaining uses
    if (discount.getRemainingUses() <= 0) {
      throw new InvalidDiscountException(discount.getCode(), "discount has no remaining uses");
    }
  }
}
