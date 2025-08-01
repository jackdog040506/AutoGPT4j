package com.james.autogpt.service;

import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.dto.scopes.EngineGoalStatus;
import com.james.autogpt.dto.scopes.ExecutionStatus;
import com.james.autogpt.model.*;
import com.james.autogpt.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * Service for programmatically creating tasks, goals, and executions
 * Used by the engine processors for dynamic task creation
 */
@Slf4j
@Service
@Transactional
public class TaskCreationService {
    
    @Autowired
    private TaskNodeRepository taskNodeRepository;
    
    @Autowired
    private EngineGoalRepository engineGoalRepository;
    
    @Autowired
    private EngineExecutionRepository engineExecutionRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    /**
     * Create a child TaskNode for branching operations
     */
    public TaskNode createChildTaskNode(TaskNode parent, String prompt, Integer priority) {
        TaskNode childTask = new TaskNode();
        childTask.setName("Child of: " + parent.getName());
        childTask.setDescription("Branched task from parent: " + parent.getId());
        childTask.setPrompt(prompt);
        childTask.setPriority(priority);
        childTask.setParentTask(parent);
        childTask.setTaskNodeMaster(parent.getTaskNodeMaster());
        
        TaskNode savedChild = taskNodeRepository.save(childTask);
        
        // Add to parent's subtasks
        parent.getSubTasks().add(savedChild);
        taskNodeRepository.save(parent);
        
        log.debug("Created child TaskNode {} for parent {}", savedChild.getId(), parent.getId());
        return savedChild;
    }
    
    /**
     * Create an AIThinking goal for the given TaskNode
     */
    public EngineGoal createAIThinkingGoal(TaskNode taskNode, String description) {
        // Find AIThinking agent
        Agent aiThinkingAgent = agentRepository.findByAgentType(AgentType.AIThinking);
        if (aiThinkingAgent == null) {
            throw new IllegalStateException("No AIThinking agent found in the system");
        }
        
        // Create goal
        EngineGoal goal = new EngineGoal();
        goal.setName("AIThinking for: " + taskNode.getName());
        goal.setDescription(description);
        goal.setPriority(taskNode.getPriority());
        goal.setStatus(EngineGoalStatus.PENDING);
        goal.setTaskNode(taskNode);
        
        EngineGoal savedGoal = engineGoalRepository.save(goal);
        
        // Create execution
        EngineExecution execution = new EngineExecution();
        execution.setAgent(aiThinkingAgent);
        execution.setStatus(ExecutionStatus.STALLED);
        execution.setGoal(savedGoal);
        execution.setConfig(new HashMap<>());
        
        EngineExecution savedExecution = engineExecutionRepository.save(execution);
        
        // Establish relationships
        savedGoal.addExecution(savedExecution);
        taskNode.getGoals().add(savedGoal);
        
        engineGoalRepository.save(savedGoal);
        taskNodeRepository.save(taskNode);
        
        log.debug("Created AIThinking goal {} and execution {} for TaskNode {}", 
            savedGoal.getId(), savedExecution.getId(), taskNode.getId());
        
        return savedGoal;
    }
    
    /**
     * Create an AIGot goal for Graph of Thought reasoning
     */
    public EngineGoal createAIGotGoal(TaskNode taskNode, String description, String reasoningAction) {
        // Find AIGot agent
        Agent aiGotAgent = agentRepository.findByAgentType(AgentType.AIGot);
        if (aiGotAgent == null) {
            throw new IllegalStateException("No AIGot agent found in the system");
        }
        
        // Create goal
        EngineGoal goal = new EngineGoal();
        goal.setName("AIGot for: " + taskNode.getName());
        goal.setDescription(description);
        goal.setPriority(taskNode.getPriority());
        goal.setStatus(EngineGoalStatus.PENDING);
        goal.setTaskNode(taskNode);
        
        EngineGoal savedGoal = engineGoalRepository.save(goal);
        
        // Create execution with reasoning action
        EngineExecution execution = new EngineExecution();
        execution.setAgent(aiGotAgent);
        execution.setStatus(ExecutionStatus.STALLED);
        execution.setGoal(savedGoal);
        
        HashMap<String, Object> config = new HashMap<>();
        config.put("reasoningAction", reasoningAction);
        execution.setConfig(config);
        
        EngineExecution savedExecution = engineExecutionRepository.save(execution);
        
        // Establish relationships
        savedGoal.addExecution(savedExecution);
        taskNode.getGoals().add(savedGoal);
        
        engineGoalRepository.save(savedGoal);
        taskNodeRepository.save(taskNode);
        
        log.debug("Created AIGot goal {} with action {} and execution {} for TaskNode {}", 
            savedGoal.getId(), reasoningAction, savedExecution.getId(), taskNode.getId());
        
        return savedGoal;
    }
} 