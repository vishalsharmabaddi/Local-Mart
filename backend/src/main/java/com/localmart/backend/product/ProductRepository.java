package com.localmart.backend.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShop_Id(Long shopId);   // "_" = go into shop, then its id → WHERE shop_id = ?
}
