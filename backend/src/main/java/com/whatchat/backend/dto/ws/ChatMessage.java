package com.whatchat.backend.dto.ws;

import lombok.Data;

import java.util.UUID;

@Data
public class ChatMessage {
    private UUID senderId;
    private UUID receiverId;
    private String content; // RSA/AES ile şifreli metin
}