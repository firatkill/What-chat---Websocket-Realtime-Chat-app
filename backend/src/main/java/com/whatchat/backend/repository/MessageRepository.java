package com.whatchat.backend.repository;

import com.whatchat.backend.entity.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
@Transactional
public interface MessageRepository extends JpaRepository<Message, UUID> {

    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(
            UUID senderId1, UUID receiverId1,
            UUID receiverId2, UUID senderId2
    );

    List<Message> findBySenderIdOrReceiverId(UUID senderId, UUID receiverId);
}