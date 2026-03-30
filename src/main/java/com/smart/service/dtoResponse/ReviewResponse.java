package com.smart.service.dtoResponse;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        Integer rating,
        String title,
        String comment,
        String travelerType,
        String visitDate,
        List<String> imageUrls,
        String passengerName,
        String passengerAvatar,
        LocalDateTime createdAt
) {}
