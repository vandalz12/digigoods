package com.example.digigoods.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.digigoods.dto.AddToCartRequest;
import com.example.digigoods.dto.AddToCartResponse;
import com.example.digigoods.dto.GetCartResponse;
import com.example.digigoods.exception.InsufficientStockException;
import com.example.digigoods.model.CartDetail;
import com.example.digigoods.model.CartHeader;
import com.example.digigoods.model.Product;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.CartDetailRepository;
import com.example.digigoods.repository.CartHeaderRepository;
import com.example.digigoods.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for CartService to ensure 100% code coverage.
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ProductService productService;

  @Mock
  private CartHeaderRepository cartHeaderRepository;

  @Mock
  private CartDetailRepository cartDetailRepository;

  @InjectMocks
  private CartService cartService;

  private User user;
  private Product product;
  private CartHeader cartHeader;
  private CartDetail cartDetail;
  private AddToCartRequest addToCartRequest;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setUsername("testuser");

    product = new Product();
    product.setId(1L);
    product.setName("Test Product");
    product.setStock(10);

    cartHeader = new CartHeader();
    cartHeader.setId(1L);
    cartHeader.setUser(user);

    cartDetail = new CartDetail();
    cartDetail.setId(1L);
    cartDetail.setCartHeader(cartHeader);
    cartDetail.setProduct(product);
    cartDetail.setQuantity(2);

    addToCartRequest = new AddToCartRequest(1L, 2);
  }

  @Test
  @DisplayName("Given valid request with existing cart, when addToCart, then add product to existing cart")
  void givenValidRequestWithExistingCart_whenAddToCart_thenAddProductToExistingCart() {
    // Arrange
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);
    when(cartHeaderRepository.findByUser(user)).thenReturn(Optional.of(cartHeader));
    when(cartHeaderRepository.save(any(CartHeader.class))).thenReturn(cartHeader);

    // Act
    AddToCartResponse response = cartService.addToCart(addToCartRequest, userId);

    // Assert
    assertNotNull(response);
    assertEquals("Product added to cart successfully!", response.getMessage());
    assertEquals("Product Name", response.getProductName());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository).findByUser(user);
    verify(cartHeaderRepository).save(cartHeader);
  }

  @Test
  @DisplayName("Given valid request without existing cart, when addToCart, then create new cart and add product")
  void givenValidRequestWithoutExistingCart_whenAddToCart_thenCreateNewCartAndAddProduct() {
    // Arrange
    Long userId = 1L;
    CartHeader newCartHeader = new CartHeader();
    newCartHeader.setUser(user);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);
    when(cartHeaderRepository.findByUser(user)).thenReturn(Optional.empty());
    when(cartHeaderRepository.save(any(CartHeader.class))).thenReturn(newCartHeader);

    // Act
    AddToCartResponse response = cartService.addToCart(addToCartRequest, userId);

    // Assert
    assertNotNull(response);
    assertEquals("Product added to cart successfully!", response.getMessage());
    assertEquals("Product Name", response.getProductName());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository).findByUser(user);
    verify(cartHeaderRepository, org.mockito.Mockito.times(2)).save(any(CartHeader.class));
  }

  @Test
  @DisplayName("Given user not found, when addToCart, then throw RuntimeException")
  void givenUserNotFound_whenAddToCart_thenThrowRuntimeException() {
    // Arrange
    Long userId = 1L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert
    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> cartService.addToCart(addToCartRequest, userId));

    assertEquals("User not found", exception.getMessage());
    verify(userRepository).findById(userId);
    verify(productService, never()).getProductById(any());
    verify(cartHeaderRepository, never()).findByUser(any());
    verify(cartHeaderRepository, never()).save(any());
  }

  @Test
  @DisplayName("Given insufficient stock, when addToCart, then throw InsufficientStockException")
  void givenInsufficientStock_whenAddToCart_thenThrowInsufficientStockException() {
    // Arrange
    Long userId = 1L;
    product.setStock(1); // Less than requested quantity (2)
    addToCartRequest = new AddToCartRequest(1L, 2);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);

    // Act & Assert
    InsufficientStockException exception = assertThrows(InsufficientStockException.class,
        () -> cartService.addToCart(addToCartRequest, userId));

    assertEquals("Insufficient stock for product 1. Requested: 2, Available: 1", exception.getMessage());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository, never()).findByUser(any());
    verify(cartHeaderRepository, never()).save(any());
  }

  @Test
  @DisplayName("Given valid user ID, when getCart, then return cart items")
  void givenValidUserId_whenGetCart_thenReturnCartItems() {
    // Arrange
    Long userId = 1L;
    List<CartDetail> cartDetails = List.of(cartDetail);
    when(cartDetailRepository.findByUserId(userId)).thenReturn(cartDetails);

    // Act
    List<GetCartResponse> response = cartService.getCart(userId);

    // Assert
    assertNotNull(response);
    assertEquals(1, response.size());
    GetCartResponse cartResponse = response.get(0);
    assertEquals(1L, cartResponse.getProductId());
    assertEquals("Test Product", cartResponse.getProductName());
    assertEquals(2, cartResponse.getQuantity());
    verify(cartDetailRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Given user with empty cart, when getCart, then return empty list")
  void givenUserWithEmptyCart_whenGetCart_thenReturnEmptyList() {
    // Arrange
    Long userId = 1L;
    when(cartDetailRepository.findByUserId(userId)).thenReturn(List.of());

    // Act
    List<GetCartResponse> response = cartService.getCart(userId);

    // Assert
    assertNotNull(response);
    assertEquals(0, response.size());
    verify(cartDetailRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Given multiple cart items, when getCart, then return all items")
  void givenMultipleCartItems_whenGetCart_thenReturnAllItems() {
    // Arrange
    Long userId = 1L;

    Product product2 = new Product();
    product2.setId(2L);
    product2.setName("Test Product 2");

    CartDetail cartDetail2 = new CartDetail();
    cartDetail2.setId(2L);
    cartDetail2.setCartHeader(cartHeader);
    cartDetail2.setProduct(product2);
    cartDetail2.setQuantity(3);

    List<CartDetail> cartDetails = List.of(cartDetail, cartDetail2);
    when(cartDetailRepository.findByUserId(userId)).thenReturn(cartDetails);

    // Act
    List<GetCartResponse> response = cartService.getCart(userId);

    // Assert
    assertNotNull(response);
    assertEquals(2, response.size());

    GetCartResponse firstItem = response.get(0);
    assertEquals(1L, firstItem.getProductId());
    assertEquals("Test Product", firstItem.getProductName());
    assertEquals(2, firstItem.getQuantity());

    GetCartResponse secondItem = response.get(1);
    assertEquals(2L, secondItem.getProductId());
    assertEquals("Test Product 2", secondItem.getProductName());
    assertEquals(3, secondItem.getQuantity());

    verify(cartDetailRepository).findByUserId(userId);
  }

  @Test
  @DisplayName("Given exact stock quantity requested, when addToCart, then add product successfully")
  void givenExactStockQuantityRequested_whenAddToCart_thenAddProductSuccessfully() {
    // Arrange
    Long userId = 1L;
    product.setStock(2); // Exact quantity requested
    addToCartRequest = new AddToCartRequest(1L, 2);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);
    when(cartHeaderRepository.findByUser(user)).thenReturn(Optional.of(cartHeader));
    when(cartHeaderRepository.save(any(CartHeader.class))).thenReturn(cartHeader);

    // Act
    AddToCartResponse response = cartService.addToCart(addToCartRequest, userId);

    // Assert
    assertNotNull(response);
    assertEquals("Product added to cart successfully!", response.getMessage());
    assertEquals("Product Name", response.getProductName());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository).findByUser(user);
    verify(cartHeaderRepository).save(cartHeader);
  }

  @Test
  @DisplayName("Given quantity of 1, when addToCart, then add product successfully")
  void givenQuantityOfOne_whenAddToCart_thenAddProductSuccessfully() {
    // Arrange
    Long userId = 1L;
    addToCartRequest = new AddToCartRequest(1L, 1);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);
    when(cartHeaderRepository.findByUser(user)).thenReturn(Optional.of(cartHeader));
    when(cartHeaderRepository.save(any(CartHeader.class))).thenReturn(cartHeader);

    // Act
    AddToCartResponse response = cartService.addToCart(addToCartRequest, userId);

    // Assert
    assertNotNull(response);
    assertEquals("Product added to cart successfully!", response.getMessage());
    assertEquals("Product Name", response.getProductName());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository).findByUser(user);
    verify(cartHeaderRepository).save(cartHeader);
  }

  @Test
  @DisplayName("Given existing cart with same product, when addToCart, then modify existing quantity")
  void givenExistingCartWithSameProduct_whenAddToCart_thenModifyExistingQuantity() {
    // Arrange
    Long userId = 1L;
    cartHeader.addProductToCart(product, 1); // Pre-existing product in cart

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(productService.getProductById(1L)).thenReturn(product);
    when(cartHeaderRepository.findByUser(user)).thenReturn(Optional.of(cartHeader));
    when(cartHeaderRepository.save(any(CartHeader.class))).thenReturn(cartHeader);

    // Act
    AddToCartResponse response = cartService.addToCart(addToCartRequest, userId);

    // Assert
    assertNotNull(response);
    assertEquals("Product added to cart successfully!", response.getMessage());
    assertEquals("Product Name", response.getProductName());
    verify(userRepository).findById(userId);
    verify(productService).getProductById(1L);
    verify(cartHeaderRepository).findByUser(user);
    verify(cartHeaderRepository).save(cartHeader);
  }
}
