package com.example.digigoods.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for order response.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

  private String message;
  private BigDecimal finalPrice;
}
