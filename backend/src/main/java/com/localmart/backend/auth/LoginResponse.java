package com.localmart.backend.auth;

import com.localmart.backend.user.UserResponse;

public record LoginResponse(String token, String tokenType, long expiresInSeconds, UserResponse user) {
}
