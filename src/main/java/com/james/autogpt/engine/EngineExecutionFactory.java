package com.james.autogpt.engine;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.engine.processors.ExecutionProcessor;
import com.james.autogpt.model.EngineExecution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Factory that processes EngineExecution input and delegates to appropriate
 * implementation
 * based on the Agent's AgentType. Implements the factory pattern for different
 * AI reasoning approaches (AIThinking, AIGot, etc.)
 */
@Slf4j
@Component
public class EngineExecutionFactory {

    private final EnumMap<AgentType, ExecutionProcessor> processors = new EnumMap<>(AgentType.class);

    public EngineExecutionFactory(@Autowired List<ExecutionProcessor> autowired) {
        for (ExecutionProcessor processor : autowired) {
            AgentType agentType = processor.getAgentType();
            processors.put(agentType, processor);
            log.info("Registered processor for agent type: {}", agentType);
        }

        log.info("Initialized processor map with {} processors", processors.size());
    }

    /**
     * Process an EngineExecution by delegating to the appropriate processor
     * based on the Agent's type
     * 
     * @param execution The EngineExecution to process
     * @return ExecutionResult containing the outcome and any follow-up actions
     */
    public ExecutionResult processExecution(EngineExecution execution) {
        if (execution == null || execution.getAgent() == null) {
            log.error("Invalid execution or missing agent");
            return ExecutionResult.failed("Invalid execution or missing agent");
        }

        AgentType agentType = execution.getAgent().getAgentType();
        ExecutionProcessor processor = processors.get(agentType);

        if (processor == null) {
            log.error("No processor found for agent type: {}", agentType);
            return ExecutionResult.failed("No processor found for agent type: " + agentType);
        }

        try {
            log.debug("Processing execution {} with agent type {}",
                    execution.getId(), agentType);

            return processor.process(execution);

        } catch (Exception e) {
            log.error("Error processing execution {} with agent type {}",
                    execution.getId(), agentType, e);
            return ExecutionResult.failed("Processing error: " + e.getMessage());
        }
    }

    /**
     * Check if a processor exists for the given agent type
     */
    public boolean hasProcessor(AgentType agentType) {
        return processors.containsKey(agentType);
    }

    /**
     * Get available processor types
     */
    public java.util.Set<AgentType> getAvailableProcessors() {
        return processors.keySet();
    }
}