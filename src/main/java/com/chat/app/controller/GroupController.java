package com.chat.app.controller;

import com.chat.app.model.GroupEntity;
import com.chat.app.model.GroupMember;
import com.chat.app.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupEntity> createGroup(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String avatar = (String) request.get("avatar");
        List<String> memberIds = (List<String>) request.get("members");
        GroupEntity group = groupService.createGroup(name, avatar, memberIds);
        return ResponseEntity.ok(group);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GroupEntity>> getUserGroups(@PathVariable String userId) {
        return ResponseEntity.ok(groupService.getGroupsForUser(userId));
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<GroupEntity> getGroup(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getGroupById(groupId));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<GroupMember>> getMembers(@PathVariable String groupId) {
        return ResponseEntity.ok(groupService.getMembers(groupId));
    }
}