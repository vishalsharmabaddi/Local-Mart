package com.localmart.backend.order;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        Long shopId,
        String shopName,
        OrderStatus status,
        int totalAmount,
        Instant createdAt,
        List<OrderItemResponse> items
) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getShop().getId(),
                order.getShop().getName(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
