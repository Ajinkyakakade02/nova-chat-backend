package com.chat.app.controller;

import com.chat.app.model.ChatMessage;
import com.chat.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // REST endpoint to get messages for a room
    @GetMapping("/messages/{roomId}")
    public List<ChatMessage> getMessages(@PathVariable String roomId) {
        return chatService.getMessagesByRoom(roomId);
    }

    // WebSocket endpoints
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        chatService.saveAndSend(chatMessage);
    }
}