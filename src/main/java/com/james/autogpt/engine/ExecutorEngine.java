package com.james.autogpt.engine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.james.autogpt.dto.scopes.ExecutionStatus;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.repository.EngineExecutionRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Main ExecutorEngine that processes EngineExecution instances in a priority queue
 * with support for aggregation, backtracking (observer pattern), and dynamic priority reordering
 *
 * Based on Graph of Thought reasoning paradigm for AI agent processing
 */
@Slf4j
@Component
public class ExecutorEngine {

    private final PriorityBlockingQueue<ExecutionQueueItem> executionQueue;
    private final ExecutorService executorService;
    private final AtomicBoolean isRunning;
    private final Map<String, EngineExecution> activeExecutions;
    private final List<ExecutionObserver> observers;

    @Autowired
    private EngineExecutionFactory executionFactory;

    @Autowired
    private EngineExecutionRepository engineExecutionRepository;

    @Autowired
    private PriorityManager priorityManager;

    public ExecutorEngine() {
        // Priority queue ordered by priority (lower number = higher priority)
        this.executionQueue = new PriorityBlockingQueue<>(100,
            Comparator.comparing(ExecutionQueueItem::getPriority));
        this.executorService = Executors.newCachedThreadPool();
        this.isRunning = new AtomicBoolean(false);
        this.activeExecutions = new ConcurrentHashMap<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Starts the engine processing loop
     */
    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            log.info("Starting ExecutorEngine...");
            executorService.submit(this::processExecutionLoop);
        }
    }

    /**
     * Stops the engine gracefully
     */
    public void stop() {
        if (isRunning.compareAndSet(true, false)) {
            log.info("Stopping ExecutorEngine...");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Queue an EngineExecution for processing
     */
    public void queueExecution(EngineExecution execution) {
        if (execution == null || execution.getGoal() == null || execution.getGoal().getTaskNode() == null) {
            log.warn("Invalid execution queued - missing required fields");
            return;
        }

        ExecutionQueueItem item = new ExecutionQueueItem(
            execution,
            execution.getGoal().getTaskNode().getPriority(),
            System.currentTimeMillis()
        );

        executionQueue.offer(item);
        log.debug("Queued execution {} with priority {}",
            execution.getId(), execution.getGoal().getTaskNode().getPriority());
    }

    /**
     * Reorder queue based on updated priorities (for backtracking and refinement)
     */
    public void reorderQueue() {
        log.debug("Reordering execution queue based on updated priorities");
        priorityManager.reorderQueue(executionQueue);
    }

    /**
     * Add observer for backtracking capabilities
     */
    public void addObserver(ExecutionObserver observer) {
        observers.add(observer);
    }

    /**
     * Remove observer
     */
    public void removeObserver(ExecutionObserver observer) {
        observers.remove(observer);
    }

    /**
     * Main processing loop
     */
    private void processExecutionLoop() {
        while (isRunning.get()) {
            try {
                ExecutionQueueItem item = executionQueue.poll(1, TimeUnit.SECONDS);
                if (item != null) {
                    processExecution(item.getExecution());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in execution processing loop", e);
            }
        }
        log.info("ExecutorEngine processing loop terminated");
    }

    /**
     * Process a single EngineExecution
     */
    private void processExecution(EngineExecution execution) {
        String executionId = execution.getId();

        try {
            log.debug("Processing execution: {}", executionId);

            // Reload execution with all associations to avoid lazy loading issues
            EngineExecution reloadedExecution = engineExecutionRepository.findWithAllAssociationsById(executionId)
                .orElse(execution);

            activeExecutions.put(executionId, reloadedExecution);

            // Update status to running and save
            reloadedExecution.setStatus(ExecutionStatus.RUNNING);
            engineExecutionRepository.save(reloadedExecution);

            // Notify observers (for backtracking)
            notifyObservers(ExecutionEvent.STARTED, reloadedExecution);

            // Process through factory based on agent type
            ExecutionResult result = executionFactory.processExecution(reloadedExecution);

            // Handle result
            handleExecutionResult(reloadedExecution, result);

        } catch (Exception e) {
            log.error("Error processing execution: {}", executionId, e);
            execution.setStatus(ExecutionStatus.FAILED);
            engineExecutionRepository.save(execution);
            notifyObservers(ExecutionEvent.FAILED, execution);
        } finally {
            activeExecutions.remove(executionId);
        }
    }

    /**
     * Handle the result of execution processing
     */
    private void handleExecutionResult(EngineExecution execution, ExecutionResult result) {
        switch (result.getStatus()) {
            case COMPLETED:
                execution.setStatus(ExecutionStatus.COMPLETED);
                engineExecutionRepository.save(execution);
                notifyObservers(ExecutionEvent.COMPLETED, execution);
                break;

            case FAILED:
                execution.setStatus(ExecutionStatus.FAILED);
                engineExecutionRepository.save(execution);
                notifyObservers(ExecutionEvent.FAILED, execution);
                break;

            case REQUIRES_REQUEUE:
                // For refinement - requeue with updated priority
                execution.setStatus(ExecutionStatus.STALLED);
                engineExecutionRepository.save(execution);
                queueExecution(execution);
                reorderQueue();
                notifyObservers(ExecutionEvent.REQUEUED, execution);
                break;

            case SPAWNED_NEW_TASKS:
                // For branching - new tasks already queued by factory
                execution.setStatus(ExecutionStatus.COMPLETED);
                engineExecutionRepository.save(execution);
                notifyObservers(ExecutionEvent.COMPLETED, execution);
                break;
        }
    }

    /**
     * Notify all observers of execution events
     */
    private void notifyObservers(ExecutionEvent event, EngineExecution execution) {
        for (ExecutionObserver observer : observers) {
            try {
                observer.onExecutionEvent(event, execution);
            } catch (Exception e) {
                log.error("Error notifying observer", e);
            }
        }
    }

    /**
     * Get current queue size for monitoring
     */
    public int getQueueSize() {
        return executionQueue.size();
    }

    /**
     * Get active executions count
     */
    public int getActiveExecutionsCount() {
        return activeExecutions.size();
    }

    /**
     * Check if engine is running
     */
    public boolean isRunning() {
        return isRunning.get();
    }
}