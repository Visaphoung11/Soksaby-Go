package com.smart.service.controller;

import com.smart.service.dtoRequest.ReviewRequest;
import com.smart.service.dtoResponse.ReviewResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserEntity passenger) {
        return new ResponseEntity<>(reviewService.createReview(request, passenger), HttpStatus.CREATED);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ReviewResponse>> getByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(reviewService.getReviewsByTrip(tripId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ReviewResponse>> getByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(reviewService.getReviewsByDriver(driverId));
    }
}
