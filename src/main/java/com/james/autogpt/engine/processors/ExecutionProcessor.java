package com.james.autogpt.engine.processors;

import com.james.autogpt.engine.ExecutionResult;
import com.james.autogpt.model.EngineExecution;

/**
 * Interface for processing different types of EngineExecution
 * Each AgentType has its own implementation of this interface
 */
public interface ExecutionProcessor {

    /**
     * Process an EngineExecution and return the result
     *
     * @param execution The execution to process
     * @return ExecutionResult containing the outcome
     */
    ExecutionResult process(EngineExecution execution);

    /**
     * Get the agent type this processor handles
     *
     * @return The AgentType this processor is designed for
     */
    com.james.autogpt.dto.scopes.AgentType getAgentType();
}