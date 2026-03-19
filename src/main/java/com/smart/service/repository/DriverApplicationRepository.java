package com.smart.service.repository;

import com.smart.service.entity.DriverApplicationEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverApplicationRepository extends JpaRepository<DriverApplicationEntity, Long> {
    // Check if user already applied to prevent duplicates
    boolean existsByUser(UserEntity user);

    // For Admin to filter PENDING applications
    List<DriverApplicationEntity> findByStatus(ApplicationStatus status);
}