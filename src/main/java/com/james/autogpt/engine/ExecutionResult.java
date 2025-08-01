package com.james.autogpt.engine;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of processing an EngineExecution
 * Contains status, messages, and any spawned tasks or actions
 */
@Data
public class ExecutionResult {
    
    public enum Status {
        COMPLETED,           // Execution completed successfully
        FAILED,             // Execution failed
        REQUIRES_REQUEUE,   // Execution needs to be requeued (for refinement)
        SPAWNED_NEW_TASKS   // Execution spawned new tasks (for branching)
    }
    
    private Status status;
    private String message;
    private List<String> spawnedTaskIds;
    private Object resultData;
    
    public ExecutionResult(Status status, String message) {
        this.status = status;
        this.message = message;
        this.spawnedTaskIds = new ArrayList<>();
    }
    
    public static ExecutionResult completed(String message) {
        return new ExecutionResult(Status.COMPLETED, message);
    }
    
    public static ExecutionResult failed(String message) {
        return new ExecutionResult(Status.FAILED, message);
    }
    
    public static ExecutionResult requiresRequeue(String message) {
        return new ExecutionResult(Status.REQUIRES_REQUEUE, message);
    }
    
    public static ExecutionResult spawnedNewTasks(String message, List<String> taskIds) {
        ExecutionResult result = new ExecutionResult(Status.SPAWNED_NEW_TASKS, message);
        result.setSpawnedTaskIds(taskIds);
        return result;
    }
    
    public void addSpawnedTaskId(String taskId) {
        this.spawnedTaskIds.add(taskId);
    }
} 