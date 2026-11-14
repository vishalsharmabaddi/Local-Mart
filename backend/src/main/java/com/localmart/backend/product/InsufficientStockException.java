package com.localmart.backend.product;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int available, int requested) {
        super("Not enough stock for " + productName + ": available " + available + ", requested " + requested);
    }
}
