package com.chat.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicator {
    private String userId;
    private String username;
    private String roomId;
    private boolean typing;
}