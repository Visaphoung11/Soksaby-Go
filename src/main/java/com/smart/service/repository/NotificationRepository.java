package com.smart.service.repository;

import com.smart.service.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
    // Get all notifications for a specific user email, newest first
    List<NotificationEntity> findByUserEmailOrderByCreatedAtDesc(String email);

    // Count how many are unread for the "Bell Icon" badge
    long countByUserEmailAndIsReadFalse(String email);
}
