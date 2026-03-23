package com.smart.service.enums;

public enum TripStatus {
    AVAILABLE,   // Trip is open for booking
    ONGOING,     // Driver has started the trip
    COMPLETED,   // Trip is finished
    CANCELLED    // Driver or System cancelled the trip
}
