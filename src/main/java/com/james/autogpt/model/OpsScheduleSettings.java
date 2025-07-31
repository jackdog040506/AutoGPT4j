package com.james.autogpt.model;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.james.autogpt.dto.ScheduleJobStatus;

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
@Table(name = "ops_schedule_settings")
public class OpsScheduleSettings extends RecordEntityBase {

	@Id
	private OpsScheduleSettingsPk id;

	private String cron;

	private String classPath;

	private String categoryId;

	private String status = ScheduleJobStatus.INIT.name();

	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp dateStarted;

	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp dateEnded;

	private String timezone;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> config = new HashMap<>();

	private Boolean ignoreMisfire;
}
