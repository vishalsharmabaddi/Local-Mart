package com.localmart.backend.order;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByShop_Id(Long shopId);

    // @EntityGraph = "load shop + items in the SAME query" → no N+1 (like JOIN FETCH, without writing JPQL)
    @EntityGraph(attributePaths = {"shop", "items"})
    List<Order> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);   // newest first

    @EntityGraph(attributePaths = {"shop", "items"})
    List<Order> findByShop_IdOrderByCreatedAtDesc(Long shopId);

    @EntityGraph(attributePaths = {"shop", "items"})
    Optional<Order> findWithItemsById(Long id);   // words between "find" and "By" are just a label
}
