package com.whatchat.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user1Id", referencedColumnName = "id")
    private User user1;


    @OneToOne(mappedBy = "", fetch = FetchType.EAGER)
    @JoinColumn(name = "user2Id", referencedColumnName = "id")
    private User user2;

    // opsiyonel: en son mesajın tarihi (sıralama için)
    @OneToOne
    private Message lastMessage;

    // getters/setters
}