package com.smart.service.dtoResponse;

import java.time.LocalDateTime;

public record TripSummaryResponse(
        Long id,
        String title,
        String destination,
        LocalDateTime departureTime,
        String driverName,
        Long driverId
) {}
