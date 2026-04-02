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
    private final CategoryRepository categoryRepository;
    private final TripMapper tripMapper;

    private void validateTripConstraints(TripRequest request) {
        if ("TUK_TUK".equalsIgnoreCase(request.transportationType())) {
            int capacity = request.vehicleCapacity() != null ? request.vehicleCapacity()
                    : (request.totalSeats() != null ? request.totalSeats() : 0);
            if (capacity > 5) {
                throw new RuntimeException("TuKTuK maximum capacity is 5 seats.");
            }
            if (!Boolean.TRUE.equals(request.isWholeVehicleBooking()) || request.wholeVehiclePrice() == null) {
                throw new RuntimeException(
                        "TukTuk trips must use Fixed Price (Whole Vehicle Booking with wholeVehiclePrice).");
            }
        } else if ("BUS".equalsIgnoreCase(request.transportationType())) {
            int capacity = request.vehicleCapacity() != null ? request.vehicleCapacity()
                    : (request.totalSeats() != null ? request.totalSeats() : 0);
            if (capacity != 25 && capacity != 40) {
                throw new RuntimeException("BUS capacity must be exactly 25 or 40 seats.");
            }
        }

        if (!"TUK_TUK".equalsIgnoreCase(request.transportationType())) {
            if (request.pricePerSeat() == null && request.wholeVehiclePrice() == null) {
                throw new RuntimeException(
                        "Trip must have either pricePerSeat or wholeVehiclePrice.");
            }
        }
    }

    @Override
    @Transactional
    public TripResponse createTrip(TripRequest request, UserEntity driver) {
        // 1. Overlap Check
        LocalDateTime bufferEnd = request.departureTime().plusHours(4);
        if (tripRepository.countOverlappingTrips(driver.getId(), request.departureTime(), bufferEnd) > 0) {
            throw new RuntimeException("Schedule Conflict: You already have a trip at this time.");
        }

        validateTripConstraints(request);

        // 2. Use Mapper to convert Request -> Entity
        TripEntity trip = tripMapper.toEntity(request);

        // 3. IMPORTANT: Set the "Back-Reference" for nested collections
        if (trip.getImages() != null) {
            trip.getImages().forEach(image -> image.setTrip(trip));
        }
        if (trip.getItinerary() != null) {
            trip.getItinerary().forEach(item -> item.setTrip(trip));
        }

        // Set manual fields
        trip.setDriver(driver);
        // Preference for vehicleCapacity as total available seats if provided
        int initialSeats = request.vehicleCapacity() != null ? request.vehicleCapacity() : request.totalSeats();
        trip.setAvailableSeats(initialSeats);
        if (trip.getTotalSeats() == null) {
            trip.setTotalSeats(initialSeats);
        }

        // 4. Handle Category
        if (request.categoryId() != null) {
            trip.setCategory(categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        // 5. Save and return Response
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

        validateTripConstraints(request);

        // Use Mapper to update the existing entity
        tripMapper.updateEntityFromRequest(request, trip);

        // Apply category update
        if (request.categoryId() != null) {
            trip.setCategory(categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
        }

        // Apply image update
        if (request.imageUrls() != null) {
            trip.getImages().clear();
            List<TripImageEntity> newImages = tripMapper.mapUrlsToEntities(request.imageUrls());
            newImages.forEach(img -> img.setTrip(trip));
            trip.getImages().addAll(newImages);
        }

        // Apply vehicle image update
        if (request.vehicleImageUrls() != null) {
            trip.getVehicleImageUrls().clear();
            trip.getVehicleImageUrls().addAll(request.vehicleImageUrls());
        }

        // Apply itinerary update
        if (request.itinerary() != null) {
            trip.getItinerary().clear();
            List<com.smart.service.entity.ItineraryItemEntity> newItinerary = tripMapper
                    .mapItineraryRequestsToEntities(request.itinerary());
            newItinerary.forEach(item -> item.setTrip(trip));
            trip.getItinerary().addAll(newItinerary);
        }

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
    @Transactional(readOnly = true)
    public List<TripResponse> getMyTrips(Long driverId) {
        return tripRepository.findAllByDriverId(driverId).stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        return tripRepository.findByIdWithDetails(id)
                .map(tripMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found"));
    }
}
