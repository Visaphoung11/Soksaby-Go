package com.smart.service.entity;

import com.smart.service.enums.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "driver_applications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverApplicationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "National ID is required")
    @Column(nullable = false, unique = true)
    private String nationalId;
    @NotBlank(message = "License number is required")
    @Column(nullable = false, unique = true)
    private String licenseNumber;
    @NotBlank(message = "ID card image URL is required")
    @Column(nullable = false)
    private String vehicleType;
    // Links from Cloudinary
    private String idCardImageUrl;
    private String idCardPublicId;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }
}