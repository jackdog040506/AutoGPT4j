package com.james.autogpt.dto.task;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAIPlannerTaskRequest {

	@NotBlank(message = "Task name is required")
	@Size(max = 255, message = "Task name must not exceed 255 characters")
	private String name;

	private String masterPrompt;

	private String rootPrompt;

	@NotBlank(message = "Major agent ID is required")
	@Size(max = 255, message = "Major agent ID must not exceed 255 characters")
	private String majorAgentId;

	@NotNull(message = "Agent IDs list is required")
	private List<String> agentIds;
}