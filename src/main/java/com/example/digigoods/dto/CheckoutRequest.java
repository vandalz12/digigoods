package com.example.digigoods.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for checkout request.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {

  @NotNull(message = "User ID is required")
  private Long userId;

  @NotEmpty(message = "Product IDs cannot be empty")
  private List<Long> productIds;

  private List<String> discountCodes;
}
