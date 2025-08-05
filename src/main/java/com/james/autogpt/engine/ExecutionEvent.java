package com.james.autogpt.engine;

/**
 * Events that can occur during execution processing
 * Used by observers for backtracking and monitoring
 */
public enum ExecutionEvent {
    STARTED,      // Execution has started processing
    COMPLETED,    // Execution completed successfully
    FAILED,       // Execution failed
    REQUEUED,     // Execution was requeued for refinement
    PAUSED,       // Execution was paused
    RESUMED       // Execution was resumed
}