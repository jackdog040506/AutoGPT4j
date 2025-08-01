package com.james.autogpt.service;

import com.james.autogpt.dto.Result;
import com.james.autogpt.engine.ExecutorEngine;
import com.james.autogpt.engine.observers.BacktrackingObserver;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.repository.EngineExecutionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;

/**
 * High-level service for managing the AI agent execution engine
 * Provides interface for starting/stopping engine and queueing executions
 */
@Slf4j
@Service
public class EngineService {
    
    @Autowired
    private ExecutorEngine executorEngine;
    
    @Autowired
    private BacktrackingObserver backtrackingObserver;
    
    @Autowired
    private EngineExecutionRepository engineExecutionRepository;
    
    @PostConstruct
    public void initialize() {
        log.info("Initializing EngineService...");
        
        // Register observers
        executorEngine.addObserver(backtrackingObserver);
        
        // Start the engine
        executorEngine.start();
        
        log.info("EngineService initialized and started");
    }
    
    @PreDestroy
    public void shutdown() {
        log.info("Shutting down EngineService...");
        executorEngine.stop();
        log.info("EngineService shutdown complete");
    }
    
    /**
     * Queue an execution for processing
     */
    public Result<String> queueExecution(String executionId) {
        try {
            EngineExecution execution = engineExecutionRepository.findById(executionId).orElse(null);
            if (execution == null) {
                return Result.ofError(404, "Execution not found: " + executionId);
            }
            
            executorEngine.queueExecution(execution);
            
            log.info("Queued execution for processing: {}", executionId);
            return Result.ofSuccess(executionId, "Execution queued successfully");
            
        } catch (Exception e) {
            log.error("Error queueing execution: {}", executionId, e);
            return Result.ofError(500, "Failed to queue execution: " + e.getMessage());
        }
    }
    
    /**
     * Queue all pending executions for processing
     */
    // public Result<Integer> queueAllPendingExecutions() {
    //     try {
    //         List<EngineExecution> pendingExecutions = engineExecutionRepository.findByStatus(
    //             com.james.autogpt.dto.scopes.ExecutionStatus.STALLED
    //         );
            
    //         int queuedCount = 0;
    //         for (EngineExecution execution : pendingExecutions) {
    //             executorEngine.queueExecution(execution);
    //             queuedCount++;
    //         }
            
    //         log.info("Queued {} pending executions", queuedCount);
    //         return Result.ofSuccess(queuedCount, "Queued " + queuedCount + " pending executions");
            
    //     } catch (Exception e) {
    //         log.error("Error queueing pending executions", e);
    //         return Result.ofError(500, "Failed to queue pending executions: " + e.getMessage());
    //     }
    // }
    
    /**
     * Get engine status information
     */
    public Result<EngineStatus> getEngineStatus() {
        try {
            EngineStatus status = new EngineStatus();
            status.setRunning(executorEngine.isRunning());
            status.setQueueSize(executorEngine.getQueueSize());
            status.setActiveExecutions(executorEngine.getActiveExecutionsCount());
            
            return Result.ofSuccess(status, "Engine status retrieved");
            
        } catch (Exception e) {
            log.error("Error getting engine status", e);
            return Result.ofError(500, "Failed to get engine status: " + e.getMessage());
        }
    }
    
    /**
     * Restart the engine
     */
    public Result<String> restartEngine() {
        try {
            log.info("Restarting ExecutorEngine...");
            
            executorEngine.stop();
            Thread.sleep(1000); // Give time for graceful shutdown
            executorEngine.start();
            
            log.info("ExecutorEngine restarted successfully");
            return Result.ofSuccess("restarted", "Engine restarted successfully");
            
        } catch (Exception e) {
            log.error("Error restarting engine", e);
            return Result.ofError(500, "Failed to restart engine: " + e.getMessage());
        }
    }
    
    /**
     * Engine status information
     */
    public static class EngineStatus {
        private boolean running;
        private int queueSize;
        private int activeExecutions;
        
        // Getters and setters
        public boolean isRunning() { return running; }
        public void setRunning(boolean running) { this.running = running; }
        
        public int getQueueSize() { return queueSize; }
        public void setQueueSize(int queueSize) { this.queueSize = queueSize; }
        
        public int getActiveExecutions() { return activeExecutions; }
        public void setActiveExecutions(int activeExecutions) { this.activeExecutions = activeExecutions; }
    }
} 