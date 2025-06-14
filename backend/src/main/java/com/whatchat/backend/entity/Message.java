package com.whatchat.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID senderId;
    private UUID receiverId;


    private String content; // Şifreli metin

    @CreationTimestamp
    private Instant sentAt;

    private Instant deliveredAt;
    private Instant readAt;

    private boolean isDeletedBySender = false;
    private boolean isDeletedByReceiver = false;
}