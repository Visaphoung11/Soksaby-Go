package com.smart.service.dtoRequest;

import java.util.List;

public record ReviewRequest(
        Long tripId,
        Integer rating,
        String title,
        String comment,
        String travelerType,
        String visitDate,
        List<String> imageUrls
) {}
