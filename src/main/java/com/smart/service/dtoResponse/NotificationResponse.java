package com.smart.service.dtoResponse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private boolean isRead;
    private String createdAt; // Formatted as String for Frontend
}