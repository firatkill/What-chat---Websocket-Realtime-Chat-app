package com.whatchat.backend.controller;

import com.whatchat.backend.dto.chat.MessageRequest;
import com.whatchat.backend.dto.ws.ChatMessage;
import com.whatchat.backend.dto.ws.TypingNotification;
import com.whatchat.backend.dto.ws.DeliveryStatus;
import com.whatchat.backend.entity.Message;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.payload.response.ApiResponse;
import com.whatchat.backend.repository.MessageRepository;
import com.whatchat.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

    @MessageMapping("/sendMessage")
    public void sendMessage(@AuthenticationPrincipal User user, MessageRequest messageRequest) {

       messageService.sendMessage(user,messageRequest);
    }

    @MessageMapping("/typing/{receiverId}")
    public ResponseEntity<ApiResponse<Void>> typing(@org.springframework.messaging.handler.annotation.DestinationVariable UUID receiverId,
                                                    TypingNotification notification) {
        messagingTemplate.convertAndSend(
                "/topic/typing/" + notification.getSenderId(),
                notification
        );
        ApiResponse<Void> response= new ApiResponse<>(true,"message sent.",null,null);
        return ResponseEntity.ok(response);
    }

    @MessageMapping("/message.read")
    public void markAsRead(DeliveryStatus status) {
        messageRepository.findById(status.getMessageId()).ifPresent(msg -> {
            msg.setReadAt(Instant.now());
            messageRepository.save(msg);
        });
    }

    @MessageMapping("/message.delivered")
    public void markAsDelivered(DeliveryStatus status) {
        messageRepository.findById(status.getMessageId()).ifPresent(msg -> {
            msg.setDeliveredAt(Instant.now());
            messageRepository.save(msg);
        });
    }
}