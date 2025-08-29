package com.example.digigoods.repository;

import com.example.digigoods.model.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {

  @Query("SELECT cd FROM CartDetail cd JOIN FETCH cd.cartHeader ch JOIN FETCH cd.product cp WHERE ch.user.id = :userId")
  List<CartDetail> findByUserId(@Param("userId") Long userId);

}
