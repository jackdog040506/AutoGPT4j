package com.james.autogpt.service;

import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.task.CreateAIPlannerTaskRequest;
import com.james.autogpt.dto.task.CreateTaskRequest;
import com.james.autogpt.dto.task.TaskResponse;
import com.james.autogpt.model.EngineGoal;
import com.james.autogpt.model.TaskNode;

public interface TaskService {

    /**
     * Creates a master node with one engine goal containing an engine execution with Agent of type AIPlanner
     * @param request The request containing conversation ID, name, and description
     * @return Result containing the created task information
     */
    Result<TaskResponse> createMasterNodeWithAIPlannerGoal(CreateAIPlannerTaskRequest request);

    /**
     * Creates a master node with one engine goal containing an engine execution with a specific agent
     * @param request The request containing conversation ID, name, description, and optional agent ID
     * @return Result containing the created task information
     */
    Result<TaskResponse> createMasterNodeWithGoal(CreateTaskRequest request);

    /**
     * Adds an EngineGoal to a TaskNode
     * @param taskNode The TaskNode to add the goal to
     * @param engineGoal The EngineGoal to add
     * @return Result containing the saved EngineGoal
     */
    Result<EngineGoal> addEngineGoalToTaskNode(TaskNode taskNode, EngineGoal engineGoal);
}