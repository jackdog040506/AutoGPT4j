package com.james.autogpt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.task.CreateAIPlannerTaskRequest;
import com.james.autogpt.dto.task.TaskResponse;
import com.james.autogpt.service.TaskService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tasks")
@Validated
@Slf4j
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Create a task with AIPlanner agent
     * @param request The request containing conversation ID, name, and description
     * @return Result containing the created task information
     */
    @PostMapping("/ai-planner")
    public ResponseEntity<Result<TaskResponse>> createAIPlannerTask(
            @Valid @RequestBody CreateAIPlannerTaskRequest request) {
        
        Result<TaskResponse> result = taskService.createMasterNodeWithAIPlannerGoal(request);
        
        if (result.isOk()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.getCode()).body(result);
        }
    }

//    /**
//     * Create a task with a specific agent
//     * @param request The request containing conversation ID, name, description, and optional agent ID
//     * @return Result containing the created task information
//     */
//    @PostMapping("/create")
//    public ResponseEntity<Result<TaskResponse>> createTask(
//            @Valid @RequestBody CreateTaskRequest request) {
//        log.info("Received request to create task: {}", request.getConversationId());
//        
//        Result<TaskResponse> result = taskService.createMasterNodeWithGoal(request);
//        
//        if (result.isOk()) {
//            return ResponseEntity.ok(result);
//        } else {
//            return ResponseEntity.status(result.getCode()).body(result);
//        }
//    }
} 