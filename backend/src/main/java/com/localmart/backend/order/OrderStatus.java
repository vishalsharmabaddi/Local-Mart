package com.localmart.backend.order;

public enum OrderStatus {
    PLACED,             // customer ne order kiya
    ACCEPTED,           // dukaandaar ne maana
    OUT_FOR_DELIVERY,   // rider le ke nikla
    DELIVERED,          // ghar pahunch gaya
    CANCELLED           // radd
}
