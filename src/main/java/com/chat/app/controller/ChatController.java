package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // REST endpoint to get messages for a room – with privacy check
    @GetMapping("/messages/{roomId}")
    public ResponseEntity<?> getMessages(
            @PathVariable String roomId,
            @RequestParam String userId) {
        
        // Privacy check: only return messages if userId is part of the room
        if (!chatService.isUserInRoom(userId, roomId)) {
            return ResponseEntity.ok(List.of()); // return empty list
        }
        
        List<ChatMessage> messages = chatService.getMessagesByRoom(roomId);
        return ResponseEntity.ok(messages);
    }

    // WebSocket endpoint
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatService.saveAndSend(chatMessage);
    }

    // WebSocket join
    @MessageMapping("/chat.join")
    public void joinRoom(@Payload Map<String, String> payload) {
        String userId = payload.get("userId");
        String username = payload.get("username");
        String roomId = payload.get("roomId");
        chatService.userJoined(userId, username, roomId);
    }
}
