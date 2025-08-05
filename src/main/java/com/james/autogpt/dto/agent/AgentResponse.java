package com.james.autogpt.dto.agent;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.model.Agent;

import lombok.Data;

@Data
public class AgentResponse {

    private String agentId;
    private String roles;
    private String focus;
    private String personality;
    private AgentType agentType;

    public static AgentResponse fromAgent(Agent agent) {
        AgentResponse response = new AgentResponse();
        response.setAgentId(agent.getAgentId());
        response.setRoles(agent.getRoles());
        response.setFocus(agent.getFocus());
        response.setPersonality(agent.getPersonality());
        response.setAgentType(agent.getAgentType());
        return response;
    }
}