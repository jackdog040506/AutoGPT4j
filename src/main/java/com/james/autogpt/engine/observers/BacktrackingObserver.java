package com.james.autogpt.engine.observers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.james.autogpt.engine.ExecutionEvent;
import com.james.autogpt.engine.ExecutionObserver;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.service.TaskCreationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Observer implementation for backtracking functionality
 * Monitors execution events and triggers backtracking when needed
 */
@Slf4j
@Component
public class BacktrackingObserver implements ExecutionObserver {

    @Autowired
    private TaskCreationService taskCreationService;

    @Override
    public void onExecutionEvent(ExecutionEvent event, EngineExecution execution) {
        switch (event) {
            case FAILED:
                handleFailedExecution(execution);
                break;

            case COMPLETED:
                handleCompletedExecution(execution);
                break;

            case STARTED:
                log.debug("Execution started: {}", execution.getId());
                break;

            case REQUEUED:
                log.debug("Execution requeued: {}", execution.getId());
                break;

            default:
                // No action needed for other events
                break;
        }
    }

    /**
     * Handle failed executions - potentially trigger backtracking
     */
    private void handleFailedExecution(EngineExecution execution) {
        log.warn("Execution failed: {} - analyzing for backtracking", execution.getId());

        // Check if this execution should trigger backtracking
        // if (shouldTriggerBacktracking(execution)) {
        //     try {
        //         // Create a backtracking goal
        //         taskCreationService.createAIGotGoal(
        //             execution.getGoal().getTaskNode(),
        //             "Backtracking analysis for failed execution: " + execution.getId(),
        //             "BACKTRACKING"
        //         );

        //         log.info("Created backtracking goal for failed execution: {}", execution.getId());

        //     } catch (Exception e) {
        //         log.error("Failed to create backtracking goal for execution: {}", execution.getId(), e);
        //     }
        // }
    }

    /**
     * Handle completed executions - log success metrics
     */
    private void handleCompletedExecution(EngineExecution execution) {
        log.debug("Execution completed successfully: {}", execution.getId());

        // Could implement success pattern analysis here
        // For example, tracking successful reasoning paths for future optimization
    }

    /**
     * Determine if a failed execution should trigger backtracking
     */
    private boolean shouldTriggerBacktracking(EngineExecution execution) {
        // Simple heuristics for when to trigger backtracking
        // In a more sophisticated implementation, this could consider:
        // - Number of previous failures on this task
        // - Complexity of the task
        // - Available alternative approaches
        // - Resource constraints

        if (execution.getConfig() != null) {
            Boolean skipBacktracking = (Boolean) execution.getConfig().get("skipBacktracking");
            if (Boolean.TRUE.equals(skipBacktracking)) {
                return false;
            }
        }

        // Default: trigger backtracking for failed executions
        return true;
    }
}