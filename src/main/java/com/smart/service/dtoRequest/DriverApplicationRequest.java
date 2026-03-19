package com.smart.service.dtoRequest;

import lombok.Data;

@Data
public class DriverApplicationRequest {
    private String nationalId;
    private String licenseNumber;
    private String vehicleType;
    private String idCardImageUrl; // from Cloudinary response
    private String idCardPublicId; // from Cloudinary response
}
