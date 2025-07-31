package com.james.autogpt.schedules.jobs;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.james.autogpt.utils.Statement;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@AllArgsConstructor
public class TradeDecisioning extends AbstractScheduleJob {
	@Override
	public void schedule(Statement runningStatement, JobExecutionContext context) throws JobExecutionException {
		// TODO Auto-generated method stub

	}

	@Override
	public String categoryId() {
		return TradeDecisioning.class.getSimpleName();
	}

	@Override
	Logger logger() {
		return log;
	}
}
