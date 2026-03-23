package com.smart.service.controller;

import com.smart.service.dtoRequest.TripRequest;
import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;


    /**
     * Create Trip
     * Only accessible by users with the 'DRIVER' role.
     */
    @PostMapping
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> createTrip(
            @RequestBody TripRequest request,
            @AuthenticationPrincipal UserEntity currentUser) {

        TripResponse response = tripService.createTrip(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * Get My Trips
     * Returns a list of all trips created by the logged-in driver.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<List<TripResponse>> getMyTrips(
            @AuthenticationPrincipal UserEntity currentUser) {

        List<TripResponse> responses = tripService.getMyTrips(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get Trip Details
     * Fetches a specific trip by its ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> getTripById(
            @PathVariable Long id) {

        TripResponse response = tripService.getTripById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update Trip Information
     * Updates trip details (restricted to the owner/driver).
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<TripResponse> updateTrip(
            @PathVariable Long id,
            @RequestBody TripRequest request,
            @AuthenticationPrincipal UserEntity currentUser) {

        TripResponse response = tripService.updateTrip(id, request, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    /**
     * Delete Trip
     * Performs a soft delete (restricted to the owner/driver).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Void> deleteTrip(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity currentUser) {

        tripService.deleteTrip(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
