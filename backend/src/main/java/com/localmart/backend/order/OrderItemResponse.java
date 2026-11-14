package com.localmart.backend.order;

public record OrderItemResponse(Long productId, String productName, int unitPrice, int quantity, int lineTotal) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(item.getProduct().getId(), item.getProductName(),
                item.getUnitPrice(), item.getQuantity(), item.getLineTotal());
    }
}
