package com.smart.service.dtoResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Necessary for JSON deserialization
@AllArgsConstructor // Necessary for the Builder pattern
public class AdminStatsSummaryResponse {
    private long totalUsers;
    private long totalDrivers;
    private long pendingDriverApplications;
    private long activeNow; // This can be based on isOnline or a more complex logic
    private long activeTrips;
}