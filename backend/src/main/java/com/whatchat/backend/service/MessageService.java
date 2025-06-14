package com.whatchat.backend.service;

import com.whatchat.backend.dto.chat.MessageRequest;
import com.whatchat.backend.dto.ws.ChatMessage;
import com.whatchat.backend.entity.Conversation;
import com.whatchat.backend.entity.Message;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.repository.ConversationRepository;
import com.whatchat.backend.repository.MessageRepository;
import com.whatchat.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationRepository conversationRepository;

    public List<Message> getMessages(User currentUser, String targetPublicId) {
        User target = userRepository.findByPublicId(targetPublicId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        UUID currentId = currentUser.getId();
        UUID targetId = target.getId();

        return messageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(
                        currentId, targetId,
                        currentId, targetId
                ).stream()
                .filter(m -> !(m.isDeletedBySender() && m.getSenderId().equals(currentId)))
                .filter(m -> !(m.isDeletedByReceiver() && m.getReceiverId().equals(currentId)))
                .collect(Collectors.toList());
    }

    public void deleteChat(User user, String targetPublicId) {
        User target = userRepository.findByPublicId(targetPublicId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        UUID userId = user.getId();
        UUID targetId = target.getId();

        List<Message> messages = messageRepository
                .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(
                        userId, targetId,
                        userId, targetId
                );

        for (Message msg : messages) {
            if (msg.getSenderId().equals(userId)) {
                msg.setDeletedBySender(true);
            }
            if (msg.getReceiverId().equals(userId)) {
                msg.setDeletedByReceiver(true);
            }
        }

        messageRepository.saveAll(messages);
    }

    public void sendMessage(User user,MessageRequest messageRequest) {
        Instant now = Instant.now();


        // 1. Mesajı oluştur ve kaydet
        Message message = Message.builder()
                .senderId(user.getId())
                .receiverId(messageRequest.getReceiverId())
                .content(messageRequest.getContent())
                .sentAt(now)
                .build();
        messageRepository.save(message);

        // 2. Conversation'ı bul veya oluştur
        UUID senderId = user.getId();
        UUID receiverId = messageRequest.getReceiverId();

        UUID user1Id = senderId.compareTo(receiverId) < 0 ? senderId : receiverId;
        UUID user2Id = senderId.compareTo(receiverId) < 0 ? receiverId : senderId;

        Conversation conversation = conversationRepository
                .findByUser1IdAndUser2Id(user1Id, user2Id)
                .orElseGet(() -> {
                    Conversation  newConv = new Conversation();
                    User user1=userRepository.findById(user1Id).orElse(null);
                    User user2=userRepository.findById(user2Id).orElse(null);
                    newConv.setUser1(user1);
                    newConv.setUser2(user2);
                    return newConv;
                });

       conversation.setLastMessage(message);
        conversationRepository.save(conversation);

        // 3. WebSocket ile mesajı gönder
        messagingTemplate.convertAndSend(
                "/topic/messages/" + receiverId,
                message
        );
    }


    public List<String> getChatList(User user) {
        UUID userId = user.getId();

        List<Message> messages = messageRepository.findBySenderIdOrReceiverId(userId, userId);

        Set<UUID> otherUsers = new HashSet<>();

        for (Message msg : messages) {
            UUID otherId = msg.getSenderId().equals(userId) ? msg.getReceiverId() : msg.getSenderId();
            otherUsers.add(otherId);
        }

        return otherUsers.stream()
                .map(id -> userRepository.findById(id)
                        .map(User::getPublicId)
                        .orElse("unknown"))
                .filter(p -> !p.equals("unknown"))
                .collect(Collectors.toList());
    }
}