package com.whatchat.backend.dto.ws;

import lombok.Data;

import java.util.UUID;

@Data
public class TypingNotification {
    private UUID senderId;
    private UUID receiverId;
}