package com.localmart.backend.common;

// for requests that pass validation but break a business rule
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
