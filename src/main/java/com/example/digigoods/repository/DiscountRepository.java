package com.example.digigoods.repository;

import com.example.digigoods.model.Discount;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Discount entity.
 */
@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

  /**
   * Find a discount by its code.
   *
   * @param code the discount code
   * @return an Optional containing the discount if found
   */
  Optional<Discount> findByCode(String code);

  /**
   * Find all discounts by their codes.
   *
   * @param codes the list of discount codes
   * @return list of discounts
   */
  List<Discount> findAllByCodeIn(List<String> codes);
}
