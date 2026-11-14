package com.localmart.backend.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// ONE test method, MANY inputs: each CSV row runs as its own test
class OrderStatusTest {

    @ParameterizedTest(name = "{0} → {1} is allowed")
    @CsvSource({
            "PLACED,           ACCEPTED",
            "PLACED,           CANCELLED",
            "ACCEPTED,         OUT_FOR_DELIVERY",
            "ACCEPTED,         CANCELLED",
            "OUT_FOR_DELIVERY, DELIVERED"
    })
    void allowedMoves(OrderStatus from, OrderStatus to) {
        assertTrue(from.canMoveTo(to));
    }

    @ParameterizedTest(name = "{0} → {1} is blocked")
    @CsvSource({
            "PLACED,           OUT_FOR_DELIVERY",   // can't skip accepting
            "PLACED,           DELIVERED",
            "ACCEPTED,         PLACED",             // no going back
            "OUT_FOR_DELIVERY, CANCELLED",          // rider already left
            "DELIVERED,        PLACED",
            "DELIVERED,        CANCELLED",
            "CANCELLED,        PLACED",
            "CANCELLED,        DELIVERED"
    })
    void blockedMoves(OrderStatus from, OrderStatus to) {
        assertFalse(from.canMoveTo(to));
    }
}
