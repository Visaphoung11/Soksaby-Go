package com.smart.service.serviceimpl;

import com.smart.service.dtoResponse.NotificationResponse;
import com.smart.service.entity.NotificationEntity;
import com.smart.service.entity.UserEntity;
import com.smart.service.repository.NotificationRepository;
import com.smart.service.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {



// Injections
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void createAndSend(UserEntity user, String title, String message) {
        // 1. Save to Database
        NotificationEntity notification = NotificationEntity.builder()
                .user(user)
                .title(title)
                .message(message)
                .build();
        notificationRepository.save(notification);

        // 2. Send via WebSocket (Real-time)
        // Topic: /topic/notifications/user@example.com
        String destination = "/topic/notifications/" + user.getEmail();

        NotificationResponse response = NotificationResponse.builder()
                .id(notification.getId())
                .title(title)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now().toString())
                .build();

        messagingTemplate.convertAndSend(destination, response);
    }

    @Override
    public List<NotificationResponse> getMyNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(n -> NotificationResponse.builder()
                        .id(n.getId())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .isRead(n.isRead())
                        .createdAt(n.getCreatedAt().toString())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        NotificationEntity n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setRead(true);
        notificationRepository.save(n);
    }
}
