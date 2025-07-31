package com.james.autogpt.dto;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class OpsScheduleExecResultDto {

	private String id;

	private String scheduleId;

	private String groupId;

	private Timestamp timeStarted;

	private Timestamp timeFinished;

	private String status;

	private String runningStatement;

	private String errorMsg;
}
