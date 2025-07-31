package com.james.autogpt.model;

import java.io.Serializable;
import java.util.Map;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.james.autogpt.dto.scopes.ExecutionStatus;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Represents a specific attempt by an Agent to achieve a Goal. An Execution
 * consists of a sequence of Tasks. This entity replaces the previous 'Process'
 * for tracking a specific run.
 */
@Data
@Entity
@Table(name = "engine_executions")
public class EngineExecution extends EntityBase implements Serializable {

	@ManyToOne
	@JoinColumn(name = "agent_id", nullable = false)
	private Agent agent; // E.g., "Execution for Goal #123"


	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ExecutionStatus status; // E.g., RUNNING, PAUSED, COMPLETED, FAILED, ABORTED

	// Many-to-One relationship with Goal (Which goal is being executed)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "engine_goal_id", nullable = false)
	private EngineGoal goal;
	
	@Fetch(FetchMode.JOIN)
	@Basic(fetch = FetchType.EAGER)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> config;


}