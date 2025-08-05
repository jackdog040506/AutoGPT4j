package com.james.autogpt.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "task_node_master")
@Data
@EqualsAndHashCode(callSuper = true)
@NamedEntityGraphs({
    @NamedEntityGraph(
        name = "TaskNodeMaster.withTaskNodes",
        attributeNodes = {
            @NamedAttributeNode("taskNodes"),
            @NamedAttributeNode("rootTaskNode")
        }
    ),
    @NamedEntityGraph(
        name = "TaskNodeMaster.complete",
        attributeNodes = {
            @NamedAttributeNode(value = "taskNodes", subgraph = "taskNodes-with-goals"),
            @NamedAttributeNode(value = "rootTaskNode", subgraph = "rootTask-with-goals")
        },
        subgraphs = {
            @NamedSubgraph(
                name = "taskNodes-with-goals",
                attributeNodes = {
                    @NamedAttributeNode("goals"),
                    @NamedAttributeNode("subTasks")
                }
            ),
            @NamedSubgraph(
                name = "rootTask-with-goals",
                attributeNodes = {
                    @NamedAttributeNode("goals"),
                    @NamedAttributeNode("subTasks")
                }
            )
        }
    )
})
public class TaskNodeMaster extends EntityBase {

	@Column(nullable = false, unique = true)
	private String conversationId; // Leading conversation ID

	@Column(nullable = false)
	private String name;

	@Column(columnDefinition = "TEXT")
	private String description;

	// Root task node for this conversation
	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "root_task_node_id")
	private TaskNode rootTaskNode;

	// All task nodes in this conversation tree
	@OneToMany(mappedBy = "taskNodeMaster", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<TaskNode> taskNodes = new HashSet<>();

	// Many-to-many relationship with agents
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "task_node_master_agents",
		joinColumns = @JoinColumn(name = "task_node_master_id"),
		inverseJoinColumns = @JoinColumn(name = "agent_id")
	)
	private Set<Agent> agents = new HashSet<>();

	@PrePersist
	public void onPrePersist() {
		if (this.conversationId == null) {
			setConversationId(UUID.randomUUID().toString());
		}
	}

	// Helper methods for managing relationships
	public void setRootTaskNode(TaskNode rootTaskNode) {
		this.rootTaskNode = rootTaskNode;
		if (rootTaskNode != null) {
			rootTaskNode.setTaskNodeMaster(this);
		}
	}

	public void addTaskNode(TaskNode taskNode) {
		this.taskNodes.add(taskNode);
		taskNode.setTaskNodeMaster(this);
	}

	public void removeTaskNode(TaskNode taskNode) {
		this.taskNodes.remove(taskNode);
		taskNode.setTaskNodeMaster(null);
	}
}