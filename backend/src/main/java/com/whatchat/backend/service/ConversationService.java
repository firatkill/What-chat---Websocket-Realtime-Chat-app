package com.whatchat.backend.service;

import com.whatchat.backend.entity.Conversation;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;


    public List<Conversation> getAllConversations(User user){

        return conversationRepository.findAllByUser1IdOrUser2Id(user.getId(), user.getId());


    }

}
