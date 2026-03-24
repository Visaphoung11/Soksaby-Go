package com.smart.service.controller;

import com.smart.service.dtoResponse.TripResponse;
import com.smart.service.service.PublicTripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/public/trips")
@RequiredArgsConstructor
public class PublicTripController {

    private final PublicTripService publicTripService;


    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> search(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(publicTripService.searchTrips(origin, destination, date));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(publicTripService.getTripDetails(id));
    }
}
