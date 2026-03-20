package com.smart.service.controller;

import com.smart.service.dtoResponse.ApiResponse;
import com.smart.service.dtoResponse.NotificationResponse;
import com.smart.service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyAlerts(Principal principal) {
        List<NotificationResponse> data = notificationService.getMyNotifications(principal.getName());
        return ResponseEntity.ok(ApiResponse.<List<NotificationResponse>>builder()
                .message("Notifications retrieved.")
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .message("Marked as read.")
                .success(true)
                .timestamp(LocalDateTime.now().toString())
                .build());
    }


}
