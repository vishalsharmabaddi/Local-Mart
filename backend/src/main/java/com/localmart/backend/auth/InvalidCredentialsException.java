package com.localmart.backend.auth;

public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        // same message for "wrong phone" and "wrong password" → attacker can't tell which phones exist
        super("Invalid phone or password");
    }
}
