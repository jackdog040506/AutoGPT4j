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
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "engine_goal")
@Data
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "EngineGoal.withExecutions",
        attributeNodes = {
            @NamedAttributeNode(value = "executions", subgraph = "executions-with-agent")
        },
        subgraphs = {
            @NamedSubgraph(
                name = "executions-with-agent",
                attributeNodes = {
                    @NamedAttributeNode("agent")
                }
            )
        }
    ),
    @NamedEntityGraph(
        name = "EngineGoal.withTaskNode",
        attributeNodes = {
            @NamedAttributeNode(value = "taskNode", subgraph = "taskNode-basic")
        },
        subgraphs = {
            @NamedSubgraph(
                name = "taskNode-basic",
                attributeNodes = {
                    @NamedAttributeNode("taskNodeMaster"),
                    @NamedAttributeNode("parentTask")
                }
            )
        }
    ),
    @NamedEntityGraph(
        name = "EngineGoal.complete",
        attributeNodes = {
            @NamedAttributeNode(value = "executions", subgraph = "executions-complete"),
            @NamedAttributeNode(value = "taskNode", subgraph = "taskNode-complete")
        },
        subgraphs = {
            @NamedSubgraph(
                name = "executions-complete",
                attributeNodes = {
                    @NamedAttributeNode("agent")
                }
            ),
            @NamedSubgraph(
                name = "taskNode-complete",
                attributeNodes = {
                    @NamedAttributeNode("taskNodeMaster"),
                    @NamedAttributeNode("parentTask"),
                    @NamedAttributeNode("subTasks")
                }
            )
        }
    )
})
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
