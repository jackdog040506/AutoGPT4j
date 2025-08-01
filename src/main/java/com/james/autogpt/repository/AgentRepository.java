package com.james.autogpt.repository;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.model.Agent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {
    
    /**
     * Find agent by type
     */
    Agent findByAgentType(AgentType agentType);
    
    /**
     * Find agent by ID
     */
    Agent findByAgentId(String agentId);
} 