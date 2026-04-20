package com.smart.service.repository;

import com.smart.service.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    UserEntity findByEmail(String email);
    UserEntity findById(long id);
    boolean existsByEmail(String email);

    // --- NEW ADMIN METHODS ---

    /** * Counts users who have the 'DRIVER' role.
     * We join with 'roles' and check the Enum name.
     */
    @Query("SELECT COUNT(u) FROM UserEntity u JOIN u.roles r WHERE r.name = 'DRIVER'")
    long countDrivers();

    /**
     * Counts users based on their 'status' field (e.g., "ACTIVE", "INACTIVE")
     */
    long countByStatus(String status);

    /**
     * Counts currently online users
     */
    long countByIsOnlineTrue();

    // --- EXISTING METHODS ---

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.lastActiveAt = :lastActiveAt WHERE u.email = :email")
    void updateLastActiveByEmail(String email, LocalDateTime lastActiveAt);

    @Modifying
    @Transactional
    @Query("UPDATE UserEntity u SET u.isOnline = :isOnline, u.lastActiveAt = :lastActiveAt WHERE u.id = :userId")
    void updateOnlineStatus(Long userId, boolean isOnline, LocalDateTime lastActiveAt);
}