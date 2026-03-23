package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.TripRequest;
import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.entity.TripEntity;
import com.smart.service.entity.TripImageEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.exception.ResourceNotFoundException;
import com.smart.service.mapper.TripMapper;
import com.smart.service.repository.CategoryRepository;
import com.smart.service.repository.TripRepository;
import com.smart.service.service.TripService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final CategoryRepository categoryRepository; // Need this for category_id
    private final TripMapper tripMapper;
    @Override
    @Transactional
    public TripResponse createTrip(TripRequest request, UserEntity driver) {
        // 1. Overlap Check
        LocalDateTime bufferEnd = request.departureTime().plusHours(4);
        if (tripRepository.countOverlappingTrips(driver.getId(), request.departureTime(), bufferEnd) > 0) {
            throw new RuntimeException("Schedule Conflict: You already have a trip at this time.");
        }

        // 2. Use Mapper to convert Request -> Entity
        TripEntity trip = tripMapper.toEntity(request);
// 3. IMPORTANT: Set the "Back-Reference" for Images
        if (trip.getImages() != null) {
            trip.getImages().forEach(image -> image.setTrip(trip)); // This is to save images
        }
        // 3. Set manual fields
        trip.setDriver(driver);
        trip.setAvailableSeats(request.totalSeats());

        // 4. Handle Category (since the mapper only sees the ID)
        if (request.categoryId() != null) {
            trip.setCategory(categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        // 5. Save and use Mapper to convert Entity -> Response
        return tripMapper.toResponse(tripRepository.save(trip));
    }
    @Override
    @Transactional
    public TripResponse updateTrip(Long id, TripRequest request, Long driverId) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getDriver().getId().equals(driverId)) {
            throw new AccessDeniedException("Unauthorized to edit this trip.");
        }

        if (trip.getAvailableSeats() < trip.getTotalSeats()) {
            throw new RuntimeException("Cannot edit trip: Seats already booked.");
        }

        // 🛡️ Use Mapper to update the existing entity
        tripMapper.updateEntityFromRequest(request, trip);

        return tripMapper.toResponse(tripRepository.save(trip));
    }


    @Override
    @Transactional
    public void deleteTrip(Long id, Long driverId) {
        TripEntity trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));

        if (!trip.getDriver().getId().equals(driverId)) {
            throw new AccessDeniedException("Unauthorized to delete this trip.");
        }

        tripRepository.delete(trip);
    }

    @Override
    @Transactional(readOnly = true) // This keeps the session open
    public List<TripResponse> getMyTrips(Long driverId) {
        // Use the new repository method with JOIN FETCH
        return tripRepository.findAllByDriverId(driverId).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        // Use the optimized query
        return tripRepository.findByIdWithDetails(id)
                .map(tripMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }
}
