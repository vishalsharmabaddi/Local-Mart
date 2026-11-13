package com.localmart.backend.common;

// logged in, but not allowed to touch THIS resource → 403
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }
}
