package com.chat.app.repository;

import com.chat.app.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, String> {

    List<ChatMessage> findByRoomIdOrderByTimestampAsc(String roomId);

    @Query("SELECT m FROM ChatMessage m WHERE m.roomId = :roomId ORDER BY m.timestamp DESC LIMIT 50")
    List<ChatMessage> findRecentMessages(@Param("roomId") String roomId);

    List<ChatMessage> findBySenderIdAndRecipientIdOrderByTimestampAsc(String senderId, String recipientId);
}