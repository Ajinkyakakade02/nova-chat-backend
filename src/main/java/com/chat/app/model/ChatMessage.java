package com.chat.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Column(name = "recipient_id")
    private String recipientId;

    public enum MessageType {
        CHAT, JOIN, LEAVE, TYPING, PRIVATE
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}