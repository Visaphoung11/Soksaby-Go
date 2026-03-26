package com.smart.service.controller;

import com.smart.service.dtoRequest.BookingRequest;
import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.BookingService;
//  CORRECT IMPORT BELOW
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import jakarta.validation.Valid; // Optional: for validation

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> book(
            @RequestBody BookingRequest request, // Spring will now see the JSON!
            @AuthenticationPrincipal UserEntity user
    ) {
        return ResponseEntity.ok(bookingService.createBooking(request, user));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyHistory(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(bookingService.getMyHistory(user.getId()));
    }
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse> cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, user));
    }
}