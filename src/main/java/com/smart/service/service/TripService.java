package com.smart.service.service;

import com.smart.service.dtoRequest.TripRequest;
import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.entity.UserEntity;

import java.util.List;

public interface TripService {
    TripResponse createTrip(TripRequest request, UserEntity driver);
    TripResponse updateTrip(Long id, TripRequest request, Long driverId);
    void deleteTrip(Long id, Long driverId);
    List<TripResponse> getMyTrips(Long driverId);
    TripResponse getTripById(Long id);
}