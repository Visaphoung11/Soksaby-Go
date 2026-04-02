package com.smart.service.dtoRequest;

import com.smart.service.enums.ChatMessageType;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
    private Long recipientId;
    private String content;
    private ChatMessageType type;
    private String mediaUrl;
}
