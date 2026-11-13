package com.localmart.backend.product;

public record ProductResponse(
        Long id,
        String name,
        int price,
        int stock,
        Long shopId,
        String shopName
) {

    // converts a DB entity into the JSON shape we want to send
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getShop().getId(),
                product.getShop().getName());   // touches the lazy shop → may run a query
    }
}
