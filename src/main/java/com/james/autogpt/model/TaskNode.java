package com.james.autogpt.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "task_node")
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskNode extends EntityBase {
    
    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private TaskStatus status = TaskStatus.PENDING;

    @Column(nullable = false)
    private Integer priority; // layer of task in the hierarchy,

    @Column(columnDefinition = "TEXT")
    private String prompt; // Contextual information for this task

    // Self-referencing relationship for task decomposition (parent-child)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_task_id")
    private TaskNode parentTask;

    @OneToMany(mappedBy = "parentTask", fetch = FetchType.LAZY)
    private Set<TaskNode> subTasks = new HashSet<>();

    @OneToMany(mappedBy = "taskNode", fetch = FetchType.LAZY)
    private Set<EngineGoal> goals = new HashSet<>();

    // Relationship with TaskNodeMaster
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_node_master_id", nullable = false)
    private TaskNodeMaster taskNodeMaster;

    @Column(nullable = false, unique = true)
    private String conversationId; // Leading conversation ID

    @PrePersist
	public void onPrePersist() {
		if (this.conversationId == null) {
			setConversationId(UUID.randomUUID().toString());
		}
	}
    // public enum TaskStatus {
    //     PENDING,
    //     IN_PROGRESS,
    //     COMPLETED,
    //     FAILED,
    //     CANCELLED,
    //     BLOCKED
    // }
} 