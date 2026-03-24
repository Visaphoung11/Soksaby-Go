package com.smart.service.dtoRequest;

public record BookingRequest(
        Long tripId,
        Integer seatsBooked
) {}