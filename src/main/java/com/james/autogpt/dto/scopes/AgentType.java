package com.james.autogpt.dto.scopes;

public enum AgentType {
	TaskExecutor,
	AIAgent,
	AIObserver,
	AIPlanner,
	AIThinking,  // Default goal when TaskNode spawn
	AIGot        // Goal that handles Refining, Backtracking, Aggregating, Branching
}
