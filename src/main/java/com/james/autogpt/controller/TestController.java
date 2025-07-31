package com.james.autogpt.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.james.autogpt.dto.ChatMessage;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {


	private final VectorStore vectorStore; // PgVectorStore will be autowired
    private final SimpMessageSendingOperations messagingTemplate;

	@PostMapping("/saveEmbedding")
	public void saveEmbedding(@RequestBody Map<String, Object> metadata) {
		Document doc = Document.builder().id(UUID.randomUUID().toString()).metadata(metadata).build();
		vectorStore.add(List.of(doc));
	}

	@PostMapping("/findSimilar")
	public List<Document> findSimilar(@RequestParam(defaultValue = "100") Integer maxResults,
			@RequestBody String theme) {
		SearchRequest searchRequest = SearchRequest.builder().query(theme).topK(maxResults).build();
		List<Document> documents = vectorStore.similaritySearch(searchRequest);
		return documents; // top 5 matches
	}
	
	@PostMapping("/sendMessage")
	public void sendMessage(@RequestBody String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(ChatMessage.MessageType.CHAT);
        chatMessage.setSender("TestUser");
        chatMessage.setContent(message);
        messagingTemplate.convertAndSend("/topic/public", chatMessage);
	}

}
