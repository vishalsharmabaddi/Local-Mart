package com.localmart.backend.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

// one cart line from the app. Only id + quantity: price is NEVER taken from the client.
public record OrderItemRequest(

        @NotNull(message = "Product id is required")
        Long productId,

        @Min(value = 1, message = "Quantity must be at least 1")
        @Max(value = 50, message = "Quantity cannot be more than 50")
        int quantity
) {
}
