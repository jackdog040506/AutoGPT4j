package com.james.autogpt.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;

@Data
public class PlanGoal {
    @JsonPropertyDescription("Agent ID , comes from the AVAILABLE AGENT OPTIONS")
    private String agentId;
    @JsonPropertyDescription("Agent Type")
    private String agentType;
    @JsonPropertyDescription("Agent Roles")
    private String roles;
    @JsonPropertyDescription("Current summary of the goal")
    private String prompt;
    @JsonPropertyDescription("Name of the goal")
    private String name;
    @JsonPropertyDescription("current action plan about the agent to achieve the goal")
    private String description;
}
