package com.chat.app.repository;

import com.chat.app.model.GroupEntity;
import com.chat.app.model.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupMemberRepository extends JpaRepository<GroupMember, String> {
    List<GroupMember> findByGroup(GroupEntity group);
}