package com.example.digigoods.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Discount entity representing a discount in the system.
 */
@Entity
@Table(name = "discounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Discount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;

  @Column(nullable = false, precision = 5, scale = 2)
  private BigDecimal percentage;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DiscountType type;

  @Column(name = "valid_from", nullable = false)
  private LocalDate validFrom;

  @Column(name = "valid_until", nullable = false)
  private LocalDate validUntil;

  @Column(name = "remaining_uses", nullable = false)
  private Integer remainingUses;

  @ManyToMany
  @JoinTable(
      name = "discount_applicable_products",
      joinColumns = @JoinColumn(name = "discount_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  private Set<Product> applicableProducts = new HashSet<>();
}
