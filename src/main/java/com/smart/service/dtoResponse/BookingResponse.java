package com.smart.service.dtoResponse;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Integer seatsBooked,
        Double totalPrice,
        String status,
        LocalDateTime createdAt,
        TripSummaryResponse trip, // Nested trip info
        String passengerName,
        String passengerPhone,
        String rejectionReason
) {}

