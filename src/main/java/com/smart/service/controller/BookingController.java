package com.smart.service.controller;

import com.smart.service.dtoRequest.BookingRequest;
import com.smart.service.dtoResponse.BookingResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.BookingService;
//  CORRECT IMPORT BELOW
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}