package com.whatchat.backend.dto.chat;

import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatSummary {
    private String publicId;
    @Lob
    private byte[] avatar; // ya da byte[] / Base64 string, ihtiyacına göre uyarlayabilirsin
    private LocalDateTime lastMessageSentAt;
    private String lastMessageContent;
}