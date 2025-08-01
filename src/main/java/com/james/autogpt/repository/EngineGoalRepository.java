package com.james.autogpt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.james.autogpt.dto.scopes.EngineGoalStatus;
import com.james.autogpt.model.EngineGoal;

public interface EngineGoalRepository extends JpaRepository<EngineGoal, String> {
    
    // EntityGraph optimized methods
    @EntityGraph("EngineGoal.withExecutions")
    Optional<EngineGoal> findWithExecutionsById(String id);
    
    @EntityGraph("EngineGoal.withTaskNode")
    Optional<EngineGoal> findWithTaskNodeById(String id);
    
    @EntityGraph("EngineGoal.complete")
    Optional<EngineGoal> findCompleteById(String id);
    
    @EntityGraph("EngineGoal.withExecutions")
    List<EngineGoal> findWithExecutionsByStatus(EngineGoalStatus status);
    
    @EntityGraph("EngineGoal.withTaskNode")
    @Query("SELECT eg FROM EngineGoal eg WHERE eg.taskNode.id = :taskNodeId")
    List<EngineGoal> findWithTaskNodeByTaskNodeId(@Param("taskNodeId") String taskNodeId);
    
    @EntityGraph("EngineGoal.complete")
    @Query("SELECT eg FROM EngineGoal eg WHERE eg.taskNode.taskNodeMaster.id = :masterNodeId")
    List<EngineGoal> findCompleteByTaskNodeMasterId(@Param("masterNodeId") String masterNodeId);
    
    @EntityGraph("EngineGoal.withExecutions")
    List<EngineGoal> findWithExecutionsByPriorityLessThanEqual(Integer priority);
} 