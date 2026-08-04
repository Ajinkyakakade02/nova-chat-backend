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
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Save a message to the database and broadcast it to the room topic.
     */
    public ChatMessage saveAndSend(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        ChatMessage saved = messageRepository.save(message);

        // Broadcast to all subscribers of this room
        String destination = "/topic/room-" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, saved);
        return saved;
    }

    /**
     * Get all messages for a given room, ordered by timestamp.
     */
    public List<ChatMessage> getMessagesByRoom(String roomId) {
        return messageRepository.findByRoomIdOrderByTimestampAsc(roomId);
    }

    /**
     * Handle a user joining a room (optional – can send a JOIN message).
     */
    public void userJoined(String userId, String username, String roomId) {
        ChatMessage joinMessage = new ChatMessage();
        joinMessage.setSenderId(userId);
        joinMessage.setSenderName(username);
        joinMessage.setContent(username + " joined");
        joinMessage.setRoomId(roomId);
        joinMessage.setType(ChatMessage.MessageType.JOIN);
        joinMessage.setTimestamp(LocalDateTime.now());

        messageRepository.save(joinMessage);

        String destination = "/topic/room-" + roomId;
        messagingTemplate.convertAndSend(destination, joinMessage);
    }

    /**
     * Check if a user is a participant in a given room.
     * Room IDs are created by sorting two user IDs alphabetically and joining with "-".
     * Example: "local-1" and "abc-123" → roomId = "abc-123-local-1"
     */
    public boolean isUserInRoom(String userId, String roomId) {
        if (roomId == null || userId == null) {
            return false;
        }

        String[] participants = roomId.split("-");
        for (String participant : participants) {
            if (participant.equals(userId)) {
                return true;
            }
        }
        return false;
    }
}
