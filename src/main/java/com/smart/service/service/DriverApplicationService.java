package com.smart.service.service;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoResponse.DriverApplicationResponse;

import java.util.List;

public interface DriverApplicationService {

    DriverApplicationResponse submitApplication(DriverApplicationRequest request, String email);
    List<DriverApplicationResponse> getAllApplications();
    void approveApplication(Long id);
    // Updated: Now accepts a reason
    void rejectApplication(Long id, String reason);
    DriverApplicationResponse getMyApplication(String email);
    // New: Allow users to try again
    DriverApplicationResponse reapply(Long id, DriverApplicationRequest request);
}
