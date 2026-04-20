package com.smart.service.serviceimpl;

import com.smart.service.dtoResponse.AdminStatsSummaryResponse;
import com.smart.service.dtoResponse.UserResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.repository.UserRepository;
import com.smart.service.repository.DriverApplicationRepository; 
import com.smart.service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final DriverApplicationRepository driverRepo;

    @Override
    public AdminStatsSummaryResponse getSummaryStats() {
        return AdminStatsSummaryResponse.builder()
                .totalUsers(userRepository.count())
                .totalDrivers(userRepository.countDrivers())
                .pendingDriverApplications(driverRepo.countByStatus("PENDING"))
                .activeNow(userRepository.countByStatus("ACTIVE")) // Or use isOnline
                .build();
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToResponse);
    }

    @Override
    public void updateUserStatus(Long userId, String status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }

    // Helper method to convert Entity to DTO
    private UserResponse convertToResponse(UserEntity user) {
    return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName() != null ? user.getFullName() : "Unknown")
            .email(user.getEmail())
            .status(user.getStatus() != null ? user.getStatus() : "INACTIVE") // Prevent null crash
            .isOnline(user.getIsOnline() != null ? user.getIsOnline() : false)
            .roles(user.getRoles() != null ? 
                    user.getRoles().stream()
                        .map(role -> role.getName().name()) 
                        .collect(Collectors.toSet()) : new HashSet<>())
            .build();
}
}