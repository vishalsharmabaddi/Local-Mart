package com.localmart.backend.shop;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateShopRequest(

        @NotBlank(message = "Shop name is required")
        String name,

        @NotBlank(message = "Town is required")
        String town,

        @NotBlank(message = "Phone is required")
        @Pattern(regexp = "\\d{10}", message = "Phone must be exactly 10 digits")
        String phone
) {
}
