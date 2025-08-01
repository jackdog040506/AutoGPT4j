package com.james.autogpt.repository;

import com.james.autogpt.dto.scopes.ExecutionStatus;
import com.james.autogpt.model.EngineExecution;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EngineExecutionRepository extends JpaRepository<EngineExecution, String> {
    
    /**
     * Find executions by status
     */
    List<EngineExecution> findByStatus(ExecutionStatus status);
    
    /**
     * Find executions by agent ID
     */
    List<EngineExecution> findByAgentAgentId(String agentId);
    
    /**
     * Find executions by goal ID
     */
    List<EngineExecution> findByGoalId(String goalId);
    
    // EntityGraph optimized methods - we'll use ad-hoc graphs since EngineExecution relationships are simpler
    @EntityGraph(attributePaths = {"agent"})
    Optional<EngineExecution> findWithAgentById(String id);
    
    @EntityGraph(attributePaths = {"goal"})
    Optional<EngineExecution> findWithGoalById(String id);
    
    @EntityGraph(attributePaths = {"agent", "goal"})
    Optional<EngineExecution> findCompleteById(String id);
    
    @EntityGraph(attributePaths = {"agent"})
    List<EngineExecution> findWithAgentByStatus(ExecutionStatus status);
    
    @EntityGraph(attributePaths = {"goal"})
    List<EngineExecution> findWithGoalByStatus(ExecutionStatus status);
    
    @EntityGraph(attributePaths = {"agent", "goal"})
    List<EngineExecution> findCompleteByStatus(ExecutionStatus status);
    
    @EntityGraph(attributePaths = {"agent", "goal"})
    @Query("SELECT ee FROM EngineExecution ee WHERE ee.goal.taskNode.taskNodeMaster.id = :masterNodeId")
    List<EngineExecution> findCompleteByTaskNodeMasterId(@Param("masterNodeId") String masterNodeId);
} 