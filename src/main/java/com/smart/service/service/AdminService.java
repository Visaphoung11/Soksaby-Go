package com.smart.service.service;

import com.smart.service.dtoResponse.AdminStatsSummaryResponse;
import com.smart.service.dtoResponse.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    AdminStatsSummaryResponse getSummaryStats(); // Add this back!
    Page<UserResponse> getAllUsers(Pageable pageable);
    void updateUserStatus(Long userId, String status); // Ensure this is here
}