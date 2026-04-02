package com.smart.service.controller;

import com.smart.service.dtoResponse.ChatMessageResponse;
import com.smart.service.dtoResponse.UserProfileResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<List<ChatMessageResponse>> getHistory(
            @PathVariable Long otherUserId,
            @AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(chatService.getConversation(currentUser.getId(), otherUserId));
    }

    @GetMapping("/conversations")
    public ResponseEntity<List<UserProfileResponse>> getMyConversations(@AuthenticationPrincipal UserEntity currentUser) {
        return ResponseEntity.ok(chatService.getMyConversations(currentUser.getId()));
    }
}
