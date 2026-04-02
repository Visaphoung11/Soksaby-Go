package com.smart.service.dtoResponse;

import com.smart.service.enums.ChatMessageType;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private String content;
    private ChatMessageType type;
    private String mediaUrl;
    private String timestamp;
    private boolean isRead;
}
