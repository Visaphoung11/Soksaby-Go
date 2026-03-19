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
    private String status;
    private String userEmail;
    private String userFullName;
    private LocalDateTime createdAt;
}
