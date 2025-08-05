package com.james.autogpt.schedules.jobs;

import java.sql.Timestamp;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.james.autogpt.model.OpsScheduleExecuteResult;
import com.james.autogpt.repository.OpsScheduleExecuteResultJpaRepository;
import com.james.autogpt.schedules.ScheduleExecuteStatus;
import com.james.autogpt.utils.Statement;

public abstract class AbstractScheduleJob implements Job {

	private OpsScheduleExecuteResultJpaRepository opsScheduleExecuteResultJpaRepository;

	@Autowired
	public final void setLogRepository(OpsScheduleExecuteResultJpaRepository opsScheduleExecuteResultJpaRepository) {
		this.opsScheduleExecuteResultJpaRepository = opsScheduleExecuteResultJpaRepository;
	}

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
//		String scheduleId = groupId() + "_" + scheduleId();
		String currentScheduleId = context.getJobDetail().getKey().getGroup() + "_"
				+ context.getJobDetail().getKey().getName();

		Statement runningStatement = new Statement();
		runningStatement.append(logger(), "[%s]schedule exec start", currentScheduleId);

		context
				.getMergedJobDataMap()
				.entrySet()
				.forEach(entry -> runningStatement
						.append(logger(), "[%s]%s:%s", currentScheduleId, entry.getKey(), entry.getValue()));

		OpsScheduleExecuteResult opsScheduleExecuteResult = new OpsScheduleExecuteResult();
		opsScheduleExecuteResult.setScheduleId(context.getJobDetail().getKey().getName());
		opsScheduleExecuteResult.setGroupId(context.getJobDetail().getKey().getGroup());
		opsScheduleExecuteResult.setStatus(ScheduleExecuteStatus.RUNNING.name());
		opsScheduleExecuteResult.setTimeStarted(new Timestamp(System.currentTimeMillis()));
		opsScheduleExecuteResultJpaRepository.save(opsScheduleExecuteResult);
		try {
			schedule(runningStatement, context);
			opsScheduleExecuteResult.setStatus(ScheduleExecuteStatus.FINISHED.name());
		} catch (Throwable t) {
			opsScheduleExecuteResult.setStatus(ScheduleExecuteStatus.EXCEPTION.name());
			opsScheduleExecuteResult.setErrorMsg(t.getMessage());
			runningStatement.append(logger(), "[%s]schedule exec error:%s", currentScheduleId, t.getMessage());
			logger().error("[{}]schedule exec error:", currentScheduleId, t);
		}
		opsScheduleExecuteResult.setTimeFinished(new Timestamp(System.currentTimeMillis()));
		runningStatement.append(logger(), "[%s]schedule exec finish", currentScheduleId);
		opsScheduleExecuteResult.setRunningStatement(runningStatement.getSb().toString());
		opsScheduleExecuteResultJpaRepository.save(opsScheduleExecuteResult);
	}

	public abstract void schedule(Statement runningStatement, JobExecutionContext context) throws JobExecutionException;

	public String getCN() {
		return this.getClass().getName();
	}

	public abstract String categoryId();

	abstract Logger logger();
}
