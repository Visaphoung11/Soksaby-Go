package com.smart.service.dtoRequest;

import java.time.LocalDateTime;
import java.util.List;

public record TripRequest(
        String title,
        String description,
        String origin,
        String destination,
        Double pricePerSeat,
        Integer totalSeats,
        LocalDateTime departureTime,
        Long categoryId,
        List<String> imageUrls // URLs from your media upload endpoint
) {}