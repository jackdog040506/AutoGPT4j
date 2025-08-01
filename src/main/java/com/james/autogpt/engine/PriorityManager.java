package com.james.autogpt.engine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Manages priority reordering for the execution queue
 * Supports dynamic priority updates for backtracking and refinement operations
 */
@Slf4j
@Component
public class PriorityManager {
    
    /**
     * Reorder the queue based on updated priorities
     * This is called when backtracking or refinement requires priority changes
     */
    public void reorderQueue(PriorityBlockingQueue<ExecutionQueueItem> queue) {
        if (queue.isEmpty()) {
            return;
        }
        
        log.debug("Reordering queue with {} items", queue.size());
        
        // Extract all items from queue
        List<ExecutionQueueItem> items = new ArrayList<>();
        ExecutionQueueItem item;
        while ((item = queue.poll()) != null) {
            items.add(item);
        }
        
        // Update priorities based on current TaskNode priorities
        items.forEach(this::updateItemPriority);
        
        // Sort by priority (lower number = higher priority)
        items.sort(Comparator.comparing(ExecutionQueueItem::getPriority)
                   .thenComparing(ExecutionQueueItem::getQueuedAt));
        
        // Put items back in queue
        items.forEach(queue::offer);
        
        log.debug("Queue reordered successfully");
    }
    
    /**
     * Update the priority of a queue item based on current TaskNode priority
     */
    private void updateItemPriority(ExecutionQueueItem item) {
        if (item.getExecution() != null && 
            item.getExecution().getGoal() != null && 
            item.getExecution().getGoal().getTaskNode() != null) {
            
            Integer currentPriority = item.getExecution().getGoal().getTaskNode().getPriority();
            if (currentPriority != null && !currentPriority.equals(item.getPriority())) {
                log.debug("Updated priority for execution {} from {} to {}", 
                    item.getExecution().getId(), item.getPriority(), currentPriority);
                
                // Create new item with updated priority (since ExecutionQueueItem is immutable)
                ExecutionQueueItem updatedItem = new ExecutionQueueItem(
                    item.getExecution(),
                    currentPriority,
                    item.getQueuedAt()
                );
                
                // Note: The queue will be rebuilt in reorderQueue method
            }
        }
    }
    
    /**
     * Calculate new priority for refinement operations
     * Typically increases priority (lower number) to process refined tasks sooner
     */
    public Integer calculateRefinementPriority(Integer originalPriority) {
        if (originalPriority == null || originalPriority <= 1) {
            return 1; // Highest priority
        }
        return originalPriority - 1; // Increase priority by 1 level
    }
    
    /**
     * Calculate new priority for branched tasks
     * Typically inherits parent priority or slightly lower
     */
    public Integer calculateBranchPriority(Integer parentPriority) {
        if (parentPriority == null) {
            return 2; // Default priority for branches
        }
        return parentPriority + 1; // Slightly lower priority than parent
    }
} 