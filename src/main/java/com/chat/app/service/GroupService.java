package com.chat.app.service;

import com.chat.app.model.GroupEntity;
import com.chat.app.model.GroupMember;
import com.chat.app.repository.GroupMemberRepository;
import com.chat.app.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public GroupEntity createGroup(String name, String avatar, List<String> memberUserIds) {
        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setAvatar(avatar);
        group = groupRepository.save(group);

        for (String userId : memberUserIds) {
            GroupMember member = new GroupMember();
            member.setGroup(group);
            member.setUserId(userId);
            groupMemberRepository.save(member);
        }
        return group;
    }

    public List<GroupEntity> getGroupsForUser(String userId) {
        return groupRepository.findByMembersUserId(userId);
    }

    public GroupEntity getGroupById(String groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public List<GroupMember> getMembers(String groupId) {
        GroupEntity group = getGroupById(groupId);
        return groupMemberRepository.findByGroup(group);
    }
}