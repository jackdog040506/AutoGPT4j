package com.james.autogpt.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ops_tickets")
@ToString(exclude = { "result", "tasks"})
@NamedEntityGraph(
    name = "OpsTickets.withTasks",
    attributeNodes = {
        @NamedAttributeNode("tasks")
    }
)
public class OpsTickets extends RecordEntityBase {

	@Id
	private String id;

	private String type;

	private String ticketsTemplateId;

	private String name;

	private String status;

	// Changed from EAGER to LAZY - use EntityGraph instead for performance control
	@OneToMany(mappedBy = "opsTickets", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<OpsTicketsTask> tasks;

	@Fetch(FetchMode.JOIN)
	@Basic(fetch = FetchType.EAGER)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> result = new HashMap<>();

}
