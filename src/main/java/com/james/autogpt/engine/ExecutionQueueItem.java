package com.james.autogpt.engine;

import com.james.autogpt.model.EngineExecution;

import lombok.Data;

/**
 * Wrapper class for EngineExecution items in the priority queue
 * Includes priority and timing information for queue management
 */
@Data
public class ExecutionQueueItem {
    private final EngineExecution execution;
    private final Integer priority;
    private final long queuedAt;

    public ExecutionQueueItem(EngineExecution execution, Integer priority, long queuedAt) {
        this.execution = execution;
        this.priority = priority;
        this.queuedAt = queuedAt;
    }
}