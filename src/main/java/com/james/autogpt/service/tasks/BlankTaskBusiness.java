package com.james.autogpt.service.tasks;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.task.BlankTaskBusinessContext;
import com.james.autogpt.model.OpsTickets;

@Component
public class BlankTaskBusiness implements TicketsTaskBusiness<BlankTaskBusinessContext> {
	public static final String TASK_ID = "BLANK";

	@Override
	public TypeReference<BlankTaskBusinessContext> getType() {
		return new TypeReference<>() {
		};
	}

	@Override
	public BlankTaskBusinessContext initEntity() {
		return new BlankTaskBusinessContext();
	}

	@Override
	public Result<String> execute(OpsTickets context, BlankTaskBusinessContext args) {
		return Result.ofSuccess("no operation");
	}

	@Override
	public Result<String> dispose(OpsTickets context, BlankTaskBusinessContext args) {
		return Result.ofSuccess("no operation");
	}

	@Override
	public String getTaskId() {
		return TASK_ID;
	}

}
