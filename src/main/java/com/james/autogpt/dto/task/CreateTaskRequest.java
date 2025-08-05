package com.james.autogpt.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateTaskRequest {

    @NotBlank(message = "Task name is required")
    @Size(max = 255, message = "Task name must not exceed 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    @Size(max = 255, message = "Agent ID must not exceed 255 characters")
    private String agentId; // Optional, if not provided will use AIPlanner

    private String masterPrompt;

    private String rootPrompt;

    //parent task node id for sub task forking check
    private String taskNodeId;
}