package com.whatchat.backend;

import com.whatchat.backend.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtChannelInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            handleConnect(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            handleSubscribe(accessor);
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String token = accessor.getFirstNativeHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                UUID userId = jwtTokenProvider.getUserIdFromJWT(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());
                accessor.setUser(authentication);
            } else {
                throw new IllegalArgumentException("Invalid JWT token");
            }
        } else {
            throw new IllegalArgumentException("Authorization header is missing or malformed");
        }
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        if (user == null || user.getName() == null) {
            throw new IllegalArgumentException("User is not authenticated");
        }

        String destination = accessor.getDestination();
        if (destination == null) {
            throw new IllegalArgumentException("Destination is missing");
        }

        String[] parts = destination.split("/");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Invalid destination format");
        }

        try {
            UUID userIdFromTopic = UUID.fromString(parts[parts.length - 1]);
            UUID userIdFromToken = UUID.fromString(user.getName());

            if (!userIdFromToken.equals(userIdFromTopic)) {
                throw new IllegalArgumentException("Unauthorized subscription attempt");
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid UUID in topic", ex);
        }
    }
}