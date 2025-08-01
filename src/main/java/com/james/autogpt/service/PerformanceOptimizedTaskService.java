package com.james.autogpt.service;

import com.james.autogpt.model.TaskNode;
import com.james.autogpt.model.TaskNodeMaster;
import com.james.autogpt.model.EngineGoal;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.repository.TaskNodeRepository;
import com.james.autogpt.repository.TaskNodeMasterRepository;
import com.james.autogpt.repository.EngineGoalRepository;
import com.james.autogpt.repository.EngineExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Demonstrates best practices for using EntityGraphs to optimize performance
 * and avoid N+1 query problems.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PerformanceOptimizedTaskService {
//
//    private final TaskNodeRepository taskNodeRepository;
//    private final TaskNodeMasterRepository taskNodeMasterRepository;
//    private final EngineGoalRepository engineGoalRepository;
//    private final EngineExecutionRepository engineExecutionRepository;
//
//    /**
//     * Load TaskNode with its immediate subtasks - optimized for UI tree display
//     */
//    public Optional<TaskNode> loadTaskNodeWithSubTasks(String conversationId) {
//        log.debug("Loading TaskNode with subtasks for conversation: {}", conversationId);
//        
//        // Single query loads TaskNode + subTasks + parentTask
//        return taskNodeRepository.findWithSubTasksByConversationId(conversationId);
//    }
//
//    /**
//     * Load TaskNode with all goals and their executions - optimized for goal management
//     */
//    public Optional<TaskNode> loadTaskNodeWithGoals(String conversationId) {
//        log.debug("Loading TaskNode with goals for conversation: {}", conversationId);
//        
//        // Single query loads TaskNode + goals + executions
//        return taskNodeRepository.findWithGoalsByConversationId(conversationId);
//    }
//
//    /**
//     * Load complete TaskNode with all relationships - use sparingly for detailed views
//     */
//    public Optional<TaskNode> loadCompleteTaskNode(String conversationId) {
//        log.debug("Loading complete TaskNode for conversation: {}", conversationId);
//        
//        // Single query loads TaskNode + subTasks + goals + executions + agents + taskNodeMaster
//        return taskNodeRepository.findCompleteByConversationId(conversationId);
//    }
//
//    /**
//     * Load TaskNodeMaster with all its TaskNodes - optimized for conversation overview
//     */
//    public Optional<TaskNodeMaster> loadMasterWithTaskNodes(String conversationId) {
//        log.debug("Loading TaskNodeMaster with task nodes for conversation: {}", conversationId);
//        
//        // Single query loads TaskNodeMaster + taskNodes + rootTaskNode
//        return taskNodeMasterRepository.findWithTaskNodesByConversationId(conversationId);
//    }
//
//    /**
//     * Load complete conversation data - optimized for full conversation analysis
//     */
//    public Optional<TaskNodeMaster> loadCompleteConversation(String conversationId) {
//        log.debug("Loading complete conversation data for: {}", conversationId);
//        
//        // Single query loads TaskNodeMaster + taskNodes + rootTaskNode + goals + subTasks
//        return taskNodeMasterRepository.findCompleteByConversationId(conversationId);
//    }
//
//    /**
//     * Get all goals for a TaskNodeMaster with their execution details
//     */
//    public List<EngineGoal> loadGoalsWithExecutions(String masterNodeId) {
//        log.debug("Loading goals with executions for master node: {}", masterNodeId);
//        
//        // Single query loads EngineGoals + executions + agents for the entire conversation
//        return engineGoalRepository.findCompleteByTaskNodeMasterId(masterNodeId);
//    }
//
//    /**
//     * Get all executions for a conversation with full context
//     */
//    public List<EngineExecution> loadExecutionsForConversation(String masterNodeId) {
//        log.debug("Loading executions for conversation: {}", masterNodeId);
//        
//        // Single query loads EngineExecutions + agents + goals for the entire conversation
//        return engineExecutionRepository.findCompleteByTaskNodeMasterId(masterNodeId);
//    }
//
//    /**
//     * Performance comparison method - demonstrates the difference
//     */
//    public void demonstratePerformanceDifference(String conversationId) {
//        log.info("=== Performance Comparison Demo ===");
//        
//        long startTime = System.currentTimeMillis();
//        
//        // BAD: This will cause N+1 queries
//        TaskNode badExample = taskNodeRepository.findByConversationId(conversationId);
//        if (badExample != null) {
//            // These accesses trigger lazy loading - separate queries for each relationship
//            int subTaskCount = badExample.getSubTasks().size(); // Query 1
//            int goalCount = badExample.getGoals().size(); // Query 2 + N queries for each goal's executions
//            log.warn("BAD: Loaded with {} subtasks, {} goals using N+1 queries", subTaskCount, goalCount);
//        }
//        
//        long badTime = System.currentTimeMillis() - startTime;
//        
//        startTime = System.currentTimeMillis();
//        
//        // GOOD: Single optimized query
//        Optional<TaskNode> goodExample = taskNodeRepository.findCompleteByConversationId(conversationId);
//        if (goodExample.isPresent()) {
//            TaskNode node = goodExample.get();
//            // These accesses use already loaded data - no additional queries
//            int subTaskCount = node.getSubTasks().size();
//            int goalCount = node.getGoals().size();
//            log.info("GOOD: Loaded with {} subtasks, {} goals using single optimized query", subTaskCount, goalCount);
//        }
//        
//        long goodTime = System.currentTimeMillis() - startTime;
//        
//        log.info("Performance improvement: {}ms vs {}ms ({}x faster)", 
//                badTime, goodTime, badTime > 0 ? (double)badTime/goodTime : "N/A");
//    }
//
//    /**
//     * Best practice: Choose the right EntityGraph for your use case
//     */
//    public void demonstrateBestPractices() {
//        log.info("=== EntityGraph Best Practices ===");
//        log.info("1. Use TaskNode.withSubTasks for UI tree views");
//        log.info("2. Use TaskNode.withGoals for goal management screens");
//        log.info("3. Use TaskNode.complete for detailed task analysis (use sparingly)");
//        log.info("4. Use TaskNodeMaster.withTaskNodes for conversation overviews");
//        log.info("5. Use TaskNodeMaster.complete for full conversation analysis");
//        log.info("6. Always measure performance impact in your specific use cases");
//        log.info("7. Consider pagination for large datasets even with EntityGraphs");
//    }
} 