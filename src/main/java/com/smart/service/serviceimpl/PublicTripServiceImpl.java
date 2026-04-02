package com.smart.service.serviceimpl;

import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.entity.TripEntity;
import com.smart.service.mapper.TripMapper;
import com.smart.service.repository.TripRepository;
import com.smart.service.service.PublicTripService;
import com.smart.service.service.TripSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicTripServiceImpl implements PublicTripService {

    private final TripRepository tripRepository;
    private final TripMapper tripMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> searchTrips(String origin, String destination, LocalDate date) {
        // 1. Build the dynamic WHERE clause
        Specification<TripEntity> spec = TripSpecification.hasFilters(origin, destination, date);

        // 2. ✅ FIX: Remove the (Sort) cast.
        // Just pass 'spec' directly. Spring will know which method to use.
        return tripRepository.findAll(spec).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripDetails(Long id) {
        // Using the Detail query we wrote earlier to avoid Lazy Loading 500 errors
        return tripRepository.findByIdWithDetails(id)
                .map(tripMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Trip not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripResponse> getAllTrips() {
        Specification<TripEntity> spec = TripSpecification.hasFilters(null, null, null);
        return tripRepository.findAll(spec).stream()
                .map(tripMapper::toResponse)
                .toList();
    }
}
