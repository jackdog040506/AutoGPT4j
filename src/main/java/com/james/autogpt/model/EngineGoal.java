package com.james.autogpt.model;

import java.util.HashSet;
import java.util.Set;

import com.james.autogpt.dto.scopes.EngineGoalStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "engine_goal")
@Data
@EqualsAndHashCode(callSuper = true)
public class EngineGoal extends EntityBase {

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private EngineGoalStatus status = EngineGoalStatus.PENDING; // E.g., PENDING, IN_PROGRESS, COMPLETED, FAILED, CANCELLED

	@Column(nullable = false)
	private Integer priority; // E.g., 1 (High) to 5 (Low)

	// Self-referencing relationship for goal decomposition (parent-child)
	@ManyToOne
	@JoinColumn(name = "task_node_id")
	private TaskNode taskNode;

	// One-to-Many relationship with Execution (A goal can have multiple attempts/executions)
	@OneToMany(mappedBy = "goal", fetch = FetchType.LAZY)
	private Set<EngineExecution> executions = new HashSet<>();

	public void addExecution(EngineExecution execution) {
		this.executions.add(execution);
		execution.setGoal(this);
	}

	public void removeExecution(EngineExecution execution) {
		this.executions.remove(execution);
		execution.setGoal(null);
	}
}
