package com.chat.app.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "full_name")
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    private String phone; // alias

    private String password;

    @Column(length = 500)
    private String about;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.OFFLINE;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum UserStatus {
        ONLINE, OFFLINE, AWAY
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}