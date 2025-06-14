package com.whatchat.backend.repository;

import com.whatchat.backend.entity.Conversation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUser1IdAndUser2Id(UUID user1Id, UUID user2Id);

    List<Conversation> findAllByUser1IdOrUser2Id(UUID user1Id, UUID user2Id);


}
