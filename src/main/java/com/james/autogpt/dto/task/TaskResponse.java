package com.james.autogpt.dto.task;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TaskResponse {

    private String masterNodeId;
    private String rootTaskNodeId;
    private String engineGoalId;
    private String engineExecutionId;
    private String conversationId;
    private String name;
    private String description;
    private String agentId;
    private String agentType;
    private LocalDateTime createdAt;

    public static TaskResponse fromTaskNodeMaster(String masterNodeId, String rootTaskNodeId,
                                               String engineGoalId, String engineExecutionId,
                                               String conversationId, String name, String description,
                                               String agentId, String agentType, LocalDateTime createdAt) {
        TaskResponse response = new TaskResponse();
        response.setMasterNodeId(masterNodeId);
        response.setRootTaskNodeId(rootTaskNodeId);
        response.setEngineGoalId(engineGoalId);
        response.setEngineExecutionId(engineExecutionId);
        response.setConversationId(conversationId);
        response.setName(name);
        response.setDescription(description);
        response.setAgentId(agentId);
        response.setAgentType(agentType);
        response.setCreatedAt(createdAt);
        return response;
    }
}