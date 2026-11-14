package com.localmart.backend.order;

public enum OrderStatus {
    PLACED,             // customer ne order kiya
    ACCEPTED,           // dukaandaar ne maana
    OUT_FOR_DELIVERY,   // rider le ke nikla
    DELIVERED,          // ghar pahunch gaya
    CANCELLED;          // radd

    // STATE MACHINE: the only allowed moves. Anything not listed here is illegal.
    //   PLACED → ACCEPTED → OUT_FOR_DELIVERY → DELIVERED
    //     ↓         ↓
    //  CANCELLED  CANCELLED
    public boolean canMoveTo(OrderStatus next) {
        return switch (this) {
            case PLACED -> next == ACCEPTED || next == CANCELLED;
            case ACCEPTED -> next == OUT_FOR_DELIVERY || next == CANCELLED;
            case OUT_FOR_DELIVERY -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;   // final states: the story is over
        };
    }
}
