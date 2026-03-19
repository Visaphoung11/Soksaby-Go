package com.smart.service.service;

import com.smart.service.dtoRequest.DriverApplicationRequest;
import com.smart.service.dtoResponse.DriverApplicationResponse;

import java.util.List;

public interface DriverApplicationService {

    DriverApplicationResponse submitApplication(DriverApplicationRequest request, String email);
    List<DriverApplicationResponse> getAllApplications();
    void approveApplication(Long id);
    void rejectApplication(Long id);
}
