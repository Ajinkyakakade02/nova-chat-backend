package com.chat.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @Column(name = "user_id", nullable = false)
    private String userId;
}