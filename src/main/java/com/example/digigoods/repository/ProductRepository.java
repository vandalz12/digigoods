package com.example.digigoods.repository;

import com.example.digigoods.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Product entity.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Find all products by their IDs.
   *
   * @param ids the list of product IDs
   * @return list of products
   */
  List<Product> findAllByIdIn(List<Long> ids);
}
