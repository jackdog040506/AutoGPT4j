package com.james.autogpt.service;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.dto.scopes.EngineGoalStatus;
import com.james.autogpt.dto.scopes.ExecutionStatus;
import com.james.autogpt.dto.task.CreateAIPlannerTaskRequest;
import com.james.autogpt.dto.task.CreateTaskRequest;
import com.james.autogpt.dto.task.TaskResponse;
import com.james.autogpt.model.Agent;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.model.EngineGoal;
import com.james.autogpt.model.TaskNode;
import com.james.autogpt.model.TaskNodeMaster;
import com.james.autogpt.repository.AgentRepository;
import com.james.autogpt.repository.EngineExecutionRepository;
import com.james.autogpt.repository.EngineGoalRepository;
import com.james.autogpt.repository.TaskNodeMasterRepository;
import com.james.autogpt.repository.TaskNodeRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskNodeMasterRepository taskNodeMasterRepository;
    
    @Autowired
    private TaskNodeRepository taskNodeRepository;
    
    @Autowired
    private EngineGoalRepository engineGoalRepository;
    
    @Autowired
    private EngineExecutionRepository engineExecutionRepository;
    
    @Autowired
    private AgentRepository agentRepository;

    @Override
    public Result<TaskResponse> createMasterNodeWithAIPlannerGoal(CreateAIPlannerTaskRequest request) {
        
        try {
            // Find AIPlanner agent
            Agent aiPlannerAgent = agentRepository.findByAgentType(AgentType.AIPlanner);
            if (aiPlannerAgent == null) {
                log.error("No AIPlanner agent found in the system");
                return Result.ofError(500, "No AIPlanner agent found in the system");
            }
            
            // Create a generic request with the AIPlanner agent
            CreateTaskRequest taskRequest = new CreateTaskRequest();
            taskRequest.setName(request.getName());
            taskRequest.setMasterPrompt(request.getMasterPrompt());
            taskRequest.setRootPrompt(request.getRootPrompt());
            taskRequest.setAgentId(aiPlannerAgent.getAgentId());
            
            return createMasterNodeWithGoal(taskRequest);
            
        } catch (Exception e) {
            log.error("Error creating master node with AIPlanner goal", e);
            return Result.ofError(500, "Failed to create task with AIPlanner: " + e.getMessage());
        }
    }

    @Override
    public Result<TaskResponse> createMasterNodeWithGoal(CreateTaskRequest request) {
        log.info("Creating master node with goal for conversation: {} with agent: {}", request.getAgentId());
        
        try {
            String agentId = request.getAgentId();
            
            // If no agent ID provided, find AIPlanner agent
            if (!StringUtils.hasText(agentId)) {
                Agent aiPlannerAgent = agentRepository.findByAgentType(AgentType.AIPlanner);
                if (aiPlannerAgent == null) {
                    log.error("No AIPlanner agent found in the system");
                    return Result.ofError(500, "No AIPlanner agent found in the system");
                }
                agentId = aiPlannerAgent.getAgentId();
            }
            
            // Find the agent
            Agent agent = agentRepository.findByAgentId(agentId);
            if (agent == null) {
                log.error("Agent not found with ID: {}", agentId);
                return Result.ofError(404, "Agent not found with ID: " + agentId);
            }
            
            // Create TaskNodeMaster
            TaskNodeMaster masterNode = new TaskNodeMaster();
            masterNode.setName(request.getName());
            
            // Create root TaskNode
            TaskNode rootTaskNode = new TaskNode();
            rootTaskNode.setPriority(1); // High priority for root tasks
            rootTaskNode.setPrompt(request.getRootPrompt());
            rootTaskNode.setTaskNodeMaster(masterNode);
            
            // Create EngineGoal
            EngineGoal engineGoal = new EngineGoal();
            engineGoal.setName(request.getName());
            engineGoal.setDescription(request.getDescription());
            engineGoal.setPriority(1);
            engineGoal.setStatus(EngineGoalStatus.PENDING);
            engineGoal.setTaskNode(rootTaskNode);
            
            // Create EngineExecution
            EngineExecution engineExecution = new EngineExecution();
            engineExecution.setAgent(agent);
            engineExecution.setStatus(ExecutionStatus.STALLED);
            engineExecution.setGoal(engineGoal);
            
            // Set up execution configuration
            engineExecution.setConfig(new HashMap<>());
            
            // Establish relationships
            engineGoal.addExecution(engineExecution);
            rootTaskNode.getGoals().add(engineGoal);
            masterNode.setRootTaskNode(rootTaskNode);
            masterNode.addTaskNode(rootTaskNode);
            
            // Save all entities
            TaskNodeMaster savedMasterNode = taskNodeMasterRepository.save(masterNode);
            TaskNode savedRootTaskNode = taskNodeRepository.save(rootTaskNode);
            EngineGoal savedEngineGoal = engineGoalRepository.save(engineGoal);
            EngineExecution savedEngineExecution = engineExecutionRepository.save(engineExecution);
            
            // Create response
            TaskResponse response = TaskResponse.fromTaskNodeMaster(
                savedMasterNode.getId(),
                savedRootTaskNode.getId(),
                savedEngineGoal.getId(),
                savedEngineExecution.getId(),
                savedMasterNode.getConversationId(),
                request.getName(),
                request.getDescription(),
                agent.getAgentId(),
                agent.getAgentType().toString(),
                savedMasterNode.getDateCreated()
            );
            
            log.info("Successfully created master node with goal. Master ID: {}, Root Task ID: {}, Goal ID: {}, Execution ID: {}", 
                    savedMasterNode.getId(), savedRootTaskNode.getId(), savedEngineGoal.getId(), savedEngineExecution.getId());
            
            return Result.ofSuccess(response, "Task created successfully");
            
        } catch (Exception e) {
            log.error("Error creating master node with goal", e);
            return Result.ofError(500, "Failed to create task: " + e.getMessage());
        }
    }
} 