package com.james.autogpt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.james.autogpt.model.TaskNode;

public interface TaskNodeRepository extends JpaRepository<TaskNode, String> {
    
    TaskNode findByConversationId(String conversationId);
    
    // EntityGraph optimized methods
    @EntityGraph("TaskNode.withSubTasks")
    Optional<TaskNode> findWithSubTasksByConversationId(String conversationId);
    
    @EntityGraph("TaskNode.withGoals")
    Optional<TaskNode> findWithGoalsByConversationId(String conversationId);
    
    @EntityGraph("TaskNode.complete")
    Optional<TaskNode> findCompleteByConversationId(String conversationId);
    
    @EntityGraph("TaskNode.withSubTasks")
    List<TaskNode> findWithSubTasksByParentTaskId(String parentTaskId);
    
    @EntityGraph("TaskNode.withGoals")
    @Query("SELECT tn FROM TaskNode tn WHERE tn.taskNodeMaster.id = :masterNodeId")
    List<TaskNode> findWithGoalsByTaskNodeMasterId(@Param("masterNodeId") String masterNodeId);
    
    @EntityGraph("TaskNode.complete")
    @Query("SELECT tn FROM TaskNode tn WHERE tn.taskNodeMaster.id = :masterNodeId")
    List<TaskNode> findCompleteByTaskNodeMasterId(@Param("masterNodeId") String masterNodeId);
} 