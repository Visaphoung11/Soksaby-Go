package com.smart.service.service;

import com.smart.service.dtoResponse.NotificationResponse;
import com.smart.service.entity.UserEntity;

import java.util.List;

public interface NotificationService {
    void createAndSend(UserEntity user, String title, String message);
    List<NotificationResponse> getMyNotifications(String email);
    void markAsRead(Long id);
}