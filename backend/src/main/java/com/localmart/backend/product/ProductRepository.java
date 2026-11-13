package com.localmart.backend.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShop_Id(Long shopId);   // "_" = go into shop, then its id → WHERE shop_id = ?

    @Query("select p from Product p join fetch p.shop")   // products + their shops in ONE query
    List<Product> findAllWithShop();
}
