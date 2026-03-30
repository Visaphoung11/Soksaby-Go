package com.smart.service.service;

import com.smart.service.dtoRequest.ReviewRequest;
import com.smart.service.dtoResponse.ReviewResponse;
import com.smart.service.entity.UserEntity;
import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(ReviewRequest request, UserEntity passenger);
    List<ReviewResponse> getReviewsByTrip(Long tripId);
    List<ReviewResponse> getReviewsByDriver(Long driverId);
}
