package com.smart.service.controller;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoRequest.RejectionRequest;
import com.smart.service.dtoResponse.ApiResponse;
import com.smart.service.dtoResponse.DriverApplicationResponse;
import com.smart.service.service.DriverApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/driver-applications")
@RequiredArgsConstructor
public class DriverApplicationController {
    private final DriverApplicationService service;

    /**
     * 1. USER ACTION: Submit a new application.
     * Accessible by any authenticated user.
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DriverApplicationResponse>> apply(
            @RequestBody DriverApplicationRequest request,
            Principal principal) {

        DriverApplicationResponse data = service.submitApplication(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DriverApplicationResponse>builder()
                        .message("Application submitted successfully. Please wait for admin review.")
                        .success(true)
                        .data(data)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    /**
     * 2. ADMIN ACTION: Get all applications (History).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<DriverApplicationResponse>>> getAll() {
        List<DriverApplicationResponse> data = service.getAllApplications();

        return ResponseEntity.ok(
                ApiResponse.<List<DriverApplicationResponse>>builder()
                        .message("Fetched all driver applications.")
                        .success(true)
                        .data(data)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    /**
     * 3. ADMIN ACTION: Approve an application.
     * Triggers the ROLE_DRIVER upgrade logic.
     */
    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approve(@PathVariable Long id) {
        service.approveApplication(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Application approved! User is now a registered Driver.")
                        .success(true)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }

    /**
     * 4. ADMIN ACTION: Reject an application.
     */
    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> reject(
            @PathVariable Long id,
            @RequestBody RejectionRequest rejectionRequest) { // Get reason from body

        service.rejectApplication(id, rejectionRequest.getReason());

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .message("Application has been rejected. Reason: " + rejectionRequest.getReason())
                        .success(true)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
    /**
     * 5. NEW: User re-submits a rejected application.
     */
    @PutMapping("/{id}/reapply")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DriverApplicationResponse>> reapply(
            @PathVariable Long id,
            @RequestBody DriverApplicationRequest request) {

        DriverApplicationResponse data = service.reapply(id, request);

        return ResponseEntity.ok(
                ApiResponse.<DriverApplicationResponse>builder()
                        .message("Application re-submitted successfully. Reason cleared.")
                        .success(true)
                        .data(data)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse<DriverApplicationResponse>> getMyApplication(Principal principal) {
        DriverApplicationResponse data = service.getMyApplication(principal.getName());

        return ResponseEntity.ok(
                ApiResponse.<DriverApplicationResponse>builder()
                        .message("User application status retrieved.")
                        .success(true)
                        .data(data)
                        .timestamp(LocalDateTime.now().toString())
                        .build()
        );
    }
}
