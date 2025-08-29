package com.example.digigoods.service;

import com.example.digigoods.dto.CheckoutRequest;
import com.example.digigoods.dto.OrderResponse;
import com.example.digigoods.exception.ExcessiveDiscountException;
import com.example.digigoods.exception.UnauthorizedAccessException;
import com.example.digigoods.model.Discount;
import com.example.digigoods.model.DiscountType;
import com.example.digigoods.model.Order;
import com.example.digigoods.model.Product;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.OrderRepository;
import com.example.digigoods.repository.UserRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for checkout operations.
 */
@Service
public class CheckoutService {

  private static final BigDecimal MAX_DISCOUNT_PERCENTAGE = new BigDecimal("75.00");
  private static final BigDecimal HUNDRED = new BigDecimal("100.00");

  private final ProductService productService;
  private final DiscountService discountService;
  private final OrderRepository orderRepository;
  private final UserRepository userRepository;

  public CheckoutService(ProductService productService,
                         DiscountService discountService,
                         OrderRepository orderRepository,
                         UserRepository userRepository) {
    this.productService = productService;
    this.discountService = discountService;
    this.orderRepository = orderRepository;
    this.userRepository = userRepository;
  }

  /**
   * Process checkout request.
   *
   * @param request the checkout request
   * @param authenticatedUserId the ID of the authenticated user
   * @return order response
   */
  @Transactional
  public OrderResponse processCheckout(CheckoutRequest request, Long authenticatedUserId) {
    // 1. Authentication & Authorization
    validateUserAuthorization(request.getUserId(), authenticatedUserId);

    // 2. Product Validation
    List<Product> products = productService.getProductsByIds(request.getProductIds());

    // 3. Original Subtotal Calculation
    BigDecimal originalSubtotal = calculateOriginalSubtotal(request.getProductIds(), products);

    // 4. Discount Validation
    List<Discount> discounts = discountService.validateAndGetDiscounts(request.getDiscountCodes());

    // 5. Discount Application
    BigDecimal finalPrice = applyDiscounts(request.getProductIds(), products, discounts,
        originalSubtotal);

    // 6. Maximum Discount Rule
    validateMaximumDiscount(originalSubtotal, finalPrice);

    // 7. Final Commit
    commitTransaction(request, products, discounts, originalSubtotal, finalPrice);

    return new OrderResponse("Order created successfully!", finalPrice);
  }

  private void validateUserAuthorization(Long requestUserId, Long authenticatedUserId) {
    if (!requestUserId.equals(authenticatedUserId)) {
      throw new UnauthorizedAccessException("User cannot place order for another user");
    }
  }

  private BigDecimal calculateOriginalSubtotal(List<Long> productIds, List<Product> products) {
    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, Function.identity()));

    return productIds.stream()
        .map(productMap::get)
        .map(Product::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal applyDiscounts(List<Long> productIds, List<Product> products,
                                    List<Discount> discounts,
                                    BigDecimal originalSubtotal) {
    Map<Long, Product> productMap = products.stream()
        .collect(Collectors.toMap(Product::getId, Function.identity()));

    // Separate discounts by type
    List<Discount> productSpecificDiscounts = discounts.stream()
        .filter(d -> d.getType() == DiscountType.PRODUCT_SPECIFIC)
        .toList();

    List<Discount> generalDiscounts = discounts.stream()
        .filter(d -> d.getType() == DiscountType.GENERAL)
        .toList();

    // Apply product-specific discounts first
    BigDecimal intermediateSubtotal = applyProductSpecificDiscounts(
        productIds, productMap, productSpecificDiscounts);

    // Apply general discounts to intermediate subtotal
    return applyGeneralDiscounts(intermediateSubtotal, generalDiscounts);
  }

  private BigDecimal applyProductSpecificDiscounts(List<Long> productIds,
                                                   Map<Long, Product> productMap,
                                                   List<Discount> productSpecificDiscounts) {
    BigDecimal total = BigDecimal.ZERO;

    for (Long productId : productIds) {
      Product product = productMap.get(productId);
      BigDecimal itemPrice = product.getPrice();

      // Find applicable discounts for this product
      BigDecimal totalDiscountPercentage = productSpecificDiscounts.stream()
          .filter(discount -> discount.getApplicableProducts().contains(product))
          .map(Discount::getPercentage)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      // Apply discount
      if (totalDiscountPercentage.compareTo(BigDecimal.ZERO) > 0) {
        BigDecimal discountAmount = itemPrice.multiply(totalDiscountPercentage)
            .divide(HUNDRED, 2, RoundingMode.HALF_UP);
        itemPrice = itemPrice.subtract(discountAmount);
      }

      total = total.add(itemPrice);
    }

    return total;
  }

  private BigDecimal applyGeneralDiscounts(BigDecimal subtotal, List<Discount> generalDiscounts) {
    BigDecimal currentPrice = subtotal;

    for (Discount discount : generalDiscounts) {
      BigDecimal discountAmount = currentPrice.multiply(discount.getPercentage())
          .divide(HUNDRED, 2, RoundingMode.HALF_UP);
      currentPrice = currentPrice.subtract(discountAmount);
    }

    return currentPrice;
  }

  private void validateMaximumDiscount(BigDecimal originalSubtotal, BigDecimal finalPrice) {
    BigDecimal totalDiscount = originalSubtotal.subtract(finalPrice);
    BigDecimal discountPercentage = totalDiscount.multiply(HUNDRED)
        .divide(originalSubtotal, 2, RoundingMode.HALF_UP);

    if (discountPercentage.compareTo(MAX_DISCOUNT_PERCENTAGE) > 0) {
      throw new ExcessiveDiscountException();
    }
  }

  private void commitTransaction(CheckoutRequest request, List<Product> products,
                                 List<Discount> discounts, BigDecimal originalSubtotal,
                                 BigDecimal finalPrice) {
    // Get user
    User user = userRepository.findById(request.getUserId())
        .orElseThrow(() -> new RuntimeException("User not found"));

    // Create order
    Order order = new Order();
    order.setUser(user);
    order.setProducts(new HashSet<>(products));
    order.setAppliedDiscounts(new HashSet<>(discounts));
    order.setOriginalSubtotal(originalSubtotal);
    order.setFinalPrice(finalPrice);

    orderRepository.save(order);

    // Update product stock
    productService.validateAndUpdateStock(request.getProductIds());

    // Update discount usage
    discountService.updateDiscountUsage(discounts);
  }
}
