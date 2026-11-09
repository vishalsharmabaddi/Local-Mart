package com.localmart.backend.product;

public record CreateProductRequest(String name, int price, int stock) {
}
