package com.localmart.backend.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record CreateProductRequest(

        @NotBlank(message = "Name is required")
        String name,

        @Positive(message = "Price must be greater than 0")
        int price,

        @PositiveOrZero(message = "Stock cannot be negative")
        int stock,

        @NotNull(message = "Shop id is required")
        Long shopId
) {
}
