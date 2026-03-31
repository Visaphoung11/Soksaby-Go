package com.smart.service.service;
import com.smart.service.dtoResponse.TripResponse;
import java.time.LocalDate;
import java.util.List;

public interface PublicTripService {
    List<TripResponse> searchTrips(String origin, String destination, LocalDate date);
    List<TripResponse> getAllTrips();
    TripResponse getTripDetails(Long id);
}
