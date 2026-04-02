package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.ReviewRequest;
import com.smart.service.dtoResponse.ReviewResponse;
import com.smart.service.entity.ReviewEntity;
import com.smart.service.entity.TripEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.BookingStatus;
import com.smart.service.repository.BookingRepository;
import com.smart.service.repository.ReviewRepository;
import com.smart.service.repository.TripRepository;
import com.smart.service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, UserEntity passenger) {
        // 1. Validate Rating 1-5
        if (request.rating() < 1 || request.rating() > 5) {
            throw new RuntimeException("Rating must be between 1 and 5.");
        }

        // 2. Fetch Trip
        TripEntity trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // 3. Verify Passenger actually traveled (CONFIRMED booking)
        boolean hasTraveled = bookingRepository.existsByPassengerIdAndTripIdAndStatus(
                passenger.getId(), trip.getId(), BookingStatus.CONFIRMED);

        if (!hasTraveled) {
            throw new RuntimeException("You can only review trips you have completed/confirmed.");
        }

        // 3.5 Verify Passenger hasn't already reviewed this trip
        boolean hasReviewed = reviewRepository.existsByPassengerIdAndTripId(passenger.getId(), trip.getId());
        if (hasReviewed) {
            throw new RuntimeException("You have already reviewed this trip.");
        }

        // 4. Create Review
        ReviewEntity review = ReviewEntity.builder()
                .rating(request.rating())
                .title(request.title())
                .comment(request.comment())
                .travelerType(request.travelerType())
                .visitDate(request.visitDate())
                .imageUrls(request.imageUrls())
                .passenger(passenger)
                .driver(trip.getDriver())
                .trip(trip)
                .createdAt(LocalDateTime.now())
                .build();

        ReviewEntity saved = reviewRepository.save(review);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByTrip(Long tripId) {
        return reviewRepository.findAllByTripIdOrderByCreatedAtDesc(tripId)
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByDriver(Long driverId) {
        return reviewRepository.findAllByDriverIdOrderByCreatedAtDesc(driverId)
                .stream().map(this::mapToResponse).toList();
    }

    private ReviewResponse mapToResponse(ReviewEntity entity) {
        return new ReviewResponse(
                entity.getId(),
                entity.getRating(),
                entity.getTitle(),
                entity.getComment(),
                entity.getTravelerType(),
                entity.getVisitDate(),
                entity.getImageUrls(),
                entity.getPassenger().getFullName(),
                entity.getPassenger().getProfileImage(),
                entity.getCreatedAt());
    }
}
