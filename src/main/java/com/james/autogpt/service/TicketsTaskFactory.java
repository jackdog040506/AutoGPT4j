package com.james.autogpt.service;

import com.james.autogpt.dto.Result;
import com.james.autogpt.model.OpsTickets;
import com.james.autogpt.model.OpsTicketsTask;

public interface TicketsTaskFactory {

	void saveConfig(OpsTicketsTask opsTicketsTask, Object object);

	Result<String> executeTask(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask);

	Result<String> disposeTask(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask);

	Object getTaskConfig(OpsTicketsTask opsTicketsTask);

	<T> T getTaskConfig(OpsTicketsTask opsTicketsTask, Class<T> caster);

}
