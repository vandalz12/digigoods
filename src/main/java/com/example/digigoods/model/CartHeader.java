package com.example.digigoods.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "cart_headers")
public class CartHeader {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @OneToMany(mappedBy = "cartHeader", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CartDetail> cartDetails = new ArrayList<>();

  public boolean isProductExists(Long productId) {
    return cartDetails.stream()
        .anyMatch(detail -> detail.getProduct().getId().equals(productId));
  }

  public void addProductToCart(Product product, Integer quantity) {
    CartDetail cartDetail = new CartDetail();
    cartDetail.setCartHeader(this);
    cartDetail.setQuantity(quantity);
    cartDetail.setProduct(product);
    cartDetails.add(cartDetail);
  }

  public void modifyQuantity(Long productId, Integer quantity) {
    cartDetails.stream()
        .filter(detail -> detail.getProduct().getId().equals(productId))
        .findFirst()
        .ifPresent(detail -> detail.setQuantity(quantity));
  }

}
