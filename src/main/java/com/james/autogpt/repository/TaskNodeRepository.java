package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.TaskNode;

public interface TaskNodeRepository extends JpaRepository<TaskNode, String> {
    TaskNode findByConversationId(String conversationId);
} 