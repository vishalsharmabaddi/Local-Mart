package com.localmart.backend.order;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(

        @NotNull(message = "Shop id is required")
        Long shopId,

        @NotEmpty(message = "Order must have at least one item")
        List<@Valid OrderItemRequest> items   // @Valid inside → each line is validated too
) {
}
