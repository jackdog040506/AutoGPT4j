package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.model.TaskNodeMaster;

public interface TaskNodeMasterRepository extends JpaRepository<TaskNodeMaster, String> {
    TaskNodeMaster findByConversationId(String conversationId);
} 