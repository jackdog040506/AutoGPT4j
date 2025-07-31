package com.james.autogpt.schedules.jobs;

import java.util.Date;
import java.util.Map;

import org.quartz.Calendar;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class TestJobExecutionContext implements JobExecutionContext {

	private JobDataMap jobDataMap;
	private JobDetail jobDetail;

	public TestJobExecutionContext(Map<?, ?> map, String scheduleId, String groupId) {
		super();
		this.jobDataMap = new JobDataMap(map);
		this.jobDetail = new JobDetail() {

			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public JobKey getKey() {
				return new JobKey(scheduleId, groupId);
			}

			@Override
			public String getDescription() {

				return null;
			}

			@Override
			public Class<? extends Job> getJobClass() {

				return null;
			}

			@Override
			public JobDataMap getJobDataMap() {

				return null;
			}

			@Override
			public boolean isDurable() {

				return false;
			}

			@Override
			public boolean isPersistJobDataAfterExecution() {

				return false;
			}

			@Override
			public boolean isConcurrentExecutionDisallowed() {
				return false;
			}

			@Override
			public boolean requestsRecovery() {

				return false;
			}

			@Override
			public JobBuilder getJobBuilder() {

				return null;
			}

			@Override
			public Object clone() {
				return null;
			}

		};

	}

	@Override
	public Scheduler getScheduler() {

		return null;
	}

	@Override
	public Trigger getTrigger() {

		return null;
	}

	@Override
	public Calendar getCalendar() {

		return null;
	}

	@Override
	public boolean isRecovering() {

		return false;
	}

	@Override
	public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {

		return null;
	}

	@Override
	public int getRefireCount() {

		return 0;
	}

	@Override
	public JobDataMap getMergedJobDataMap() {
		return jobDataMap;
	}

	@Override
	public JobDetail getJobDetail() {
		return jobDetail;
	}

	@Override
	public Job getJobInstance() {

		return null;
	}

	@Override
	public Date getFireTime() {

		return null;
	}

	@Override
	public Date getScheduledFireTime() {

		return null;
	}

	@Override
	public Date getPreviousFireTime() {

		return null;
	}

	@Override
	public Date getNextFireTime() {

		return null;
	}

	@Override
	public String getFireInstanceId() {

		return null;
	}

	@Override
	public Object getResult() {

		return null;
	}

	@Override
	public void setResult(Object result) {

	}

	@Override
	public long getJobRunTime() {

		return 0;
	}

	@Override
	public void put(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object get(Object key) {
		throw new UnsupportedOperationException();
	}

}
