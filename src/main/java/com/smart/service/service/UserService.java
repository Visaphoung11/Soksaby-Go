package com.smart.service.service;

import com.smart.service.dtoRequest.UserProfileUpdateRequest;
import com.smart.service.dtoResponse.UserProfileResponse;

public interface UserService {
    UserProfileResponse getUserProfile(String email);
    UserProfileResponse updateUserProfile(String email, UserProfileUpdateRequest request);
}
