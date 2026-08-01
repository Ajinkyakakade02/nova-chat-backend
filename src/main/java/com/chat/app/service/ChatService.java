package com.chat.app.service;

import com.chat.app.model.ChatMessage;
import com.chat.app.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate; // for real-time

    public ChatMessage saveAndSend(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        ChatMessage saved = messageRepository.save(message);

        // Send to the room topic
        String destination = "/topic/room-" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, saved);
        return saved;
    }

    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }
}