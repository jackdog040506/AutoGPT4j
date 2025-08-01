package com.james.autogpt.dto.scopes;

/**
 * Enum representing the six phases of the Agentic Loop
 * Think → Reason → Plan → Criticize → Act (Command) → Speak
 */
public enum AgenticCycle {
    THINK,      // Collect and concentrate sources of truth to prepare for reasoning
    REASON,     // Generate a focused breakdown of the current subgoal
    PLAN,       // Decide on subgoals or the next goal for the current node
    CRITICIZE,  // Evaluate plans and provide constructive alternatives
    ACT,        // Transform final decisions into actionable goals
    SPEAK       // Summarize the process and store in vector DB
}
