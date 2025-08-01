package com.james.autogpt.dto.scopes;

/**
 * Defines the types of reasoning actions that can be performed by AIGot agent type
 * Based on Graph of Thought reasoning patterns
 */
public enum ReasoningAction {
    REFINING,       // Re-spawn a new AIThinking goal on TaskNode, queue back and resort priority
    BACKTRACKING,   // Implement no-go summary and maybe refining proximity associate with queue
    AGGREGATING,    // Make empty implement for now (placeholder for future)
    BRANCHING       // Create new TaskNode as child with default AIThinking goal
} 