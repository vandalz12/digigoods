package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CheckoutService Tests")
class CheckoutServiceTest {

  @Mock
  private ProductService productService;

  @Mock
  private DiscountService discountService;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private CheckoutService checkoutService;

  private User testUser;
  private Product product1;
  private Product product2;
  private Discount generalDiscount;
  private Discount productSpecificDiscount;
  private CheckoutRequest checkoutRequest;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    testUser.setPassword("password");

    product1 = new Product();
    product1.setId(1L);
    product1.setName("Product 1");
    product1.setPrice(new BigDecimal("100.00"));
    product1.setStock(10);

    product2 = new Product();
    product2.setId(2L);
    product2.setName("Product 2");
    product2.setPrice(new BigDecimal("50.00"));
    product2.setStock(5);

    generalDiscount = new Discount();
    generalDiscount.setId(1L);
    generalDiscount.setCode("GENERAL20");
    generalDiscount.setPercentage(new BigDecimal("20.00"));
    generalDiscount.setType(DiscountType.GENERAL);
    generalDiscount.setValidFrom(LocalDate.now().minusDays(1));
    generalDiscount.setValidUntil(LocalDate.now().plusDays(30));
    generalDiscount.setRemainingUses(5);
    generalDiscount.setApplicableProducts(new HashSet<>());

    productSpecificDiscount = new Discount();
    productSpecificDiscount.setId(2L);
    productSpecificDiscount.setCode("PRODUCT10");
    productSpecificDiscount.setPercentage(new BigDecimal("10.00"));
    productSpecificDiscount.setType(DiscountType.PRODUCT_SPECIFIC);
    productSpecificDiscount.setValidFrom(LocalDate.now().minusDays(1));
    productSpecificDiscount.setValidUntil(LocalDate.now().plusDays(30));
    productSpecificDiscount.setRemainingUses(3);
    productSpecificDiscount.setApplicableProducts(new HashSet<>(List.of(product1)));

    checkoutRequest = new CheckoutRequest();
    checkoutRequest.setUserId(1L);
    checkoutRequest.setProductIds(List.of(1L, 2L));
    checkoutRequest.setDiscountCodes(List.of("GENERAL20"));
  }

  @Test
  @DisplayName("Given valid checkout request, when processCheckout, then return order response")
  void givenValidCheckoutRequest_whenProcessCheckout_thenReturnOrderResponse() {
    // Arrange
    List<Product> products = List.of(product1, product2);
    List<Discount> discounts = List.of(generalDiscount);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(checkoutRequest.getDiscountCodes())).thenReturn(discounts);
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    // Act
    OrderResponse response = checkoutService.processCheckout(checkoutRequest, 1L);

    // Assert
    assertNotNull(response);
    assertEquals("Order created successfully!", response.getMessage());
    assertNotNull(response.getFinalPrice());
    
    verify(productService).getProductsByIds(checkoutRequest.getProductIds());
    verify(discountService).validateAndGetDiscounts(checkoutRequest.getDiscountCodes());
    verify(productService).validateAndUpdateStock(checkoutRequest.getProductIds());
    verify(discountService).updateDiscountUsage(discounts);
    verify(orderRepository).save(any(Order.class));
  }

  @Test
  @DisplayName("Given unauthorized user, when processCheckout, then throw UnauthorizedAccessException")
  void givenUnauthorizedUser_whenProcessCheckout_thenThrowUnauthorizedAccessException() {
    // Arrange
    Long differentUserId = 2L;

    // Act & Assert
    UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
        () -> checkoutService.processCheckout(checkoutRequest, differentUserId));
    
    assertEquals("User cannot place order for another user", exception.getMessage());
  }

  @Test
  @DisplayName("Given checkout with no discounts, when processCheckout, then process successfully")
  void givenCheckoutWithNoDiscounts_whenProcessCheckout_thenProcessSuccessfully() {
    // Arrange
    checkoutRequest.setDiscountCodes(null);
    List<Product> products = List.of(product1, product2);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(null)).thenReturn(List.of());
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    // Act
    OrderResponse response = checkoutService.processCheckout(checkoutRequest, 1L);

    // Assert
    assertNotNull(response);
    assertEquals("Order created successfully!", response.getMessage());
    assertEquals(new BigDecimal("150.00"), response.getFinalPrice());
  }

  @Test
  @DisplayName("Given excessive discount, when processCheckout, then throw ExcessiveDiscountException")
  void givenExcessiveDiscount_whenProcessCheckout_thenThrowExcessiveDiscountException() {
    // Arrange
    Discount excessiveDiscount = new Discount();
    excessiveDiscount.setCode("EXCESSIVE80");
    excessiveDiscount.setPercentage(new BigDecimal("80.00"));
    excessiveDiscount.setType(DiscountType.GENERAL);
    excessiveDiscount.setValidFrom(LocalDate.now().minusDays(1));
    excessiveDiscount.setValidUntil(LocalDate.now().plusDays(30));
    excessiveDiscount.setRemainingUses(5);
    excessiveDiscount.setApplicableProducts(new HashSet<>());

    checkoutRequest.setDiscountCodes(List.of("EXCESSIVE80"));
    List<Product> products = List.of(product1, product2);
    List<Discount> discounts = List.of(excessiveDiscount);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(checkoutRequest.getDiscountCodes())).thenReturn(discounts);

    // Act & Assert
    assertThrows(ExcessiveDiscountException.class,
        () -> checkoutService.processCheckout(checkoutRequest, 1L));
  }

  @Test
  @DisplayName("Given product specific discount, when processCheckout, then apply discount correctly")
  void givenProductSpecificDiscount_whenProcessCheckout_thenApplyDiscountCorrectly() {
    // Arrange
    checkoutRequest.setDiscountCodes(List.of("PRODUCT10"));
    List<Product> products = List.of(product1, product2);
    List<Discount> discounts = List.of(productSpecificDiscount);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(checkoutRequest.getDiscountCodes())).thenReturn(discounts);
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    // Act
    OrderResponse response = checkoutService.processCheckout(checkoutRequest, 1L);

    // Assert
    assertNotNull(response);
    assertEquals("Order created successfully!", response.getMessage());
    // Product1 (100) gets 10% discount = 90, Product2 (50) no discount = 50, Total = 140
    assertEquals(new BigDecimal("140.00"), response.getFinalPrice());
  }

  @Test
  @DisplayName("Given user not found, when processCheckout, then throw RuntimeException")
  void givenUserNotFound_whenProcessCheckout_thenThrowRuntimeException() {
    // Arrange
    List<Product> products = List.of(product1, product2);
    List<Discount> discounts = List.of(generalDiscount);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(checkoutRequest.getDiscountCodes())).thenReturn(discounts);
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> checkoutService.processCheckout(checkoutRequest, 1L));
    
    assertEquals("User not found", exception.getMessage());
  }

  @Test
  @DisplayName("Given multiple products with same ID, when processCheckout, then calculate correctly")
  void givenMultipleProductsWithSameId_whenProcessCheckout_thenCalculateCorrectly() {
    // Arrange
    checkoutRequest.setProductIds(List.of(1L, 1L, 2L)); // 2x product1, 1x product2
    checkoutRequest.setDiscountCodes(null);
    List<Product> products = List.of(product1, product2);
    
    when(productService.getProductsByIds(anyList())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(null)).thenReturn(List.of());
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    // Act
    OrderResponse response = checkoutService.processCheckout(checkoutRequest, 1L);

    // Assert
    assertNotNull(response);
    assertEquals("Order created successfully!", response.getMessage());
    // 2x Product1 (100) + 1x Product2 (50) = 250
    assertEquals(new BigDecimal("250.00"), response.getFinalPrice());
  }

  @Test
  @DisplayName("Given multiple general discounts, when processCheckout, then apply all discounts")
  void givenMultipleGeneralDiscounts_whenProcessCheckout_thenApplyAllDiscounts() {
    // Arrange
    Discount secondGeneralDiscount = new Discount();
    secondGeneralDiscount.setCode("GENERAL10");
    secondGeneralDiscount.setPercentage(new BigDecimal("10.00"));
    secondGeneralDiscount.setType(DiscountType.GENERAL);
    secondGeneralDiscount.setValidFrom(LocalDate.now().minusDays(1));
    secondGeneralDiscount.setValidUntil(LocalDate.now().plusDays(30));
    secondGeneralDiscount.setRemainingUses(5);
    secondGeneralDiscount.setApplicableProducts(new HashSet<>());

    checkoutRequest.setDiscountCodes(List.of("GENERAL20", "GENERAL10"));
    List<Product> products = List.of(product1, product2);
    List<Discount> discounts = List.of(generalDiscount, secondGeneralDiscount);
    
    when(productService.getProductsByIds(checkoutRequest.getProductIds())).thenReturn(products);
    when(discountService.validateAndGetDiscounts(checkoutRequest.getDiscountCodes())).thenReturn(discounts);
    when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
    when(orderRepository.save(any(Order.class))).thenReturn(new Order());

    // Act
    OrderResponse response = checkoutService.processCheckout(checkoutRequest, 1L);

    // Assert
    assertNotNull(response);
    assertEquals("Order created successfully!", response.getMessage());
    // 150 * 0.8 * 0.9 = 108.00
    assertEquals(new BigDecimal("108.00"), response.getFinalPrice());
  }
}
