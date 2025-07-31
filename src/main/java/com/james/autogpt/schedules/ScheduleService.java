package com.james.autogpt.schedules;

import java.util.List;
import java.util.Optional;

import org.quartz.SchedulerException;
import org.springframework.data.domain.Pageable;

import com.james.autogpt.dto.OpsScheduleExecResultDto;
import com.james.autogpt.dto.OpsScheduleSettingsDto;
import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.ResultList;
import com.james.autogpt.dto.ResultPage;
import com.james.autogpt.model.OpsScheduleSettings;

public interface ScheduleService {

	/**
	 * 停止调度某个job
	 */
	Result<String> removeSchedule(String scheduleId, String groupId) throws SchedulerException;

	/**
	 * 更新某个job
	 */
	Result<String> saveJobSetting(OpsScheduleSettingsDto setting) throws SchedulerException;

	Result<OpsScheduleExecResultDto> immdRunJobSetting(String scheduleId, String groupId);

	Optional<OpsScheduleSettings> findJobSetting(String scheduleId, String groupId);

	ResultList<OpsScheduleSettingsDto> getAll();

	ResultList<OpsScheduleSettingsDto> getAllAvailableJobs();

	List<OpsScheduleSettings> getListByGroupId(String groupId);

	ResultPage<OpsScheduleExecResultDto> getExecHistory(String groupId, String scheduleId, Pageable pageable);

	ResultList<OpsScheduleSettingsDto> getByGroupId(String groupId);

}
