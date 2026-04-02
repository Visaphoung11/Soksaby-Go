package com.smart.service.controller;

import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/driver/bookings")
@RequiredArgsConstructor
public class DriverBookingController {
    private final BookingService bookingService;

    @GetMapping("/requests")
    public ResponseEntity<List<BookingResponse>> getRequests(@AuthenticationPrincipal UserEntity driver) {
        return ResponseEntity.ok(bookingService.getIncomingRequests(driver.getId()));
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<BookingResponse> respond(
            @PathVariable Long id, 
            @RequestParam boolean accept, 
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal UserEntity driver) {
        return ResponseEntity.ok(bookingService.handleBookingResponse(id, driver.getId(), accept, reason));
    }
}