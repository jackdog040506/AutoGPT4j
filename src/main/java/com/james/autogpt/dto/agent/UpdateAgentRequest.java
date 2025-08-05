package com.james.autogpt.dto.agent;

import com.james.autogpt.dto.scopes.AgentType;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAgentRequest {

    @Size(max = 1000, message = "Roles must not exceed 1000 characters")
    private String roles; // comma-separated roles

    @Size(max = 500, message = "Focus must not exceed 500 characters")
    private String focus;

    @Size(max = 1000, message = "Personality must not exceed 1000 characters")
    private String personality;

    private AgentType agentType;
}