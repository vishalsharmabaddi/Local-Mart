package com.localmart.backend.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByShop_Id(Long shopId);   // "_" = go into shop, then its id → WHERE shop_id = ?

    // products + their shops in ONE query (no N+1), one PAGE at a time.
    // countQuery: Spring also needs the TOTAL count for "totalPages"; no join needed for counting.
    @Query(value = "select p from Product p join fetch p.shop",
           countQuery = "select count(p) from Product p")
    Page<Product> findAllWithShop(Pageable pageable);
}
