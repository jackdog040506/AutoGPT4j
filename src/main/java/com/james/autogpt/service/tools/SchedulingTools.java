package com.james.autogpt.service.tools;

import org.quartz.SchedulerException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.james.autogpt.dto.OpsScheduleSettingsDto;
import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.ResultList;
import com.james.autogpt.schedules.ScheduleService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SchedulingTools {

	private final ScheduleService scheduleService;

	/**
	 * list job by groupId
	 */
	@Tool(name = "scheduling_getByGroupId", description = "List jobs by groupId")
	public ResultList<OpsScheduleSettingsDto> getByGroupId(
			@ToolParam(description = "The group ID to filter jobs") String groupId) {
		// Directly delegate to ScheduleService
		return scheduleService.getByGroupId(groupId);
	}

	/**
	 * 停止调度某个job
	 */
	@Tool(name = "scheduling_removeSchedule", description = "Stop scheduling a specific job")
	public Result<String> removeSchedule(
			@ToolParam(description = "Unique identifier for the schedule") String scheduleId,
			@ToolParam(description = "Identifier for the group this schedule belongs to") String groupId)
			throws SchedulerException {
		// Delegate to ScheduleService
		return scheduleService.removeSchedule(scheduleId, groupId);
	}

	/**
	 * 更新某个job
	 */
	@Tool(name = "scheduling_saveJobSetting", description = "Update a specific job")
	public Result<String> saveJobSetting(
			@ToolParam(description = "The job setting to update") OpsScheduleSettingsDto setting)
			throws SchedulerException {
		// Delegate to ScheduleService
		return scheduleService.saveJobSetting(setting);
	}

	// Result<OpsScheduleExecResultDto> immdRunJobSetting(String scheduleId, String
	// groupId);
}
