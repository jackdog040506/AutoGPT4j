package com.james.autogpt.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.agent.AgentResponse;
import com.james.autogpt.dto.agent.CreateAgentRequest;
import com.james.autogpt.dto.agent.UpdateAgentRequest;
import com.james.autogpt.dto.scopes.AgentType;
import com.james.autogpt.model.Agent;
import com.james.autogpt.repository.AgentRepository;
import com.james.autogpt.service.tools.SchedulingTools;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgentService {
	private ChatClient chatClient;
	private final EmbeddingModel embeddingModel;
//	private final VectorStore vectorStore;
	private final SchedulingTools schedulingTools;
	private final VectorStore vectorStore;
	private final AgentRepository agentRepository;

	@Autowired
	public void setChatClientBuilder(ChatClient.Builder chatClientBuilder) {
		this.chatClient = chatClientBuilder.build();
	}

	// CRUD Operations for Agent

	/**
	 * Create a new agent
	 */
	public Result<AgentResponse> createAgent(CreateAgentRequest request) {
		try {
			// Check if agent already exists
			if (agentRepository.existsById(request.getAgentId())) {
				return Result.ofError(400, "Agent with ID " + request.getAgentId() + " already exists");
			}

			Agent agent = new Agent();
			agent.setAgentId(request.getAgentId());
			agent.setRoles(request.getRoles());
			agent.setFocus(request.getFocus());
			agent.setPersonality(request.getPersonality());
			agent.setAgentType(request.getAgentType());

			Agent savedAgent = agentRepository.save(agent);
			return Result.ofSuccess(AgentResponse.fromAgent(savedAgent));
		} catch (Exception e) {
			return Result.ofError(500, "Failed to create agent: " + e.getMessage());
		}
	}

	/**
	 * Get agent by ID
	 */
	public Result<AgentResponse> getAgentById(String agentId) {
		try {
			Optional<Agent> agent = agentRepository.findById(agentId);
			if (agent.isPresent()) {
				return Result.ofSuccess(AgentResponse.fromAgent(agent.get()));
			}
			return Result.ofError(404, "Agent not found with ID: " + agentId);
		} catch (Exception e) {
			return Result.ofError(500, "Failed to retrieve agent: " + e.getMessage());
		}
	}

	/**
	 * Get all agents
	 */
	public Result<List<AgentResponse>> getAllAgents() {
		try {
			List<AgentResponse> agents = agentRepository.findAll()
					.stream()
					.map(AgentResponse::fromAgent)
					.collect(Collectors.toList());
			return Result.ofSuccess(agents);
		} catch (Exception e) {
			return Result.ofError(500, "Failed to retrieve agents: " + e.getMessage());
		}
	}

	/**
	 * Get agents by type
	 */
	public Result<List<AgentResponse>> getAgentsByType(AgentType agentType) {
		try {
			List<AgentResponse> agents = agentRepository.findAll()
					.stream()
					.filter(agent -> agent.getAgentType() == agentType)
					.map(AgentResponse::fromAgent)
					.collect(Collectors.toList());
			return Result.ofSuccess(agents);
		} catch (Exception e) {
			return Result.ofError(500, "Failed to retrieve agents by type: " + e.getMessage());
		}
	}

	/**
	 * Update agent
	 */
	public Result<AgentResponse> updateAgent(String agentId, UpdateAgentRequest request) {
		try {
			Optional<Agent> existingAgent = agentRepository.findById(agentId);
			if (existingAgent.isEmpty()) {
				return Result.ofError(404, "Agent not found with ID: " + agentId);
			}

			Agent agent = existingAgent.get();

			// Update only non-null fields
			if (request.getRoles() != null) {
				agent.setRoles(request.getRoles());
			}
			if (request.getFocus() != null) {
				agent.setFocus(request.getFocus());
			}
			if (request.getPersonality() != null) {
				agent.setPersonality(request.getPersonality());
			}
			if (request.getAgentType() != null) {
				agent.setAgentType(request.getAgentType());
			}

			Agent updatedAgent = agentRepository.save(agent);
			return Result.ofSuccess(AgentResponse.fromAgent(updatedAgent));
		} catch (Exception e) {
			return Result.ofError(500, "Failed to update agent: " + e.getMessage());
		}
	}

	/**
	 * Delete agent
	 */
	public Result<String> deleteAgent(String agentId) {
		try {
			if (!agentRepository.existsById(agentId)) {
				return Result.ofError(404, "Agent not found with ID: " + agentId);
			}

			agentRepository.deleteById(agentId);
			return Result.ofSuccess("Agent deleted successfully");
		} catch (Exception e) {
			return Result.ofError(500, "Failed to delete agent: " + e.getMessage());
		}
	}

	// Add events to vector store (memory)
// 	public void addEventsToMemory(List<CondensedEvent> events) {
// 		var docs = events.stream().map(event -> {
// 			Map<String, Object> metadata = new ConcurrentHashMap<>();
// //					metadata.put("id", event.getId() == null ? "" : String.valueOf(event.getId()));
// //					metadata.put("eventType", event.getEventType());
// 			metadata.put("Authenticity about this news", //
// 					event.getScore() == null ? "" : String.valueOf(event.getScore()));
// 			metadata
// 					.put("Emotional present forecast Direction about this news options has 'strong bearish', 'small bearish', 'mediocre' , 'small bullish' , 'strong bullish'.", //
// 							event.getEmotion());
// 			metadata.put("The direction of the event, such as 'positive', 'negative', or 'neutral'.", //
// 					event.getDirection());
// //					metadata.put("summary", event.getSummary());
// 			metadata.put("all the stock code that news mentions , join by comma.", //
// 					event.getIndicators());
// 			metadata
// 					.put("Relevance of time effect about these news, will it effected within this options 'days', 'one week', 'one month'.", //
// 							event.getTimeFrame());
// 			metadata.put("createdAt", //
// 					event.getCreatedAt() == null ? "" : event.getCreatedAt().toString());

// 			Document doc = new Document(event.getSummary(), metadata);
// 			return doc;
// 		}).collect(Collectors.toList());
// 		vectorStore.add(docs);
// 	}

	// Search for relevant events in vector store
//	public List<CondensedEvent> searchRelevantEvents(String query, int topK) {
//		var results = vectorStore.similaritySearch(query);
//		return results.stream().limit(topK).map(doc -> {
//			String idStr = String.valueOf(doc.getMetadata().getOrDefault("id", ""));
//			try {
//				Long id = Long.parseLong(idStr);
//				return condensedEventRepository.findById(id).orElse(null);
//			} catch (NumberFormatException e) {
//				CondensedEvent ce = new CondensedEvent();
//				ce.setContent(doc.getFormattedContent());
//				return ce;
//			}
//		}).filter(e -> e != null).collect(Collectors.toList());
//	}

	// Example: process a news event and add to memory
	// public CondensedEvent processNewsEvent(String newsContent) {
	// 	BeanOutputConverter<CondensedEvent> outputConverter = new BeanOutputConverter<>(CondensedEvent.class);
	// 	String format = outputConverter.getFormat();

	// 	// Build the prompt with format instructions
	// 	String userInputTemplate = Constants.PROMPT_CONDENSE_NEWS_EVENT;
	// 	PromptTemplate promptTemplate = new PromptTemplate(userInputTemplate);
	// 	Prompt prompt = promptTemplate.create(java.util.Map.of("newsContent", newsContent, "format", format));
	// 	CondensedEvent event = chatClient
	// 			.prompt(prompt)
	// 			.call()
	// 			.entity(new ParameterizedTypeReference<CondensedEvent>() {
	// 			});
	// 	event.setId(null);
	// 	event.setContent(newsContent);
	// 	event.setCreatedAt(LocalDateTime.now()); // Ensure createdAt is null for new events
	// 	event = condensedEventRepository.save(event);
	// 	addEventsToMemory(List.of(event));
	// 	return event;
	// }

	// Example: summarize events using RAG
//	public String summarizeEvents(String query) {
//		List<CondensedEvent> relevant = searchRelevantEvents(query, 5);
//		String context = relevant.stream().map(CondensedEvent::getContent).collect(Collectors.joining("\n"));
//		String promptText = "Given the following events:\n" + context + "\n\nAnswer the following question:\n" + query;
//		Prompt prompt = new Prompt(promptText);
//		return chatClient.prompt(prompt).call().content();
//	}

// 	public List<Generation> summarizeEvents(List<CondensedEvent> events, String promptText) {

// 		VectorStore portotype = SimpleVectorStore.builder(embeddingModel).build();

// 		List<Document> documents = events
// 				.stream()
// 				.filter(event -> event.getContent() != null && !event.getContent().isEmpty())
// 				.map(event -> {
// 					Map<String, Object> metadata = new ConcurrentHashMap<>();
// //					metadata.put("id", event.getId() == null ? "" : String.valueOf(event.getId()));
// //					metadata.put("eventType", event.getEventType());
// 					metadata.put("Authenticity about this news", //
// 							event.getScore() == null ? "" : String.valueOf(event.getScore()));
// 					metadata
// 							.put("Emotional present forecast Direction about this news options has 'strong bearish', 'small bearish', 'mediocre' , 'small bullish' , 'strong bullish'.", //
// 									event.getEmotion());
// 					metadata.put("The direction of the event, such as 'positive', 'negative', or 'neutral'.", //
// 							event.getDirection());
// //					metadata.put("summary", event.getSummary());
// 					metadata.put("all the stock code that news mentions , join by comma.", //
// 							event.getIndicators());
// 					metadata
// 							.put("Relevance of time effect about these news, will it effected within this options 'days', 'one week', 'one month'.", //
// 									event.getTimeFrame());
// 					metadata.put("createdAt", //
// 							event.getCreatedAt() == null ? "" : event.getCreatedAt().toString());

// 					Document doc = new Document(event.getSummary(), metadata);
// 					return doc;
// 				})
// 				.toList();
// 		portotype.add(documents);

// //		String context = relevant.stream().map(CondensedEvent::getContent).collect(Collectors.joining("\n"));
// 		Prompt prompt = new Prompt(promptText);
// 		ChatResponse response = chatClient
// 				.prompt(prompt)
// 				.tools(schedulingTools, tradeTools, indicatorTools)
// 				.advisors(new QuestionAnswerAdvisor(portotype))
// 				.call()
// 				.chatResponse();

// 		return response.getResults();
// 	}

	// public List<Generation> indicatorAssessment(String prompt) {
	// 	ChatResponse response = chatClient.prompt(prompt).tools(indicatorTools).call().chatResponse();
	// 	return response.getResults();
	// }

	// public List<Generation> tradeAssessment(String stock, String agentId, String time) {
	// 	PromptTemplate promptTemplate = new PromptTemplate(new ClassPathResource("templates/prompt.yaml"));
	// 	Prompt prompt = promptTemplate.create(java.util.Map.of("stockCode", stock, "agentId", agentId, "time", time));

	// 	SearchRequest searchRequest = SearchRequest
	// 			.builder()//
	// 			.query(String.format("%s,%s,%s", stock, agentId, time))//
	// 			.topK(1000)
	// 			.build();

	// 	QuestionAnswerAdvisor advisor = QuestionAnswerAdvisor.builder(vectorStore)
	// 		    .searchRequest(searchRequest)
	// 		    .build();
	// 	ChatResponse response = chatClient
	// 			.prompt(prompt)
	// 			.advisors(advisor)
	// 			.tools(indicatorTools, schedulingTools, tradeTools)
	// 			.call()
	// 			.chatResponse();

	// 	return response.getResults();
	// }

}