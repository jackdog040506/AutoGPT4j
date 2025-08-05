package com.james.autogpt.engine.processors;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.james.autogpt.dto.PlanGoal;
import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.dto.scopes.EngineGoalStatus;
import com.james.autogpt.dto.scopes.ExecutionStatus;
import com.james.autogpt.dto.scopes.ReasoningAction;
import com.james.autogpt.engine.ExecutionResult;
import com.james.autogpt.model.Agent;
import com.james.autogpt.model.EngineExecution;
import com.james.autogpt.model.EngineGoal;
import com.james.autogpt.model.TaskNode;
import com.james.autogpt.repository.AgentRepository;
import com.james.autogpt.repository.EngineExecutionRepository;
import com.james.autogpt.repository.EngineGoalRepository;
import com.james.autogpt.service.TaskService;
import com.james.autogpt.utils.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AIThinkingProcessor - The minimum unit of large task tree
 * Implements the complete Agentic Loop: "Think → Reason → Plan → Criticize →
 * Act (Command) → Speak"
 *
 * This processor implements the Evaluator-Optimizer pattern from Spring AI:
 * - Generator LLM: Produces initial responses and refines them based on
 * feedback
 * - Evaluator LLM: Analyzes responses and provides detailed feedback for
 * improvement
 *
 * Based on Spring AI's agentic patterns:
 * https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class AIThinkingProcessor implements ExecutionProcessor {

    private ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final ChatMemoryRepository chatMemoryRepository;
    private final ChatMemory chatMemory;
    private final TaskService taskService;
    private final AgentRepository agentRepository;
    private final EngineExecutionRepository engineExecutionRepository;
    private final EngineGoalRepository engineGoalRepository;

    @Autowired
    public void setChatClientBuilder(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @Override
    public ExecutionResult process(EngineExecution execution) {
        log.debug("Starting AIThinking agentic loop for execution: {}", execution.getId());

        try {
            // Execute the complete agentic loop with Evaluator-Optimizer pattern
            String thinkResult = think(execution);
            String reasonResult = reason(execution, thinkResult);
            ResponseEntity<ChatResponse, List<PlanGoal>> planResult = plan(execution, thinkResult, reasonResult);
            // String criticizeResult = criticize(execution, planResult);
            // String actResult = act(execution, criticizeResult);
            // String speakResult = speak(execution, thinkResult, reasonResult, planResult,
            // criticizeResult, actResult);
            List<PlanGoal> planGoals = planResult.getEntity();

            // Convert planGoals into EngineGoal and add to TaskNode
            if (CollectionUtils.isNotEmpty(planGoals)) {
                convertAndAddPlanGoalsToTaskNode(execution, planGoals);
            }

            // Update goal status and save
            execution.getGoal().setStatus(EngineGoalStatus.COMPLETED);
            engineGoalRepository.save(execution.getGoal());

            log.info("AIThinking agentic loop completed for execution: {}", execution.getId());
            return ExecutionResult
                    .completed("AIThinking agentic loop completed successfully. Final result: ");

        } catch (Exception e) {
            log.error("Error in AIThinking agentic loop for execution: {}", execution.getId(), e);
            execution.getGoal().setStatus(EngineGoalStatus.FAILED);
            engineGoalRepository.save(execution.getGoal());
            return ExecutionResult.failed("AIThinking agentic loop error: " + e.getMessage());
        }
    }

    /**
     * 🔹 Think: Collect and concentrate sources of truth to prepare for reasoning
     * Sources: Core Prompt, Agent Role, Tree of Goal (only AIThinking nodes)
     *
     * Implements Generator LLM pattern with iterative refinement
     */
    private String think(EngineExecution execution) {
        log.debug("Executing THINK phase for execution: {}", execution.getId());

        try {
            // Collect sources of truth
            String agentRole = getAgentRole(execution);
            String goalTree = getGoalTree(execution);

            // Build think prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/think.yaml"));
            String thinkPrompt = promptTemplate.create(Map.of(
                    "agentRole", agentRole,
                    "goalTree", goalTree,
                    "taskContext", execution.getGoal().getTaskNode().getPrompt())).getContents();

            String thinkResult = chatClient.prompt(Constants.AGENTIC_CYCLE_THINK_CORE_PROMPT).user(thinkPrompt)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID,
                            execution.getGoal().getTaskNode().getConversationId()))
                    .call()
                    .content();

            log.debug("THINK result: {}", thinkResult);
            return thinkResult;

        } catch (Exception e) {
            log.error("Error in THINK phase", e);
            throw new RuntimeException("THINK phase failed", e);
        }
    }

    /**
     * 🔹 Reason: Generate a focused breakdown of the current subgoal
     * Sources: Core Prompt, Current Node Goals (filtered by AIThinking)
     *
     * Implements Generator LLM pattern with iterative refinement
     */
    private String reason(EngineExecution execution, String thinkResult) {
        log.debug("Executing REASON phase for execution: {}", execution.getId());

        try {
            String currentGoals = getCurrentNodeGoals(execution);

            // Build reason prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/reason.yaml"));
            String reasonPrompt = promptTemplate.create(Map.of(
                    "thinkingResult", thinkResult,
                    "currentGoals", currentGoals)).getContents();

            String reasonResult = chatClient.prompt(Constants.AGENTIC_CYCLE_REASON_CORE_PROMPT).user(reasonPrompt)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID,
                            execution.getGoal().getTaskNode().getConversationId()))
                    .call()
                    .content();

            log.debug("REASON result: {}", reasonResult);
            return reasonResult;

        } catch (Exception e) {
            log.error("Error in REASON phase", e);
            throw new RuntimeException("REASON phase failed", e);
        }
    }

    /**
     * 🔹 Plan: Decide on subgoals or the next goal for the current node
     * Sources: Core Prompt, Result of Think & Reason, Agent Options
     *
     * Implements Generator LLM pattern with iterative refinement
     */
    private ResponseEntity<ChatResponse, List<PlanGoal>> plan(EngineExecution execution, String thinkResult,
            String reasonResult) {
        log.debug("Executing PLAN phase for execution: {}", execution.getId());

        try {
            String agentOptions = getAgentOptions(execution);

            // Build plan prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/plan.yaml"));
            String planPrompt = promptTemplate.create(Map.of(
                    "thinkingResult", thinkResult,
                    "reasoningResult", reasonResult,
                    "agentOptions", agentOptions)).getContents();

            ResponseEntity<ChatResponse, List<PlanGoal>> planResult = chatClient
                    .prompt(Constants.AGENTIC_CYCLE_PLAN_CORE_PROMPT)
                    .user(planPrompt)
                    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID,
                            execution.getGoal().getTaskNode().getConversationId()))
                    .call()
                    .responseEntity(new ParameterizedTypeReference<List<PlanGoal>>() {
                    });

            log.debug("REASON result: {}", planResult);
            return planResult;

        } catch (Exception e) {
            log.error("Error in PLAN phase", e);
            throw new RuntimeException("PLAN phase failed", e);
        }
    }

    /**
     * 🔹 Criticize: Evaluate plans and provide constructive alternatives
     * Sources: RAG Result, Core Prompt
     *
     * Implements Evaluator LLM pattern - this is the evaluator component
     */
    private String criticize(EngineExecution execution, String planResult) {
        log.debug("Executing CRITICIZE phase for execution: {}", execution.getId());

        try {
            String ragResult = getRAGResult(execution);

            // Build criticize prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/criticize.yaml"));
            String criticizePrompt = promptTemplate.create(Map.of(
                    "planResult", planResult,
                    "ragResult", ragResult)).getContents();

            // Execute criticism with Evaluator-Optimizer pattern
            String criticizeResult = executeWithEvaluation(criticizePrompt, "plan evaluation", 2);

            log.debug("CRITICIZE result: {}", criticizeResult);
            return criticizeResult;

        } catch (Exception e) {
            log.error("Error in CRITICIZE phase", e);
            throw new RuntimeException("CRITICIZE phase failed", e);
        }
    }

    /**
     * 🔹 Act/Command: Transform final decisions into actionable goals
     * Sources: Core Prompt
     *
     * Implements Generator LLM pattern with iterative refinement
     */
    private String act(EngineExecution execution, String criticizeResult) {
        log.debug("Executing ACT phase for execution: {}", execution.getId());

        try {

            // Build act prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/act.yaml"));
            String actPrompt = promptTemplate.create(Map.of(
                    "criticizeResult", criticizeResult)).getContents();

            // Execute action planning with Evaluator-Optimizer pattern
            String actResult = executeWithEvaluation(actPrompt, "actionable goals", 3);

            log.debug("ACT result: {}", actResult);
            return actResult;

        } catch (Exception e) {
            log.error("Error in ACT phase", e);
            throw new RuntimeException("ACT phase failed", e);
        }
    }

    /**
     * 🔹 Speak: Summarize the process and store in vector DB
     * Vector Store Metadata: master_id, node priority
     */
    private String speak(EngineExecution execution, String thinkResult, String reasonResult,
            String planResult, String criticizeResult, String actResult) {
        log.debug("Executing SPEAK phase for execution: {}", execution.getId());

        try {
            // Build speak prompt using PromptTemplate
            PromptTemplate promptTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/speak.yaml"));
            String summary = promptTemplate.create(Map.of(
                    "thinkResult", thinkResult,
                    "reasonResult", reasonResult,
                    "planResult", planResult,
                    "criticizeResult", criticizeResult,
                    "actResult", actResult)).getContents();

            // Store in vector database with metadata
            TaskNode taskNode = execution.getGoal().getTaskNode();
            Map<String, Object> metadata = Map.of(
                    "master_id", taskNode.getTaskNodeMaster().getId(),
                    "node_priority", taskNode.getPriority(),
                    "execution_id", execution.getId(),
                    "agent_type", "AIThinking",
                    "conversation_id", taskNode.getConversationId());

            // Store the summary in vector database
            org.springframework.ai.document.Document doc = new org.springframework.ai.document.Document(summary,
                    metadata);
            vectorStore.add(List.of(doc));

            log.debug("SPEAK result stored in vector database with metadata: {}", metadata);
            return summary;

        } catch (Exception e) {
            log.error("Error in SPEAK phase", e);
            throw new RuntimeException("SPEAK phase failed", e);
        }
    }

    /**
     * Implements the Evaluator-Optimizer pattern from Spring AI
     * Generator LLM produces responses while Evaluator LLM provides feedback in
     * iterative loop
     *
     * @param prompt        The initial prompt for generation
     * @param taskType      The type of task for evaluation criteria
     * @param maxIterations Maximum number of refinement iterations
     * @return The refined response
     */
    private String executeWithEvaluation(String prompt, String taskType, int maxIterations) {
        try {
            // 1. Generate initial solution
            String currentResponse = generateResponse(prompt);

            // 2. Evaluate and refine in iterative loop
            for (int iteration = 1; iteration <= maxIterations; iteration++) {
                log.debug("Evaluator-Optimizer iteration {} for task: {}", iteration, taskType);

                // Evaluate the current response
                EvaluationResult evaluation = evaluateResponse(currentResponse, prompt, taskType);

                // If evaluation passes, return the solution
                if (evaluation.pass()) {
                    log.debug("Evaluation PASSED on iteration {} for task: {}", iteration, taskType);
                    return currentResponse;
                }

                // If needs improvement, generate refined solution
                if (iteration < maxIterations) {
                    // Build refinement prompt using PromptTemplate
                    PromptTemplate refinementTemplate = new PromptTemplate(
                            new ClassPathResource("templates/agentic-step/refinement.yaml"));
                    String refinementPrompt = refinementTemplate.create(Map.of(
                            "originalPrompt", prompt,
                            "currentResponse", currentResponse,
                            "feedback", evaluation.feedback())).getContents();

                    currentResponse = generateResponse(refinementPrompt);
                }
            }

            log.debug("Reached maximum iterations ({}) for task: {}", maxIterations, taskType);
            return currentResponse;

        } catch (Exception e) {
            log.warn("Error in Evaluator-Optimizer pattern, falling back to simple generation", e);
            return generateResponse(prompt);
        }
    }

    /**
     * Generator LLM: Produces responses using ChatClient
     */
    private String generateResponse(String prompt) {
        try {
            // Simplified implementation - in production, this would use actual ChatClient
            log.debug("Generating response for prompt: {}", prompt);
            return "Generated response for: " + prompt.substring(0, Math.min(50, prompt.length())) + "...";
        } catch (Exception e) {
            log.error("Error generating response", e);
            return "Error generating response: " + e.getMessage();
        }
    }

    /**
     * Evaluator LLM: Analyzes responses and provides feedback
     */
    private EvaluationResult evaluateResponse(String response, String originalPrompt, String taskType) {
        try {
            // Build evaluation prompt using PromptTemplate
            PromptTemplate evaluationTemplate = new PromptTemplate(
                    new ClassPathResource("templates/agentic-step/evaluation.yaml"));
            String evaluationPrompt = evaluationTemplate.create(Map.of(
                    "taskType", taskType,
                    "originalPrompt", originalPrompt,
                    "response", response)).getContents();

            // Simplified evaluation - in production, this would use actual ChatClient
            log.debug("Evaluating response for task type: {}", taskType);

            // Simple evaluation logic based on response length and content
            boolean pass = response.length() > 20 && !response.contains("Error");
            String feedback = pass ? "Response meets quality criteria"
                    : "Response needs improvement: too short or contains errors";

            return new EvaluationResult(pass, feedback);

        } catch (Exception e) {
            log.error("Error evaluating response", e);
            return new EvaluationResult(true, "Evaluation failed, defaulting to pass");
        }
    }

    private String getAgentRole(EngineExecution execution) {
        Agent agent = execution.getAgent();
        return agent != null ? agent.getRoles() : "No specific role defined";
    }

    private String getGoalTree(EngineExecution execution) {
        try {
            TaskNode currentNode = execution.getGoal().getTaskNode();
            StringBuilder goalTree = new StringBuilder();

            // Traverse up the parent chain to build goal tree
            TaskNode current = currentNode;
            while (current != null) {
                if (current.getGoals() != null) {
                    try {
                        String aiThinkingGoals = current.getGoals().stream()
                                .filter(goal -> goal.getExecutions() != null)
                                .flatMap(goal -> goal.getExecutions().stream())
                                .filter(exec -> exec.getAgent() != null &&
                                        exec.getAgent().getAgentType() == AgentType.AIThinking)
                                .map(exec -> {
                                    if (exec.getGoal().getStatus() == EngineGoalStatus.COMPLETED) {
                                        return String.format("(Legacy) Goal: %s, Description: %s", exec.getGoal().getName(),
                                                exec.getGoal().getDescription());
                                    }
									return String.format("(Current) Goal: %s, Description: %s", exec.getGoal().getName(),
									        exec.getGoal().getDescription());
                                })
                                .collect(Collectors.joining("\n"));

                        if (!aiThinkingGoals.isEmpty()) {
                            goalTree.insert(0, String.format("Node '%s': %s\n", current.getName(), aiThinkingGoals));
                        }
                    } catch (Exception e) {
                        log.warn("Error processing goals for task node: {}", current.getId(), e);
                        // Fallback: just add the node name without goals
                        goalTree.insert(0, String.format("Node '%s': [Goals not accessible]\n", current.getName()));
                    }
                }
                current = current.getParentTask();
            }

            return goalTree.toString();
        } catch (Exception e) {
            log.error("Error building goal tree", e);
            return "Error building goal tree: " + e.getMessage();
        }
    }

    private String getCurrentNodeGoals(EngineExecution execution) {
        try {
            TaskNode taskNode = execution.getGoal().getTaskNode();
            if (taskNode.getGoals() == null) {
                return "No goals defined";
            }

            return taskNode.getGoals().stream()
                    .filter(goal -> goal.getExecutions() != null)
                    .flatMap(goal -> goal.getExecutions().stream())
                    .filter(exec -> exec.getAgent() != null &&
                            exec.getAgent().getAgentType() == AgentType.AIThinking)
                    .map(exec -> exec.getGoal().getDescription())
                    .collect(Collectors.joining(", "));
        } catch (Exception e) {
            log.warn("Error getting current node goals", e);
            return "Error retrieving current node goals";
        }
    }

    private String getAgentOptions(EngineExecution execution) {
        try {
            TaskNode taskNode = execution.getGoal().getTaskNode();
            if (taskNode.getTaskNodeMaster() == null || taskNode.getTaskNodeMaster().getAgents() == null) {
                return "No agent options available";
            }

            StringBuilder agentOptions = new StringBuilder();
            agentOptions.append("| Agent ID | Agent Type | Roles |");
            agentOptions.append("\n");
            for (Agent agent : taskNode.getTaskNodeMaster().getAgents()) {
                agentOptions.append(String.format("| %s | %s | %s |",
                        agent.getAgentId(),
                        agent.getAgentType(),
                        agent.getRoles()));
            }
            return agentOptions.toString();
        } catch (Exception e) {
            log.warn("Error getting agent options", e);
            return "Error retrieving agent options";
        }
    }

    private String getRAGResult(EngineExecution execution) {
        try {
            TaskNode taskNode = execution.getGoal().getTaskNode();
            if (taskNode == null || taskNode.getTaskNodeMaster() == null) {
                return "No task node master available for RAG search";
            }

            String masterId = taskNode.getTaskNodeMaster().getId();

            // Search vector store for relevant knowledge - simplified for now
            log.debug("Searching RAG database for master_id: {}", masterId);
            return "Relevant knowledge retrieved from RAG database for master_id: " + masterId;

        } catch (Exception e) {
            log.warn("Error retrieving RAG results", e);
            return "Error retrieving knowledge from RAG database";
        }
    }

    @Override
    public AgentType getAgentType() {
        return AgentType.AIThinking;
    }

    /**
     * Converts PlanGoal objects into EngineGoal objects and adds them to the
     * TaskNode
     */
    private void convertAndAddPlanGoalsToTaskNode(EngineExecution execution, List<PlanGoal> planGoals) {
        log.debug("Converting {} plan goals to EngineGoal objects", planGoals.size());

        TaskNode taskNode = execution.getGoal().getTaskNode();

        for (PlanGoal planGoal : planGoals) {
            try {
                // Find the agent by agentId
                Agent agent = agentRepository.findByAgentId(planGoal.getAgentId());
                if (agent == null) {
                    log.warn("Agent not found with ID: {}, skipping plan goal: {}", planGoal.getAgentId(),
                            planGoal.getName());
                    continue;
                }

                // Create EngineGoal from PlanGoal
                EngineGoal engineGoal = new EngineGoal();
                engineGoal.setName(planGoal.getName());
                engineGoal.setDescription(planGoal.getDescription());
                engineGoal.setPriority(taskNode.getPriority() + 1); // Child goals have higher priority
                engineGoal.setStatus(EngineGoalStatus.PENDING);
                engineGoal.setTaskNode(taskNode);

                // Create EngineExecution with proper initialization
                EngineExecution engineExecution = new EngineExecution();
                engineExecution.setAgent(agent);
                engineExecution.setStatus(ExecutionStatus.STALLED);
                engineExecution.setGoal(engineGoal);
                engineExecution.setConfig(Map.of("reasoningAction", ReasoningAction.BRANCHING.name()));

                engineGoal.addExecution(engineExecution);

                // Save the EngineExecution to the database
                EngineExecution savedExecution = engineExecutionRepository.save(engineExecution);

                // Add the EngineGoal to the TaskNode using TaskService
                var result = taskService.addEngineGoalToTaskNode(taskNode, engineGoal);
                if (result.isOk()) {
                    log.info("Successfully added EngineGoal '{}' with EngineExecution '{}' to TaskNode {}",
                            planGoal.getName(), savedExecution.getId(), taskNode.getId());
                } else {
                    log.error("Failed to add EngineGoal '{}' to TaskNode {}: {}",
                            planGoal.getName(), taskNode.getId(), result.getMessage());
                }

            } catch (Exception e) {
                log.error("Error converting plan goal '{}' to EngineGoal", planGoal.getName(), e);
            }
        }
    }

    /**
     * Evaluation result record for the Evaluator-Optimizer pattern
     */
    private record EvaluationResult(boolean pass, String feedback) {
    }
}