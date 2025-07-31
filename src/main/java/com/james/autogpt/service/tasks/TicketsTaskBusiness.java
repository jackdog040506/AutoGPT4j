package com.james.autogpt.service.tasks;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.james.autogpt.dto.Result;
import com.james.autogpt.dto.TicketsTaskBusinessEntity;
import com.james.autogpt.model.OpsTickets;
import com.james.autogpt.model.OpsTicketsTask;
import com.james.autogpt.utils.ObjectMapperUtil;

public interface TicketsTaskBusiness<T extends TicketsTaskBusinessEntity> {

	TypeReference<T> getType();

	/**
	 * @param t
	 * @return store entity as map into task config
	 */
	default Map<String, Object> toMap(Object context) {
		return ObjectMapperUtil.toMap(context);
	}

	T initEntity();

	/**
	 * @param map
	 * @return reform entity by task config
	 */
	default T toEntity(Map<String, Object> config) {
		if (config == null) {
			return initEntity();
		}

		return ObjectMapperUtil.OM.convertValue(config, getType());
	}

	default Result<String> execute(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask) {
		T context = toEntity(opsTicketsTask.getConfig());
		Result<String> result = execute(opsTickets, context);
		if (result.isOk()) {
			Map<String, Object> alt = toMap(context);
			opsTicketsTask.setConfig(alt);
		}
		return result;
	}

	default Result<String> dispose(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask) {
		T context = toEntity(opsTicketsTask.getConfig());
		Result<String> result = dispose(opsTickets, context);
		if (result.isOk()) {
			Map<String, Object> alt = toMap(context);
			opsTicketsTask.setConfig(alt);
		}
		return result;
	}

	Result<String> execute(OpsTickets context, T args);

	Result<String> dispose(OpsTickets context, T args);

	String getTaskId();
}
