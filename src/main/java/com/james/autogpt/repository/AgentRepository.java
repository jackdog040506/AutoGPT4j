package com.james.autogpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.model.Agent;

public interface AgentRepository extends JpaRepository<Agent, Long> {
    Agent findByAgentId(String agentId);
    Agent findByAgentType(AgentType agentType);
} 