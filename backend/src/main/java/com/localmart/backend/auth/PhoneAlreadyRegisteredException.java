package com.localmart.backend.auth;

public class PhoneAlreadyRegisteredException extends RuntimeException {

    public PhoneAlreadyRegisteredException(String phone) {
        super("Phone " + phone + " is already registered");
    }
}
