package com.james.autogpt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.james.autogpt.model.TaskNodeMaster;

public interface TaskNodeMasterRepository extends JpaRepository<TaskNodeMaster, String> {
    
    TaskNodeMaster findByConversationId(String conversationId);
    
//    // EntityGraph optimized methods
//    @EntityGraph("TaskNodeMaster.withTaskNodes")
//    Optional<TaskNodeMaster> findWithTaskNodesByConversationId(String conversationId);
//    
//    @EntityGraph("TaskNodeMaster.complete")
//    Optional<TaskNodeMaster> findCompleteByConversationId(String conversationId);
//    
//    @EntityGraph("TaskNodeMaster.withTaskNodes")
//    Optional<TaskNodeMaster> findWithTaskNodesById(String id);
//    
//    @EntityGraph("TaskNodeMaster.complete")
//    Optional<TaskNodeMaster> findCompleteById(String id);
//    
//    @EntityGraph("TaskNodeMaster.withTaskNodes")
//    List<TaskNodeMaster> findAllWithTaskNodes();
} 