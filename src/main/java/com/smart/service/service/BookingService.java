package com.smart.service.service;

import com.smart.service.dtoRequest.BookingRequest;
import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.UserEntity;

import java.util.List;

public interface BookingService {
    // For Passengers
    BookingResponse createBooking(BookingRequest request, UserEntity passenger);
    List<BookingResponse> getMyHistory(Long passengerId);

    // For Drivers
    List<BookingResponse> getIncomingRequests(Long driverId);
    BookingResponse handleBookingResponse(Long bookingId, Long driverId, boolean accept, String reason);
    BookingResponse cancelBooking(Long bookingId, UserEntity currentUser);
}