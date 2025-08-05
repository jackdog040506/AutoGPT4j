package com.james.autogpt.schedules;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.TriggerUtils;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.james.autogpt.dto.OpsScheduleExecResultDto;
import com.james.autogpt.dto.OpsScheduleSettingsDto;
import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.ResultList;
import com.james.autogpt.dto.ResultPage;
import com.james.autogpt.dto.ScheduleJobStatus;
import com.james.autogpt.model.OpsScheduleExecuteResult;
import com.james.autogpt.model.OpsScheduleSettings;
import com.james.autogpt.model.OpsScheduleSettingsPk;
import com.james.autogpt.repository.OpsScheduleExecuteResultJpaRepository;
import com.james.autogpt.repository.OpsScheduleSettingsJpaRepository;
import com.james.autogpt.schedules.jobs.AbstractScheduleJob;
import com.james.autogpt.schedules.jobs.TestJobExecutionContext;
import com.james.autogpt.utils.ModelMapperUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

	private final OpsScheduleExecuteResultJpaRepository opsScheduleExecuteResultJpaRepository;
	private final OpsScheduleSettingsJpaRepository opsScheduleSettingsJpaRepository;
	private final Scheduler scheduler;

	private final List<AbstractScheduleJob> jobs;

	/**
	 * 調度指定的job(調整job)
	 */
	private Result<String> schedule(OpsScheduleSettingsDto setting, AbstractScheduleJob scheduleJob)
			throws SchedulerException {
		// triggerId 使用 scheduledId 代替
		String triggerId = setting.getScheduleId();
		String groupId = setting.getGroupId();
		String jobId = setting.getScheduleId();

		if (!scheduler.isStarted()) {
			return Result.ofError(400, "schedule.not.started");
		}

		TriggerKey triggerKey = TriggerKey.triggerKey(triggerId, groupId);
		if (scheduler.checkExists(triggerKey)) {
			// 如果已經在調度
			if (!StringUtils.hasText(setting.getCron())
					|| !ScheduleJobStatus.STARTED.name().equals(setting.getStatus())) {
				// 需要移除調度
				removeSchedule(triggerId,  groupId);
			} else {
				CronScheduleBuilder builder = CronScheduleBuilder
						.cronSchedule(setting.getCron())
						.inTimeZone(TimeZone.getTimeZone(setting.getTimezone()));
				if (Boolean.TRUE.equals(setting.getIgnoreMisfire())) {
					builder.withMisfireHandlingInstructionDoNothing();
				}

				// 需要重新調度
				CronTrigger trigger = TriggerBuilder
						.newTrigger()
						.withIdentity(triggerKey)
						.usingJobData(new JobDataMap(setting.getParameter()))
						.withSchedule(builder)
						.startAt(setting.getDateStarted() != null ? new Date(setting.getDateStarted().getTime())
								: new Date())
						.endAt(setting.getDateEnded())
						.build();
				scheduler.rescheduleJob(TriggerKey.triggerKey(triggerId, groupId), trigger);
			}
		} else if (StringUtils.hasText(setting.getCron())
				&& ScheduleJobStatus.STARTED.name().equals(setting.getStatus())) {
			// 如果不再調度, 看看是否需要調度
			@SuppressWarnings("rawtypes")
			Class clazz;
			try {
				clazz = Class.forName(scheduleJob.getCN());
			} catch (ClassNotFoundException e) {
				log.error("not found class, cannot schedule, className:{}", scheduleJob.getCN());
				throw new SchedulerException("not found class, cannot schedule");
			}

			CronScheduleBuilder builder = CronScheduleBuilder
					.cronSchedule(setting.getCron())
					.inTimeZone(TimeZone.getTimeZone(setting.getTimezone()));
			if (Boolean.TRUE.equals(setting.getIgnoreMisfire())) {
				builder.withMisfireHandlingInstructionDoNothing();
			}

			@SuppressWarnings("unchecked")
			JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobId, groupId).build();
			CronTrigger trigger = TriggerBuilder
					.newTrigger()
					.withIdentity(triggerKey)
					.usingJobData(new JobDataMap(setting.getParameter()))
					.withSchedule(builder)
					.startAt(setting.getDateStarted() != null ? new Date(setting.getDateStarted().getTime())
							: new Date())
					.endAt(setting.getDateEnded())
					.build();
			scheduler.scheduleJob(job, trigger);
		}

		return Result.ofSuccess(triggerKey.toString());
	}

	/**
	 * 停止調度某個job
	 */
	@Override
	public Result<String> removeSchedule(String scheduleId, String groupId) throws SchedulerException {
		// triggerId 使用 scheduledId 代替

		Optional<OpsScheduleSettings> settingsOp = findJobSetting(scheduleId, groupId);

		if (!settingsOp.isPresent()) {
			return Result.ofError(400, "schedule.setting.notfound");
		}

		if (!scheduler.isStarted()) {
			return Result.ofError(400, "schedule.not.started");
		}

		boolean success = scheduler.unscheduleJob(TriggerKey.triggerKey(scheduleId, groupId));
		if (success) {
			OpsScheduleSettings opsScheduleSettings = settingsOp.get();
			opsScheduleSettings.setStatus(ScheduleJobStatus.STOPPED.name());
			opsScheduleSettingsJpaRepository.save(opsScheduleSettings);
		}

		return Result.ofSuccess("success");
	}

	/**
	 * 更新某個job
	 */
	@Override
	public Result<String> saveJobSetting(OpsScheduleSettingsDto setting) throws SchedulerException {

		Optional<OpsScheduleSettings> settingsOp = findJobSetting(setting.getScheduleId(), setting.getGroupId());

		AbstractScheduleJob found = findImplByCategoryId(setting.getCategoryId());
		if (found == null) {
			return Result.ofError(400, "no.operation");
		}
		OpsScheduleSettings opsScheduleSettings;
		if (settingsOp.isPresent()) {
			opsScheduleSettings = settingsOp.get();
		} else {
			opsScheduleSettings = new OpsScheduleSettings();
			OpsScheduleSettingsPk id = new OpsScheduleSettingsPk();
			id.setScheduleId(setting.getScheduleId());
			id.setGroupId(setting.getGroupId());
			opsScheduleSettings.setId(id);
		}
		opsScheduleSettings.setCron(setting.getCron());
		opsScheduleSettings.setStatus(setting.getStatus());
		opsScheduleSettings.setClassPath(found.getCN());
		opsScheduleSettings.setCategoryId(found.categoryId());
		opsScheduleSettings.setConfig(setting.getParameter());
		opsScheduleSettings.setDateStarted(setting.getDateStarted());
		opsScheduleSettings.setDateEnded(setting.getDateEnded());
		opsScheduleSettings.setTimezone(setting.getTimezone());
		opsScheduleSettings.setIgnoreMisfire(setting.getIgnoreMisfire());
		opsScheduleSettingsJpaRepository.save(opsScheduleSettings);
		return schedule(setting, found);
	}

	@Override
	public Optional<OpsScheduleSettings> findJobSetting(String scheduleId, String groupId) {

		OpsScheduleSettingsPk id = new OpsScheduleSettingsPk();
		id.setScheduleId(scheduleId);
		id.setGroupId(groupId);

		return opsScheduleSettingsJpaRepository.findById(id);
	}

	@Override
	public ResultList<OpsScheduleSettingsDto> getAll() {
		List<OpsScheduleSettings> settingList = opsScheduleSettingsJpaRepository
				.findByOrderByIdScheduleIdAscIdGroupIdAsc();
		if (CollectionUtils.isEmpty(settingList)) {
			return ResultList.ofSuccess(new ArrayList<>());
		}
		List<OpsScheduleSettingsDto> scheduleJobDtoList = settingList.stream().map(setting -> {
			OpsScheduleExecuteResult lastResult = opsScheduleExecuteResultJpaRepository
					.findFirstByGroupIdAndScheduleIdOrderByDateCreatedDesc(setting.getId().getGroupId(), setting
							.getId()
							.getScheduleId());

			OpsScheduleSettingsDto opsScheduleSettingsDto = new OpsScheduleSettingsDto();
			opsScheduleSettingsDto.setGroupId(setting.getId().getGroupId());
			opsScheduleSettingsDto.setScheduleId(setting.getId().getScheduleId());
			opsScheduleSettingsDto.setCategoryId(setting.getCategoryId());
			opsScheduleSettingsDto.setStatus(setting.getStatus());
			opsScheduleSettingsDto.setCron(setting.getCron());
			opsScheduleSettingsDto.setParameter(setting.getConfig());
			opsScheduleSettingsDto.setDateStarted(setting.getDateStarted());
			opsScheduleSettingsDto.setDateEnded(setting.getDateEnded());
			opsScheduleSettingsDto.setTimezone(setting.getTimezone());
			opsScheduleSettingsDto.setIgnoreMisfire(setting.getIgnoreMisfire());

			if (lastResult != null) {
				opsScheduleSettingsDto.setLastExecuteTime(lastResult.getTimeStarted());
				if (lastResult.getTimeFinished() != null) {
					opsScheduleSettingsDto
							.setCostMillSeconds(lastResult.getTimeFinished().getTime()
									- lastResult.getTimeStarted().getTime());
				} else {
					opsScheduleSettingsDto.setCostMillSeconds(0);
				}
			}

			if (ScheduleJobStatus.STARTED.name().equals(setting.getStatus())
					&& StringUtils.hasText(setting.getCron())) {
				CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
				try {
					cronTriggerImpl.setCronExpression(setting.getCron());
					List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 1);
					if (!CollectionUtils.isEmpty(dates)) {
						opsScheduleSettingsDto.setNextExecuteTime(new Timestamp(dates.get(0).getTime()));
					}
				} catch (ParseException e) {
					log.error("cron parse error", e);
				}
			}

			return opsScheduleSettingsDto;
		}).collect(Collectors.toList());
		return ResultList.ofSuccess(scheduleJobDtoList);
	}

	@Override
	public ResultList<OpsScheduleSettingsDto> getByGroupId(String groupId) {
		List<OpsScheduleSettings> settingList = opsScheduleSettingsJpaRepository
				.findByIdGroupIdOrderByIdScheduleIdAscIdGroupIdAsc(groupId);
		if (CollectionUtils.isEmpty(settingList)) {
			return ResultList.ofSuccess(new ArrayList<>());
		}
		List<OpsScheduleSettingsDto> scheduleJobDtoList = settingList.stream().map(setting -> {
			OpsScheduleExecuteResult lastResult = opsScheduleExecuteResultJpaRepository
					.findFirstByGroupIdAndScheduleIdOrderByDateCreatedDesc(setting.getId().getGroupId(), setting
							.getId()
							.getScheduleId());

			OpsScheduleSettingsDto opsScheduleSettingsDto = new OpsScheduleSettingsDto();
			opsScheduleSettingsDto.setGroupId(setting.getId().getGroupId());
			opsScheduleSettingsDto.setScheduleId(setting.getId().getScheduleId());
			opsScheduleSettingsDto.setCategoryId(setting.getCategoryId());
			opsScheduleSettingsDto.setStatus(setting.getStatus());
			opsScheduleSettingsDto.setCron(setting.getCron());
			opsScheduleSettingsDto.setParameter(setting.getConfig());
			opsScheduleSettingsDto.setDateStarted(setting.getDateStarted());
			opsScheduleSettingsDto.setDateEnded(setting.getDateEnded());
			opsScheduleSettingsDto.setTimezone(setting.getTimezone());
			opsScheduleSettingsDto.setIgnoreMisfire(setting.getIgnoreMisfire());

			if (lastResult != null) {
				opsScheduleSettingsDto.setLastExecuteTime(lastResult.getTimeStarted());
				if (lastResult.getTimeFinished() != null) {
					opsScheduleSettingsDto
							.setCostMillSeconds(lastResult.getTimeFinished().getTime()
									- lastResult.getTimeStarted().getTime());
				} else {
					opsScheduleSettingsDto.setCostMillSeconds(0);
				}
			}

			if (ScheduleJobStatus.STARTED.name().equals(setting.getStatus())
					&& StringUtils.hasText(setting.getCron())) {
				CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
				try {
					cronTriggerImpl.setCronExpression(setting.getCron());
					List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 1);
					if (!CollectionUtils.isEmpty(dates)) {
						opsScheduleSettingsDto.setNextExecuteTime(new Timestamp(dates.get(0).getTime()));
					}
				} catch (ParseException e) {
					log.error("cron parse error", e);
				}
			}

			return opsScheduleSettingsDto;
		}).collect(Collectors.toList());
		return ResultList.ofSuccess(scheduleJobDtoList);
	}

	@Override
	public ResultList<OpsScheduleSettingsDto> getAllAvailableJobs() {
		List<OpsScheduleSettingsDto> opsScheduleSettingsDtos = jobs.stream().map(item -> {
			OpsScheduleSettingsDto opsScheduleSettingsDto = new OpsScheduleSettingsDto();
			opsScheduleSettingsDto.setScheduleId(item.categoryId());
//			opsScheduleSettingsDto.setClassPath(item.getCN());
			return opsScheduleSettingsDto;
		}).toList();
		return ResultList.ofSuccess(opsScheduleSettingsDtos);
	}

	@Override
	public List<OpsScheduleSettings> getListByGroupId(String groupId) {
		return opsScheduleSettingsJpaRepository.findByIdGroupId(groupId);
	}

	@Override
	public ResultPage<OpsScheduleExecResultDto> getExecHistory(String groupId, String scheduleId, Pageable pageable) {
		Page<OpsScheduleExecuteResult> page = opsScheduleExecuteResultJpaRepository
				.findByGroupIdAndScheduleId(groupId, scheduleId, pageable);
		List<OpsScheduleExecResultDto> opsScheduleExecResultDtos = page
				.stream()
				.map(item -> ModelMapperUtil.INSTENCE.map(item, OpsScheduleExecResultDto.class))
				.toList();
		return ResultPage.ofSuccess(opsScheduleExecResultDtos, page);
	}

	@Override
	public Result<OpsScheduleExecResultDto> immdRunJobSetting(String scheduleId, String groupId) {

		OpsScheduleSettingsPk id = new OpsScheduleSettingsPk();
		id.setGroupId(groupId);
		id.setScheduleId(scheduleId);
		Optional<OpsScheduleSettings> opsScheduleSettingsOp = opsScheduleSettingsJpaRepository.findById(id);
		if (!opsScheduleSettingsOp.isPresent()) {
			return Result.ofError(400, "OpsScheduleSettings not found");
		}
		OpsScheduleSettings settings = opsScheduleSettingsOp.get();
		AbstractScheduleJob executable = findByCn(settings.getClassPath());
		JobExecutionContext context = new TestJobExecutionContext(settings.getConfig(), scheduleId, groupId);
		execute(executable, context);
		return Result.ofSuccess(null);
	}

	public void execute(AbstractScheduleJob job, JobExecutionContext context) {
		try {
			job.execute(context);
		} catch (JobExecutionException e) {
			log.error("execute", e);
		}
	}

	private AbstractScheduleJob findByCn(String cn) {
		for (AbstractScheduleJob abstractScheduleJob : jobs) {
			if (abstractScheduleJob.getCN().equals(cn)) {
				return abstractScheduleJob;
			}
		}
		return null;
	}

	private AbstractScheduleJob findImplByCategoryId(String categoryId) {
		for (AbstractScheduleJob abstractScheduleJob : jobs) {
			if (abstractScheduleJob.categoryId().equals(categoryId)) {
				return abstractScheduleJob;
			}
		}
		return null;
	}
}
