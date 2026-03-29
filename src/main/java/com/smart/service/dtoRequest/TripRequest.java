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
        List<String> imageUrls, // URLs from your media upload endpoint
        
        String transportationType,
        Integer vehicleCapacity,
        Boolean isWholeVehicleBooking,
        Double wholeVehiclePrice,
        List<String> vehicleImageUrls,
        
        String scheduleDescription,
        
        Boolean hasTourGuide,
        String tourGuideDescription,
        String tourGuideImageUrl,
        
        Boolean mealsIncluded,
        String diningDetails,
        
        String availabilitySchedule,
        
        List<ItineraryItemRequest> itinerary
) {}