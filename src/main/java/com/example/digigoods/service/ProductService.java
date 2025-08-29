package com.example.digigoods.service;

import com.example.digigoods.exception.InsufficientStockException;
import com.example.digigoods.exception.ProductNotFoundException;
import com.example.digigoods.model.Product;
import com.example.digigoods.repository.ProductRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service for product operations.
 */
@Service
public class ProductService {

  private final ProductRepository productRepository;

  public ProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Get products by their IDs and validate they exist.
   *
   * @param productIds the list of product IDs
   * @return list of products
   * @throws ProductNotFoundException if any product is not found
   */
  public List<Product> getProductsByIds(List<Long> productIds) {
    List<Product> products = productRepository.findAllByIdIn(productIds);

    if (products.size() != productIds.size()) {
      // Find missing product IDs
      List<Long> foundIds = products.stream()
          .map(Product::getId)
          .collect(Collectors.toList());

      List<Long> missingIds = productIds.stream()
          .filter(id -> !foundIds.contains(id))
          .collect(Collectors.toList());

      throw new ProductNotFoundException("Products not found with IDs: " + missingIds);
    }

    return products;
  }

  /**
   * Get all products from the database.
   *
   * @return list of all products
   */
  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  /**
   * Validate and update stock for products.
   *
   * @param productIds the list of product IDs (with duplicates for quantity)
   * @throws InsufficientStockException if any product has insufficient stock
   */
  public void validateAndUpdateStock(List<Long> productIds) {
    // Count quantities for each product
    Map<Long, Long> productQuantities = productIds.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

    // Get all unique products
    List<Product> products = getProductsByIds(productQuantities.keySet().stream()
        .collect(Collectors.toList()));

    // Validate stock availability
    for (Product product : products) {
      Long requestedQuantity = productQuantities.get(product.getId());
      if (product.getStock() < requestedQuantity) {
        throw new InsufficientStockException(
            product.getId(),
            requestedQuantity.intValue(),
            product.getStock()
        );
      }
    }

    // Update stock
    for (Product product : products) {
      Long requestedQuantity = productQuantities.get(product.getId());
      product.setStock(product.getStock() - requestedQuantity.intValue());
      productRepository.save(product);
    }
  }
}
