package com.smart.service.dtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public record TripResponse(
        Long id,
        String title,
        String description,
        String origin,
        String destination,
        Double pricePerSeat,
        Integer totalSeats,
        Integer availableSeats,
        LocalDateTime departureTime,
        String status,
        List<String> images,
        String driverName,
        String categoryName
) {}