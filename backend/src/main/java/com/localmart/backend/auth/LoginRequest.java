package com.localmart.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Phone is required")
        String phone,

        @NotBlank(message = "Password is required")
        String password
) {
}
