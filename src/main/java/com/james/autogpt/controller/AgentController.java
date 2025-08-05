package com.james.autogpt.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.agent.AgentResponse;
import com.james.autogpt.dto.agent.CreateAgentRequest;
import com.james.autogpt.dto.agent.UpdateAgentRequest;
import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.service.AgentService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/agents")
@Validated
@Slf4j
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * Create a new agent
     * @param request The request containing agent details
     * @return Result containing the created agent information
     */
    @PostMapping
    public ResponseEntity<Result<AgentResponse>> createAgent(
            @Valid @RequestBody CreateAgentRequest request) {

        log.info("Creating agent with ID: {}", request.getAgentId());
        Result<AgentResponse> result = agentService.createAgent(request);

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * Get agent by ID
     * @param agentId The agent ID
     * @return Result containing the agent information
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<Result<AgentResponse>> getAgentById(@PathVariable String agentId) {

        log.debug("Getting agent with ID: {}", agentId);
        Result<AgentResponse> result = agentService.getAgentById(agentId);

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * Get all agents
     * @return Result containing list of all agents
     */
    @GetMapping
    public ResponseEntity<Result<List<AgentResponse>>> getAllAgents() {

        log.debug("Getting all agents");
        Result<List<AgentResponse>> result = agentService.getAllAgents();

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * Get agents by type
     * @param agentType The agent type filter
     * @return Result containing list of agents of the specified type
     */
    @GetMapping("/type/{agentType}")
    public ResponseEntity<Result<List<AgentResponse>>> getAgentsByType(
            @PathVariable AgentType agentType) {

        log.debug("Getting agents by type: {}", agentType);
        Result<List<AgentResponse>> result = agentService.getAgentsByType(agentType);

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * Update agent
     * @param agentId The agent ID
     * @param request The request containing updated agent details
     * @return Result containing the updated agent information
     */
    @PutMapping("/{agentId}")
    public ResponseEntity<Result<AgentResponse>> updateAgent(
            @PathVariable String agentId,
            @Valid @RequestBody UpdateAgentRequest request) {

        log.info("Updating agent with ID: {}", agentId);
        Result<AgentResponse> result = agentService.updateAgent(agentId, request);

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }

    /**
     * Get all available agent types
     * @return Result containing list of all available agent types
     */
    @GetMapping("/types")
    public ResponseEntity<Result<AgentType[]>> getAgentTypes() {

        log.debug("Getting all available agent types");
        AgentType[] agentTypes = AgentType.values();
        Result<AgentType[]> result = Result.ofSuccess(agentTypes);

        return ResponseEntity.ok(result);
    }

    /**
     * Delete agent
     * @param agentId The agent ID
     * @return Result containing deletion status
     */
    @DeleteMapping("/{agentId}")
    public ResponseEntity<Result<String>> deleteAgent(@PathVariable String agentId) {

        log.info("Deleting agent with ID: {}", agentId);
        Result<String> result = agentService.deleteAgent(agentId);

        if (result.isOk()) {
            return ResponseEntity.ok(result);
        }
		return ResponseEntity.status(result.getCode()).body(result);
    }
}