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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
        if (user.getRoles().stream().anyMatch(r -> r.getName().equals(enums.DRIVER))) {
            throw new RuntimeException("You are already a verified driver!");
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
    @Transactional
    public void rejectApplication(Long id, String reason) {
        DriverApplicationEntity app = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (app.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Application is already " + app.getStatus());
        }

        // 1. Update Status and the "Why"
        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(reason); // 👈 From your new field
        app.setReviewedAt(java.time.LocalDateTime.now()); // 👈 Track when it happened

        applicationRepository.save(app);

        // 2. Send detailed notification
        notificationService.createAndSend(
                app.getUser(),
                "Application Rejected",
                "Reason: " + reason + ". You can review your details and reapply."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DriverApplicationResponse getMyApplication(String email) {
        UserEntity user = userRepository.findByEmail(email);

        // Use the new repository method
        DriverApplicationEntity app = applicationRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No application found for this user."));

        return mapper.toResponse(app);
    }

    @Override
    @Transactional
    public DriverApplicationResponse reapply(Long id, DriverApplicationRequest request) {
        // 1. Get the one from the DB
        DriverApplicationEntity existingApp = applicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        // 2. Use the new Mapper method to copy fields from Request -> Existing Entity
        mapper.updateEntityFromRequest(request, existingApp);

        // 3. Manually reset the "Process" fields
        existingApp.setStatus(ApplicationStatus.PENDING);
        existingApp.setRejectionReason(null);
        existingApp.setCreatedAt(LocalDateTime.now());

        // 4. Save and return
        return mapper.toResponse(applicationRepository.save(existingApp));
    }
}
