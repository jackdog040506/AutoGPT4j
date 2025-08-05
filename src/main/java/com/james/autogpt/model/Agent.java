package com.james.autogpt.model;

import com.james.autogpt.dto.scopes.AgentType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "agents")
public class Agent {
	@Id
	private String agentId;

	private String roles; // comma-separated roles
	private String focus;
	private String personality;

	@Enumerated(EnumType.STRING)
	private AgentType agentType; //
}