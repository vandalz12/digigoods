package com.example.digigoods.repository;

import com.example.digigoods.model.CartHeader;
import com.example.digigoods.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartHeaderRepository extends JpaRepository<CartHeader, Long> {
  Optional<CartHeader> findByUser(User user);
}
