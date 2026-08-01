package com.chat.app.repository;

import com.chat.app.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, String> {
    List<GroupEntity> findByMembersUserId(String userId);
}