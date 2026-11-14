package com.localmart.backend.order;

public class InvalidStatusChangeException extends RuntimeException {

    public InvalidStatusChangeException(String message) {
        super(message);
    }
}
