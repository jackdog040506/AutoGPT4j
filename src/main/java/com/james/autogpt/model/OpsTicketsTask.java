package com.james.autogpt.model;

import java.util.Map;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ops_tickets_task")
@ToString(exclude = "opsTickets")
public class OpsTicketsTask  extends RecordEntityBase {

	@Id
	private String id;

	private String taskTemplateId;

	private String execStatus;

	@Column(length = 1024)
	private String execMessage;

	private Integer execOrder;

//	private Boolean validToExecute=false;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "ops_tickets_id")
	private OpsTickets opsTickets;

	@Fetch(FetchMode.JOIN)
	@Basic(fetch = FetchType.EAGER)
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> config;
}
