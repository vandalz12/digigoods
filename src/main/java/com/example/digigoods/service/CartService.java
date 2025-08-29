package com.example.digigoods.service;

import com.example.digigoods.dto.AddToCartRequest;
import com.example.digigoods.dto.AddToCartResponse;
import com.example.digigoods.dto.GetCartResponse;
import com.example.digigoods.exception.InsufficientStockException;
import com.example.digigoods.model.CartHeader;
import com.example.digigoods.model.Product;
import com.example.digigoods.model.User;
import com.example.digigoods.repository.CartDetailRepository;
import com.example.digigoods.repository.CartHeaderRepository;
import com.example.digigoods.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

  private final UserRepository userRepository;
  private final ProductService productService;
  private final CartHeaderRepository cartHeaderRepository;
  private final CartDetailRepository cartDetailRepository;

  @Transactional
  public AddToCartResponse addToCart(AddToCartRequest addToCartRequest, Long authenticatedUserId) {

    User user = userRepository.findById(authenticatedUserId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    Product product = productService.getProductById(addToCartRequest.getProductId());

    validateAvailableStock(product, addToCartRequest.getQuantity());

    addCart(product, addToCartRequest.getQuantity(), user);

    return new AddToCartResponse("Product added to cart successfully!", "Product Name");
  }

  private void validateAvailableStock(Product product, int quantity) {
    if (product.getStock() < quantity) {
      throw new InsufficientStockException(
          product.getId(),
          quantity,
          product.getStock()
      );
    }
  }

  private void addCart(Product product, int quantity, User user) {

    CartHeader cartHeader = cartHeaderRepository.findByUser(user)
      .orElseGet(() -> {
        CartHeader newCartHeader = new CartHeader();
        newCartHeader.setUser(user);
        return cartHeaderRepository.save(newCartHeader);
      });

    if(cartHeader.isProductExists(product.getId())) {
      cartHeader.modifyQuantity(product.getId(), quantity);
    } else {
      cartHeader.addProductToCart(product, quantity);
    }

    cartHeaderRepository.save(cartHeader);

  }

  @Transactional(readOnly = true)
  public List<GetCartResponse> getCart(Long authenticatedUserId) {
    return cartDetailRepository
      .findByUserId(authenticatedUserId)
      .stream()
      .map(cartDetail ->
          GetCartResponse.builder()
            .productId(cartDetail.getProduct().getId())
            .productName(cartDetail.getProduct().getName())
            .quantity(cartDetail.getQuantity())
            .build()
      )
      .toList();
  }

}
