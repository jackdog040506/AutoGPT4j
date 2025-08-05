package com.james.autogpt.engine.processors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.dto.scopes.EngineGoalStatus;
import com.james.autogpt.dto.scopes.ReasoningAction;
import com.james.autogpt.engine.ExecutionResult;
import com.james.autogpt.engine.PriorityManager;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.model.EngineGoal;
import com.james.autogpt.model.TaskNode;
import com.james.autogpt.repository.EngineGoalRepository;
import com.james.autogpt.service.TaskCreationService;

import lombok.extern.slf4j.Slf4j;

/**
 * Processor for AIGot agent type
 * Handles Graph of Thought reasoning operations:
 * - Refining: re-spawn AIThinking goal with updated priority
 * - Backtracking: implement no-go summary and proximity refining
 * - Aggregating: placeholder for future implementation
 * - Branching: create new TaskNode as child with AIThinking goal
 */
@Slf4j
@Component
public class AIGotProcessor implements ExecutionProcessor {

    @Autowired
    private PriorityManager priorityManager;

    @Autowired
    private TaskCreationService taskCreationService;

    @Autowired
    private EngineGoalRepository engineGoalRepository;

    @Override
    public ExecutionResult process(EngineExecution execution) {
        log.debug("Processing AIGot execution: {}", execution.getId());

        try {
            // Determine the reasoning action from execution config
            ReasoningAction action = determineReasoningAction(execution);

            switch (action) {
                case REFINING:
                    return handleRefining(execution);

                case BACKTRACKING:
                    return handleBacktracking(execution);

                case AGGREGATING:
                    return handleAggregating(execution);

                case BRANCHING:
                    return handleBranching(execution);

                default:
                    log.warn("Unknown reasoning action for execution: {}", execution.getId());
                    return ExecutionResult.failed("Unknown reasoning action");
            }

        } catch (Exception e) {
            log.error("Error in AIGot processing for execution: {}", execution.getId(), e);
            execution.getGoal().setStatus(EngineGoalStatus.FAILED);
            engineGoalRepository.save(execution.getGoal());
            return ExecutionResult.failed("AIGot processing error: " + e.getMessage());
        }
    }

    /**
     * Handle Refining operation:
     * Re-spawn a new AIThinking goal on TaskNode, queue back and resort priority
     */
    private ExecutionResult handleRefining(EngineExecution execution) {
        log.debug("Handling refining for execution: {}", execution.getId());

        TaskNode taskNode = execution.getGoal().getTaskNode();

        // Update task priority for refinement
        Integer newPriority = priorityManager.calculateRefinementPriority(taskNode.getPriority());
        taskNode.setPriority(newPriority);

        // Create new AIThinking goal
        EngineGoal refinedGoal = taskCreationService.createAIThinkingGoal(taskNode, "Refined thinking goal");

        // Mark current goal as completed
        execution.getGoal().setStatus(EngineGoalStatus.COMPLETED);
        engineGoalRepository.save(execution.getGoal());

        log.info("Refining completed for execution: {} - new priority: {}", execution.getId(), newPriority);
        return ExecutionResult.requiresRequeue("Task refined and queued for re-processing");
    }

    /**
     * Handle Backtracking operation:
     * Implement no-go summary and maybe refining proximity associate with queue
     */
    private ExecutionResult handleBacktracking(EngineExecution execution) {
        log.debug("Handling backtracking for execution: {}", execution.getId());

        // Create no-go summary
        String noGoSummary = createNoGoSummary(execution);

        // Store summary in execution config
        Map<String, Object> config = execution.getConfig();
        config.put("noGoSummary", noGoSummary);
        config.put("backtrackingCompleted", true);

        // Mark as completed - backtracking doesn't spawn new tasks
        execution.getGoal().setStatus(EngineGoalStatus.COMPLETED);
        engineGoalRepository.save(execution.getGoal());

        log.info("Backtracking completed for execution: {}", execution.getId());
        return ExecutionResult.completed("Backtracking analysis completed with no-go summary");
    }

    /**
     * Handle Aggregating operation:
     * Empty implementation for now (placeholder)
     */
    private ExecutionResult handleAggregating(EngineExecution execution) {
        log.debug("Handling aggregating for execution: {} - placeholder implementation", execution.getId());

        // TODO: Implement aggregation logic
        // This would typically involve:
        // - Collecting results from multiple related executions
        // - Synthesizing information
        // - Creating aggregate insights

        execution.getGoal().setStatus(EngineGoalStatus.COMPLETED);
        engineGoalRepository.save(execution.getGoal());

        log.info("Aggregating completed (placeholder) for execution: {}", execution.getId());
        return ExecutionResult.completed("Aggregating operation completed (placeholder)");
    }

    /**
     * Handle Branching operation:
     * Create new TaskNode as child with default AIThinking goal
     */
    private ExecutionResult handleBranching(EngineExecution execution) {
        log.debug("Handling branching for execution: {}", execution.getId());

        TaskNode parentTask = execution.getGoal().getTaskNode();

        // Determine branch prompts from execution config
        List<String> branchPrompts = getBranchPrompts(execution);
        List<String> spawnedTaskIds = new ArrayList<>();

        for (String branchPrompt : branchPrompts) {
            // Create child TaskNode
            TaskNode childTask = taskCreationService.createChildTaskNode(
                parentTask,
                branchPrompt,
                priorityManager.calculateBranchPriority(parentTask.getPriority())
            );

            // Create AIThinking goal for the child
            EngineGoal childGoal = taskCreationService.createAIThinkingGoal(childTask, "Branched thinking goal");

            spawnedTaskIds.add(childTask.getId());

            log.debug("Created branch task: {} for parent: {}", childTask.getId(), parentTask.getId());
        }

        // Mark current goal as completed
        execution.getGoal().setStatus(EngineGoalStatus.COMPLETED);
        engineGoalRepository.save(execution.getGoal());

        log.info("Branching completed for execution: {} - spawned {} child tasks",
            execution.getId(), spawnedTaskIds.size());

        return ExecutionResult.spawnedNewTasks("Branching completed successfully", spawnedTaskIds);
    }

    /**
     * Determine the reasoning action from execution configuration
     */
    private ReasoningAction determineReasoningAction(EngineExecution execution) {
        Map<String, Object> config = execution.getConfig();

        if (config != null && config.containsKey("reasoningAction")) {
            String actionStr = (String) config.get("reasoningAction");
            try {
                return ReasoningAction.valueOf(actionStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid reasoning action in config: {}", actionStr);
            }
        }

        // Default to refining if not specified
        return ReasoningAction.REFINING;
    }

    /**
     * Create a no-go summary for backtracking
     */
    private String createNoGoSummary(EngineExecution execution) {
        StringBuilder summary = new StringBuilder();
        summary.append("No-Go Analysis for Execution: ").append(execution.getId()).append(System.lineSeparator());
        summary.append("Task: ").append(execution.getGoal().getTaskNode().getName()).append(System.lineSeparator());
        summary.append("Reason: Failed to achieve goal through current approach\n");
        summary.append("Timestamp: ").append(System.currentTimeMillis()).append(System.lineSeparator());

        // Add any specific failure reasons from config
        Map<String, Object> config = execution.getConfig();
        if (config != null && config.containsKey("failureReason")) {
            summary.append("Failure Reason: ").append(config.get("failureReason")).append(System.lineSeparator());
        }

        return summary.toString();
    }

    /**
     * Get branch prompts from execution configuration
     */
    @SuppressWarnings("unchecked")
    private List<String> getBranchPrompts(EngineExecution execution) {
        Map<String, Object> config = execution.getConfig();

        if (config != null && config.containsKey("branchPrompts")) {
            Object branchPromptsObj = config.get("branchPrompts");
            if (branchPromptsObj instanceof List) {
                return (List<String>) branchPromptsObj;
            }
        }

        // Default branch prompts if not specified
        List<String> defaultPrompts = new ArrayList<>();
        defaultPrompts.add("Explore alternative approach to: " + execution.getGoal().getTaskNode().getPrompt());
        defaultPrompts.add("Consider different perspective on: " + execution.getGoal().getTaskNode().getPrompt());

        return defaultPrompts;
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.AIGot;
    }
}