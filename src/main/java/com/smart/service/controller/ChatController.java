package com.smart.service.controller;

import com.smart.service.dtoRequest.ChatMessageRequest;
import com.smart.service.dtoResponse.ChatMessageResponse;
import com.smart.service.entity.UserEntity;
import com.smart.service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

@MessageMapping("/chat.send")
public void sendMessage(@Payload ChatMessageRequest request, Principal principal) {
  if (principal == null) {
    System.err.println("CRITICAL: WS principal is NULL!");
    return;
  }

  String senderEmail = principal.getName(); // this must be email
  ChatMessageResponse response = chatService.saveMessage(request, senderEmail);

  messagingTemplate.convertAndSendToUser(response.getRecipientEmail(), "/queue/messages", response);
  messagingTemplate.convertAndSendToUser(senderEmail, "/queue/messages", response);
}
}
