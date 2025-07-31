package com.james.autogpt.schedules;

import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.context.ApplicationContext;

public class OpsJobFactory implements JobFactory {

	private final ApplicationContext context;

	public OpsJobFactory(ApplicationContext context) {
		this.context = context;
	}

	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) {
		// Resolve the job from the Spring context
		return context.getBean(bundle.getJobDetail().getJobClass());
	}

}
