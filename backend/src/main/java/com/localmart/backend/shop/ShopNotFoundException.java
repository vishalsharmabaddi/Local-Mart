package com.localmart.backend.shop;

public class ShopNotFoundException extends RuntimeException {

    public ShopNotFoundException(Long id) {
        super("Shop not found with id " + id);
    }
}
