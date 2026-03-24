package com.smart.service.enums;

public enum BookingStatus {
    PENDING,    // Passenger requested, waiting for Driver
    CONFIRMED,  // Driver accepted
    REJECTED,   // Driver declined
    CANCELLED   // Passenger backed out
}
