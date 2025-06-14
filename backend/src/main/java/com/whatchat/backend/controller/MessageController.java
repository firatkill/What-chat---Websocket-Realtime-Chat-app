package com.whatchat.backend.controller;

import com.whatchat.backend.dto.chat.MessageRequest;
import com.whatchat.backend.dto.ws.ChatMessage;
import com.whatchat.backend.entity.Conversation;
import com.whatchat.backend.entity.Message;
import com.whatchat.backend.entity.User;
import com.whatchat.backend.payload.response.ApiResponse;
import com.whatchat.backend.service.ConversationService;
import com.whatchat.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final ConversationService conversationService;


    @GetMapping
    public ResponseEntity<ApiResponse<List<Conversation>>> getChatList(@AuthenticationPrincipal User user) {

        List<Conversation> data=conversationService.getAllConversations(user);
            ApiResponse<List<Conversation>> response = new ApiResponse<>(true,"Conversations fetched successfully",null,data);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMessages(@AuthenticationPrincipal User user,
                                     @PathVariable String publicId) {
        List<Message> data=messageService.getMessages(user,publicId);

        ApiResponse<List<Message>> response = new ApiResponse<>(true,"Message fetched successfully",null,data);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<ApiResponse<Void>> deleteChat(@AuthenticationPrincipal User user,
                               @PathVariable String publicId) {
        messageService.deleteChat(user, publicId);

        ApiResponse<Void> apiResponse= new ApiResponse<>(true,"Message deleted successfully",null,null);
        return ResponseEntity.ok(apiResponse);
    }
}