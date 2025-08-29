package com.example.digigoods.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetCartResponse {

  private Long productId;
  private String productName;
  private Integer quantity;

}
