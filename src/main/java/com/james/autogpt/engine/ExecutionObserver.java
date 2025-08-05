package com.james.autogpt.engine;

import com.james.autogpt.model.EngineExecution;

/**
 * Observer interface for monitoring execution events
 * Used for implementing backtracking and aggregation patterns
 */
public interface ExecutionObserver {

    /**
     * Called when an execution event occurs
     *
     * @param event The type of event that occurred
     * @param execution The execution that triggered the event
     */
    void onExecutionEvent(ExecutionEvent event, EngineExecution execution);
}