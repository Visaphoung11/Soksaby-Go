package com.smart.service.service;

import com.smart.service.dtoRequest.ChatMessageRequest;
import com.smart.service.dtoResponse.ChatMessageResponse;
import com.smart.service.dtoResponse.UserProfileResponse;
import com.smart.service.entity.UserEntity;
import java.util.List;

public interface ChatService {
    ChatMessageResponse saveMessage(ChatMessageRequest request, String senderEmail);
    List<ChatMessageResponse> getConversation(Long user1Id, Long user2Id);
    List<UserProfileResponse> getMyConversations(Long userId);
}
