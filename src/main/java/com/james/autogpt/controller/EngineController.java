package com.james.autogpt.controller;

import com.james.autogpt.dto.Result;
import com.james.autogpt.service.EngineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the AI agent execution engine
 * Provides endpoints for engine control and monitoring
 */
@Slf4j
@RestController
@RequestMapping("/api/engine")
public class EngineController {
    
    @Autowired
    private EngineService engineService;
    
    /**
     * Get engine status
     */
    @GetMapping("/status")
    public Result<EngineService.EngineStatus> getStatus() {
        log.debug("Getting engine status");
        return engineService.getEngineStatus();
    }
    
    /**
     * Queue a specific execution for processing
     */
    @PostMapping("/queue/{executionId}")
    public Result<String> queueExecution(@PathVariable String executionId) {
        log.info("Queueing execution: {}", executionId);
        return engineService.queueExecution(executionId);
    }
    
    /**
     * Queue all pending executions
     */
    // @PostMapping("/queue/all")
    // public Result<Integer> queueAllPending() {
    //     log.info("Queueing all pending executions");
    //     return engineService.queueAllPendingExecutions();
    // }
    
    /**
     * Restart the engine
     */
    @PostMapping("/restart")
    public Result<String> restartEngine() {
        log.info("Restarting engine via API");
        return engineService.restartEngine();
    }
} 