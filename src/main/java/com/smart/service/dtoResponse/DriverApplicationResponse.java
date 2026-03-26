package com.smart.service.dtoResponse;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DriverApplicationResponse {
    private Long id;
    private String nationalId;
    private String licenseNumber;
    private String vehicleType;
    private String idCardImageUrl;
    private String status; // PENDING, APPROVED, REJECTED

    // --- Rejection Info ---
    private String rejectionReason; // Null if PENDING or APPROVED
    private LocalDateTime reviewedAt; // When the Admin took action

    // --- User Info ---
    private Long userId; // Useful for navigation on the frontend
    private String userEmail;
    private String userFullName;

    private LocalDateTime createdAt;
}