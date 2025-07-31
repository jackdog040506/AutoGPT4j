package com.james.autogpt.dto;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import lombok.Data;

@Data
public class OpsScheduleSettingsDto {
	@JsonPropertyDescription("Unique identifier for the schedule")
	private String scheduleId;

	@JsonPropertyDescription("Identifier for the group this schedule belongs to")
	private String groupId;

	@JsonPropertyDescription("Identifier for the category of the schedule")
	private String categoryId;
	
	@JsonPropertyDescription("Cron expression defining the schedule")
	private String cron;

	@JsonPropertyDescription("Current status of the schedule")
	private String status;

	@JsonPropertyDescription("Parameters for the schedule execution")
	private Map<String, Object> parameter = new HashMap<>();

	@JsonPropertyDescription("Timestamp when the schedule started")
	private Timestamp dateStarted;

	@JsonPropertyDescription("Timestamp when the schedule ended")
	private Timestamp dateEnded;

	@JsonPropertyDescription("Timezone for the schedule")
	private String timezone;

	// --------read only-------
	@JsonPropertyDescription("Last time the schedule was executed (read-only)")
	private Timestamp lastExecuteTime;

	@JsonPropertyDescription("Next scheduled execution time (read-only)")
	private Timestamp nextExecuteTime;

	@JsonPropertyDescription("Execution cost in milliseconds (read-only)")
	private long costMillSeconds;

	@JsonPropertyDescription("Whether to ignore misfires (read-only)")
	private Boolean ignoreMisfire;
}
