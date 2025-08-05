package com.james.autogpt.dto.scopes;

public enum AgentType {
	TaskExecutor,
	AIObserver,
	AIThinking,  // Default goal when TaskNode spawn
	AIGot        // Goal that handles Refining, Backtracking, Aggregating, Branching
}
