package com.example.digigoods.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Model Classes Tests")
class ModelClassesTest {

  @Nested
  @DisplayName("User Model Tests")
  class UserModelTest {

    @Test
    @DisplayName("Given User constructor with parameters, when creating user, then set all fields correctly")
    void givenUserConstructorWithParameters_whenCreatingUser_thenSetAllFieldsCorrectly() {
      // Act
      User user = new User(1L, "testuser", "password");

      // Assert
      assertEquals(1L, user.getId());
      assertEquals("testuser", user.getUsername());
      assertEquals("password", user.getPassword());
    }

    @Test
    @DisplayName("Given User no-args constructor, when creating user, then all fields are null")
    void givenUserNoArgsConstructor_whenCreatingUser_thenAllFieldsAreNull() {
      // Act
      User user = new User();

      // Assert
      assertNull(user.getId());
      assertNull(user.getUsername());
      assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Given User setters, when setting values, then getters return correct values")
    void givenUserSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      User user = new User();

      // Act
      user.setId(2L);
      user.setUsername("newuser");
      user.setPassword("newpassword");

      // Assert
      assertEquals(2L, user.getId());
      assertEquals("newuser", user.getUsername());
      assertEquals("newpassword", user.getPassword());
    }

    @Test
    @DisplayName("Given two users with same data, when comparing, then they are equal")
    void givenTwoUsersWithSameData_whenComparing_thenTheyAreEqual() {
      // Arrange
      User user1 = new User(1L, "testuser", "password");
      User user2 = new User(1L, "testuser", "password");

      // Assert
      assertEquals(user1, user2);
      assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Given two users with different data, when comparing, then they are not equal")
    void givenTwoUsersWithDifferentData_whenComparing_thenTheyAreNotEqual() {
      // Arrange
      User user1 = new User(1L, "testuser", "password");
      User user2 = new User(2L, "otheruser", "otherpassword");

      // Assert
      assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Given User toString, when called, then return string representation")
    void givenUserToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      User user = new User(1L, "testuser", "password");

      // Act
      String result = user.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("testuser"));
      assertTrue(result.contains("password"));
    }
  }

  @Nested
  @DisplayName("Product Model Tests")
  class ProductModelTest {

    @Test
    @DisplayName("Given Product constructor with parameters, when creating product, then set all fields correctly")
    void givenProductConstructorWithParameters_whenCreatingProduct_thenSetAllFieldsCorrectly() {
      // Act
      Product product = new Product(1L, "Test Product", new BigDecimal("99.99"), 10);

      // Assert
      assertEquals(1L, product.getId());
      assertEquals("Test Product", product.getName());
      assertEquals(new BigDecimal("99.99"), product.getPrice());
      assertEquals(10, product.getStock());
    }

    @Test
    @DisplayName("Given Product no-args constructor, when creating product, then all fields are null or zero")
    void givenProductNoArgsConstructor_whenCreatingProduct_thenAllFieldsAreNullOrZero() {
      // Act
      Product product = new Product();

      // Assert
      assertNull(product.getId());
      assertNull(product.getName());
      assertNull(product.getPrice());
      assertNull(product.getStock());
    }

    @Test
    @DisplayName("Given Product setters, when setting values, then getters return correct values")
    void givenProductSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      Product product = new Product();

      // Act
      product.setId(2L);
      product.setName("New Product");
      product.setPrice(new BigDecimal("149.99"));
      product.setStock(5);

      // Assert
      assertEquals(2L, product.getId());
      assertEquals("New Product", product.getName());
      assertEquals(new BigDecimal("149.99"), product.getPrice());
      assertEquals(5, product.getStock());
    }

    @Test
    @DisplayName("Given two products with same data, when comparing, then they are equal")
    void givenTwoProductsWithSameData_whenComparing_thenTheyAreEqual() {
      // Arrange
      Product product1 = new Product(1L, "Test Product", new BigDecimal("99.99"), 10);
      Product product2 = new Product(1L, "Test Product", new BigDecimal("99.99"), 10);

      // Assert
      assertEquals(product1, product2);
      assertEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    @DisplayName("Given Product toString, when called, then return string representation")
    void givenProductToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      Product product = new Product(1L, "Test Product", new BigDecimal("99.99"), 10);

      // Act
      String result = product.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("Test Product"));
      assertTrue(result.contains("99.99"));
    }
  }

  @Nested
  @DisplayName("Discount Model Tests")
  class DiscountModelTest {

    @Test
    @DisplayName("Given Discount constructor with parameters, when creating discount, then set all fields correctly")
    void givenDiscountConstructorWithParameters_whenCreatingDiscount_thenSetAllFieldsCorrectly() {
      // Arrange
      LocalDate validFrom = LocalDate.now();
      LocalDate validUntil = LocalDate.now().plusDays(30);
      Set<Product> applicableProducts = new HashSet<>();

      // Act
      Discount discount = new Discount(1L, "TEST20", new BigDecimal("20.00"), DiscountType.GENERAL,
          validFrom, validUntil, 5, applicableProducts);

      // Assert
      assertEquals(1L, discount.getId());
      assertEquals("TEST20", discount.getCode());
      assertEquals(new BigDecimal("20.00"), discount.getPercentage());
      assertEquals(DiscountType.GENERAL, discount.getType());
      assertEquals(validFrom, discount.getValidFrom());
      assertEquals(validUntil, discount.getValidUntil());
      assertEquals(5, discount.getRemainingUses());
      assertEquals(applicableProducts, discount.getApplicableProducts());
    }

    @Test
    @DisplayName("Given Discount no-args constructor, when creating discount, then all fields are null")
    void givenDiscountNoArgsConstructor_whenCreatingDiscount_thenAllFieldsAreNull() {
      // Act
      Discount discount = new Discount();

      // Assert
      assertNull(discount.getId());
      assertNull(discount.getCode());
      assertNull(discount.getPercentage());
      assertNull(discount.getType());
      assertNull(discount.getValidFrom());
      assertNull(discount.getValidUntil());
      assertNull(discount.getRemainingUses());
      assertNotNull(discount.getApplicableProducts());
      assertTrue(discount.getApplicableProducts().isEmpty());
    }

    @Test
    @DisplayName("Given Discount setters, when setting values, then getters return correct values")
    void givenDiscountSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      Discount discount = new Discount();
      LocalDate validFrom = LocalDate.now();
      LocalDate validUntil = LocalDate.now().plusDays(30);
      Set<Product> applicableProducts = new HashSet<>();

      // Act
      discount.setId(2L);
      discount.setCode("NEW15");
      discount.setPercentage(new BigDecimal("15.00"));
      discount.setType(DiscountType.PRODUCT_SPECIFIC);
      discount.setValidFrom(validFrom);
      discount.setValidUntil(validUntil);
      discount.setRemainingUses(3);
      discount.setApplicableProducts(applicableProducts);

      // Assert
      assertEquals(2L, discount.getId());
      assertEquals("NEW15", discount.getCode());
      assertEquals(new BigDecimal("15.00"), discount.getPercentage());
      assertEquals(DiscountType.PRODUCT_SPECIFIC, discount.getType());
      assertEquals(validFrom, discount.getValidFrom());
      assertEquals(validUntil, discount.getValidUntil());
      assertEquals(3, discount.getRemainingUses());
      assertEquals(applicableProducts, discount.getApplicableProducts());
    }

    @Test
    @DisplayName("Given Discount toString, when called, then return string representation")
    void givenDiscountToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      Discount discount = new Discount();
      discount.setCode("TEST20");
      discount.setPercentage(new BigDecimal("20.00"));

      // Act
      String result = discount.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("TEST20"));
      assertTrue(result.contains("20.00"));
    }
  }

  @Nested
  @DisplayName("Order Model Tests")
  class OrderModelTest {

    @Test
    @DisplayName("Given Order constructor with parameters, when creating order, then set all fields correctly")
    void givenOrderConstructorWithParameters_whenCreatingOrder_thenSetAllFieldsCorrectly() {
      // Arrange
      User user = new User(1L, "testuser", "password");
      Set<Product> products = new HashSet<>();
      Set<Discount> discounts = new HashSet<>();
      LocalDateTime orderDate = LocalDateTime.now();

      // Act
      Order order = new Order(1L, user, products, discounts,
          new BigDecimal("100.00"), new BigDecimal("80.00"), orderDate);

      // Assert
      assertEquals(1L, order.getId());
      assertEquals(user, order.getUser());
      assertEquals(products, order.getProducts());
      assertEquals(discounts, order.getAppliedDiscounts());
      assertEquals(new BigDecimal("100.00"), order.getOriginalSubtotal());
      assertEquals(new BigDecimal("80.00"), order.getFinalPrice());
      assertEquals(orderDate, order.getOrderDate());
    }

    @Test
    @DisplayName("Given Order no-args constructor, when creating order, then initialize collections")
    void givenOrderNoArgsConstructor_whenCreatingOrder_thenInitializeCollections() {
      // Act
      Order order = new Order();

      // Assert
      assertNull(order.getId());
      assertNull(order.getUser());
      assertNotNull(order.getProducts());
      assertNotNull(order.getAppliedDiscounts());
      assertTrue(order.getProducts().isEmpty());
      assertTrue(order.getAppliedDiscounts().isEmpty());
    }

    @Test
    @DisplayName("Given Order onCreate, when called, then set order date")
    void givenOrderOnCreate_whenCalled_thenSetOrderDate() {
      // Arrange
      Order order = new Order();
      LocalDateTime beforeCreate = LocalDateTime.now().minusSeconds(1);

      // Act
      order.onCreate();
      LocalDateTime afterCreate = LocalDateTime.now().plusSeconds(1);

      // Assert
      assertNotNull(order.getOrderDate());
      assertTrue(order.getOrderDate().isAfter(beforeCreate));
      assertTrue(order.getOrderDate().isBefore(afterCreate));
    }

    @Test
    @DisplayName("Given Order toString, when called, then return string representation")
    void givenOrderToString_whenCalled_thenReturnStringRepresentation() {
      // Arrange
      Order order = new Order();
      order.setId(1L);
      order.setOriginalSubtotal(new BigDecimal("100.00"));
      order.setFinalPrice(new BigDecimal("80.00"));

      // Act
      String result = order.toString();

      // Assert
      assertNotNull(result);
      assertTrue(result.contains("100.00"));
      assertTrue(result.contains("80.00"));
    }
  }

  @Nested
  @DisplayName("CartHeader Model Tests")
  class CartHeaderModelTest {

    private CartHeader cartHeader;
    private User user;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
      user = new User(1L, "testuser", "password");

      product1 = new Product();
      product1.setId(1L);
      product1.setName("Product 1");

      product2 = new Product();
      product2.setId(2L);
      product2.setName("Product 2");

      cartHeader = new CartHeader();
      cartHeader.setId(1L);
      cartHeader.setUser(user);
    }

    @Test
    @DisplayName("Given empty cart, when checking if product exists, then return false")
    void givenEmptyCart_whenCheckingIfProductExists_thenReturnFalse() {
      // Act
      boolean exists = cartHeader.isProductExists(1L);

      // Assert
      assertFalse(exists);
    }

    @Test
    @DisplayName("Given cart with product, when checking if same product exists, then return true")
    void givenCartWithProduct_whenCheckingIfSameProductExists_thenReturnTrue() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);

      // Act
      boolean exists = cartHeader.isProductExists(1L);

      // Assert
      assertTrue(exists);
    }

    @Test
    @DisplayName("Given cart with product, when checking if different product exists, then return false")
    void givenCartWithProduct_whenCheckingIfDifferentProductExists_thenReturnFalse() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);

      // Act
      boolean exists = cartHeader.isProductExists(2L);

      // Assert
      assertFalse(exists);
    }

    @Test
    @DisplayName("Given empty cart, when adding product, then product is added with correct details")
    void givenEmptyCart_whenAddingProduct_thenProductIsAddedWithCorrectDetails() {
      // Act
      cartHeader.addProductToCart(product1, 3);

      // Assert
      assertEquals(1, cartHeader.getCartDetails().size());
      CartDetail cartDetail = cartHeader.getCartDetails().get(0);
      assertEquals(cartHeader, cartDetail.getCartHeader());
      assertEquals(product1, cartDetail.getProduct());
      assertEquals(3, cartDetail.getQuantity());
    }

    @Test
    @DisplayName("Given cart with one product, when adding another product, then both products exist")
    void givenCartWithOneProduct_whenAddingAnotherProduct_thenBothProductsExist() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);

      // Act
      cartHeader.addProductToCart(product2, 4);

      // Assert
      assertEquals(2, cartHeader.getCartDetails().size());
      assertTrue(cartHeader.isProductExists(1L));
      assertTrue(cartHeader.isProductExists(2L));
    }

    @Test
    @DisplayName("Given cart with product, when modifying quantity, then quantity is updated")
    void givenCartWithProduct_whenModifyingQuantity_thenQuantityIsUpdated() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);

      // Act
      cartHeader.modifyQuantity(1L, 5);

      // Assert
      assertEquals(1, cartHeader.getCartDetails().size());
      CartDetail cartDetail = cartHeader.getCartDetails().get(0);
      assertEquals(5, cartDetail.getQuantity());
    }

    @Test
    @DisplayName("Given cart with multiple products, when modifying specific product quantity, then only that product is updated")
    void givenCartWithMultipleProducts_whenModifyingSpecificProductQuantity_thenOnlyThatProductIsUpdated() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);
      cartHeader.addProductToCart(product2, 3);

      // Act
      cartHeader.modifyQuantity(1L, 7);

      // Assert
      assertEquals(2, cartHeader.getCartDetails().size());

      CartDetail detail1 = cartHeader.getCartDetails().stream()
          .filter(d -> d.getProduct().getId().equals(1L))
          .findFirst().orElse(null);
      CartDetail detail2 = cartHeader.getCartDetails().stream()
          .filter(d -> d.getProduct().getId().equals(2L))
          .findFirst().orElse(null);

      assertNotNull(detail1);
      assertNotNull(detail2);
      assertEquals(7, detail1.getQuantity());
      assertEquals(3, detail2.getQuantity());
    }

    @Test
    @DisplayName("Given cart without specific product, when modifying quantity, then no changes occur")
    void givenCartWithoutSpecificProduct_whenModifyingQuantity_thenNoChangesOccur() {
      // Arrange
      cartHeader.addProductToCart(product1, 2);

      // Act
      cartHeader.modifyQuantity(999L, 5);

      // Assert
      assertEquals(1, cartHeader.getCartDetails().size());
      CartDetail cartDetail = cartHeader.getCartDetails().get(0);
      assertEquals(2, cartDetail.getQuantity()); // Original quantity unchanged
    }

    @Test
    @DisplayName("Given CartHeader setters, when setting values, then getters return correct values")
    void givenCartHeaderSetters_whenSettingValues_thenGettersReturnCorrectValues() {
      // Arrange
      CartHeader newCartHeader = new CartHeader();
      User newUser = new User(2L, "newuser", "newpass");

      // Act
      newCartHeader.setId(2L);
      newCartHeader.setUser(newUser);

      // Assert
      assertEquals(2L, newCartHeader.getId());
      assertEquals(newUser, newCartHeader.getUser());
      assertNotNull(newCartHeader.getCartDetails());
      assertTrue(newCartHeader.getCartDetails().isEmpty());
    }
  }

  @Nested
  @DisplayName("DiscountType Enum Tests")
  class DiscountTypeTest {

    @Test
    @DisplayName("Given DiscountType enum, when accessing values, then return correct values")
    void givenDiscountTypeEnum_whenAccessingValues_thenReturnCorrectValues() {
      // Assert
      assertEquals(2, DiscountType.values().length);
      assertEquals(DiscountType.PRODUCT_SPECIFIC, DiscountType.valueOf("PRODUCT_SPECIFIC"));
      assertEquals(DiscountType.GENERAL, DiscountType.valueOf("GENERAL"));
    }

    @Test
    @DisplayName("Given DiscountType toString, when called, then return string representation")
    void givenDiscountTypeToString_whenCalled_thenReturnStringRepresentation() {
      // Assert
      assertEquals("PRODUCT_SPECIFIC", DiscountType.PRODUCT_SPECIFIC.toString());
      assertEquals("GENERAL", DiscountType.GENERAL.toString());
    }

    @Test
    @DisplayName("Given DiscountType ordinal, when called, then return correct ordinal")
    void givenDiscountTypeOrdinal_whenCalled_thenReturnCorrectOrdinal() {
      // Assert
      assertEquals(0, DiscountType.PRODUCT_SPECIFIC.ordinal());
      assertEquals(1, DiscountType.GENERAL.ordinal());
    }
  }
}
