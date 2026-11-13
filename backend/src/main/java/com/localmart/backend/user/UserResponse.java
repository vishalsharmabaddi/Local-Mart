package com.localmart.backend.user;

// what we send back about a user — NO password field, ever
public record UserResponse(Long id, String name, String phone, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getPhone(), user.getRole());
    }
}
