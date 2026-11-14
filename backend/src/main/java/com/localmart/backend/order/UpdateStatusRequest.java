package com.localmart.backend.order;

import jakarta.validation.constraints.NotNull;

public record UpdateStatusRequest(

        @NotNull(message = "Status is required")
        OrderStatus status
) {
}
