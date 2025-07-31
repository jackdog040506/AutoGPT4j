package com.james.autogpt.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "task_node_master")
@Data
@EqualsAndHashCode(callSuper = true)
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