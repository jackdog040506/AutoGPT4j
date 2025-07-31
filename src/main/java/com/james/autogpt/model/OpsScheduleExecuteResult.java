package com.james.autogpt.model;

import java.sql.Timestamp;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id", callSuper = false)
@Entity
@Table(name = "ops_schedule_exec_result")
public class OpsScheduleExecuteResult extends RecordEntityBase  {


	@Id
	@UuidGenerator
	private String id;

	private String scheduleId;

	private String groupId;

	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp timeStarted;

	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp timeFinished;

	private String status;

	@Column(columnDefinition = "TEXT")
	private String runningStatement;

	@Column(columnDefinition = "TEXT")
	private String errorMsg;
}
