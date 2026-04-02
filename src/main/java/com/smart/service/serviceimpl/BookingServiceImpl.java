package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.BookingRequest;
import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.BookingEntity;
import com.smart.service.entity.TripEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.BookingStatus;
import com.smart.service.exception.ResourceNotFoundException;
import com.smart.service.mapper.BookingMapper;
import com.smart.service.repository.BookingRepository;
import com.smart.service.repository.TripRepository;
import com.smart.service.service.BookingService;
import com.smart.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final BookingMapper bookingMapper;
    private final NotificationService notificationService;

    @Override
    public BookingResponse createBooking(BookingRequest request, UserEntity passenger) {
        TripEntity trip = tripRepository.findById(request.tripId())
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        // Logic for the own driver can not book their own trips
        if (trip.getDriver().getId().equals(passenger.getId())) {
            throw new RuntimeException("Drivers cannot book their own trips!");
        }

        if (trip.getAvailableSeats() < request.seatsBooked()) {
            throw new RuntimeException("Insufficient seats available!");
        }

        // Prevent passenger from booking the same trip twice if they already have an active booking
        boolean alreadyBooked = bookingRepository.existsByPassengerIdAndTripIdAndStatusIn(
                passenger.getId(), 
                trip.getId(), 
                List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED)
        );
        if (alreadyBooked) {
            throw new RuntimeException("You already booked this trip.");
        }

        BookingEntity booking = BookingEntity.builder()
                .passenger(passenger)
                .trip(trip)
                .seatsBooked(request.seatsBooked())
                .totalPrice(trip.getPricePerSeat() * request.seatsBooked()) // Price snapshot
                .status(BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // Deduct seats immediately so no one else can book them while pending
        trip.setAvailableSeats(trip.getAvailableSeats() - request.seatsBooked());
        tripRepository.save(trip);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyHistory(Long passengerId) {
        return bookingRepository.findAllByPassengerIdOrderByCreatedAtDesc(passengerId)
                .stream().map(bookingMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getIncomingRequests(Long driverId) {
        return bookingRepository.findPendingRequestsByDriverId(driverId)
                .stream().map(bookingMapper::toResponse).toList();
    }

    @Override
    public BookingResponse handleBookingResponse(Long bookingId, Long driverId, boolean accept, String reason) {
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Security check: only the trip owner can respond
        if (!booking.getTrip().getDriver().getId().equals(driverId)) {
            throw new RuntimeException("Unauthorized to handle this booking");
        }

        UserEntity passenger = booking.getPassenger();
        TripEntity trip = booking.getTrip();

        if (accept) {
            booking.setStatus(BookingStatus.CONFIRMED);

            // First Booking Wins Logic: (Overlapping trips from same driver)
            LocalDateTime start = trip.getDepartureTime().minusHours(4);
            LocalDateTime end = trip.getDepartureTime().plusHours(4);
            List<TripEntity> overlappingTrips = tripRepository.findOverlappingTripsForCancellation(
                    trip.getDriver().getId(), start, end, trip.getId());

            for (TripEntity overlap : overlappingTrips) {
                overlap.setStatus(com.smart.service.enums.TripStatus.CANCELLED);
            }
            if (!overlappingTrips.isEmpty()) {
                tripRepository.saveAll(overlappingTrips);
            }

            // Notify Passenger about Acceptance
            notificationService.createAndSend(
                    passenger,
                    "Booking Confirmed!",
                    "Your booking for trip '" + trip.getTitle() + "' has been accepted by the driver."
            );
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            booking.setRejectionReason(reason);

            // Restore the seats since the driver rejected the booking
            trip.setAvailableSeats(trip.getAvailableSeats() + booking.getSeatsBooked());
            tripRepository.save(trip);

            // Notify Passenger about Rejection
            String rejectionMsg = "Your booking for trip '" + trip.getTitle() + "' was rejected by the driver.";
            if (reason != null && !reason.isBlank()) {
                rejectionMsg += " Reason: " + reason;
            }
            notificationService.createAndSend(passenger, "Booking Rejected", rejectionMsg);
        }

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional // it ensures Trip and Booking update together
    public BookingResponse cancelBooking(Long bookingId, UserEntity currentUser) {
        // 1. Fetch Booking
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // 2. Ownership Check (Security)
        if (!booking.getPassenger().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized: You can only cancel your own bookings");
        }

        // 3. Status Validation
        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new RuntimeException("Booking is already in a terminal state: " + booking.getStatus());
        }

        // 4. SEAT RECOVERY LOGIC
        // Since seats are reserved immediately at PENDING, we must give them back
        // if the booking is currently PENDING or CONFIRMED.
        if (booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.PENDING) {
            TripEntity trip = booking.getTrip();
            int restoredSeats = trip.getAvailableSeats() + booking.getSeatsBooked();
            trip.setAvailableSeats(restoredSeats);
            tripRepository.save(trip);
        }

        // 5. Finalize Cancellation
        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }
}
