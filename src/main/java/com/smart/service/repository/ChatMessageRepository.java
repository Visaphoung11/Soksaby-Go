package com.smart.service.repository;

import com.smart.service.entity.ChatMessageEntity;
import com.smart.service.entity.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

    @Query("SELECT m FROM ChatMessageEntity m " +
           "JOIN FETCH m.sender " +
           "JOIN FETCH m.recipient " +
           "WHERE (m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.recipient.id = :user1Id) " +
           "ORDER BY m.timestamp ASC")
    List<ChatMessageEntity> findConversation(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT DISTINCT m.recipient FROM ChatMessageEntity m WHERE m.sender.id = :userId")
    List<UserEntity> findRecipientsBySenderId(@Param("userId") Long userId);

    @Query("SELECT DISTINCT m.sender FROM ChatMessageEntity m WHERE m.recipient.id = :userId")
    List<UserEntity> findSendersByRecipientId(@Param("userId") Long userId);
}
