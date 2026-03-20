package com.smart.service.serviceimpl;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoResponse.DriverApplicationResponse;
import com.smart.service.entity.RoleEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.enums.ApplicationStatus;
import com.smart.service.enums.enums;
import com.smart.service.mapper.DriverApplicationMapper;
import com.smart.service.repository.DriverApplicationRepository;
import com.smart.service.repository.RoleRepository;
import com.smart.service.repository.UserRepository;
import com.smart.service.service.DriverApplicationService;
import com.smart.service.entity.DriverApplicationEntity;
import com.smart.service.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DriverApplicationServiceImpl implements DriverApplicationService {

        // Dependency injections
    private final NotificationService notificationService;
    private final DriverApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DriverApplicationMapper mapper;

    @Override
    public DriverApplicationResponse submitApplication(DriverApplicationRequest request, String email) {
        // 1. Get the current logged-in user
        UserEntity user = userRepository.findByEmail(email);

        // 2. Business Rule: One application at a time
        if (applicationRepository.existsByUser(user)) {
            throw new RuntimeException("You already have an active application!");
        }

        // 3. Map DTO to Entity using MapStruct
        DriverApplicationEntity entity = mapper.toEntity(request);

        // 4. Set the Owner
        entity.setUser(user);

        // 5. Save and return the Response DTO
        DriverApplicationEntity saved = applicationRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Override
    public List<DriverApplicationResponse> getAllApplications() {
        // Returns all applications so Admin can see history
        return mapper.toResponseList(applicationRepository.findAll());
    }

    @Override
    @Transactional // Ensure this is here so both DB updates and notifications stay in sync
    public void approveApplication(Long id) {
        DriverApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Application is already " + app.getStatus());
        }

        // 1. Update Application Status
        app.setStatus(ApplicationStatus.APPROVED);

        // 2. Automated Role Upgrade
        UserEntity user = app.getUser();
        RoleEntity driverRole = roleRepository.findByName(enums.DRIVER);

        if (!user.getRoles().contains(driverRole)) {
            user.getRoles().add(driverRole);
            userRepository.save(user);
        }

        applicationRepository.save(app);

        // Create and Send the Notification
        notificationService.createAndSend(
                user,
                "Application Approved!",
                "Congratulations! You are now a verified Driver on Soksabay-GO."
        );
    }
    @Override
    public void rejectApplication(Long id) {
        DriverApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(ApplicationStatus.REJECTED);
        applicationRepository.save(app);
    }
}
