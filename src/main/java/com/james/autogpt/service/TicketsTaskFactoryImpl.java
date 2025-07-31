package com.james.autogpt.service;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.james.autogpt.dto.Result;
import com.james.autogpt.model.OpsTickets;
import com.james.autogpt.model.OpsTicketsTask;
import com.james.autogpt.service.tasks.TicketsTaskBusiness;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TicketsTaskFactoryImpl implements TicketsTaskFactory {

	private final Map<String, TicketsTaskBusiness<?>> taskTemplates = new LinkedHashMap<>();

	@Autowired
	public void setTaskTemplates(@Lazy Collection<TicketsTaskBusiness<?>> ticketsTaskBusinesses) {
		for (TicketsTaskBusiness<?> ticketsTaskBusiness : ticketsTaskBusinesses) {
			taskTemplates.put(ticketsTaskBusiness.getTaskId(), ticketsTaskBusiness);
		}
	}

	@Override
	public Result<String> executeTask(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask) {

		TicketsTaskBusiness<?> template = taskTemplates.get(opsTicketsTask.getTaskTemplateId());
		if (template == null) {
			throw new NullPointerException(
					String.format("template ID not found %s", opsTicketsTask.getTaskTemplateId()));
		}
		return template.execute(opsTickets, opsTicketsTask);
	}

	@Override
	public Result<String> disposeTask(OpsTickets opsTickets, OpsTicketsTask opsTicketsTask) {
		TicketsTaskBusiness<?> template = taskTemplates.get(opsTicketsTask.getTaskTemplateId());
		if (template == null) {
			throw new NullPointerException(
					String.format("template ID not found %s", opsTicketsTask.getTaskTemplateId()));
		}
		return template.dispose(opsTickets, opsTicketsTask);
	}

	@Override
	public void saveConfig(OpsTicketsTask opsTicketsTask, Object object) {
		TicketsTaskBusiness<?> template = taskTemplates.get(opsTicketsTask.getTaskTemplateId());
		if (template == null) {
			throw new NullPointerException(
					String.format("template ID not found %s", opsTicketsTask.getTaskTemplateId()));
		}
		Map<String, Object> config = template.toMap(object);
//		ValidationResult validationResult = template.validation(opsTicketsTask, config);
//		log.info("[{}] validation {} -> {}", //
//				opsTicketsTask.getTaskTemplateId(), //
//				validationResult.isValid(), //
//				validationResult.message());
		opsTicketsTask.setConfig(config);
//		opsTicketsTask.setValidToExecute(validationResult.isValid());
	}

	@Override
	public Object getTaskConfig(OpsTicketsTask opsTicketsTask) {
		TicketsTaskBusiness<?> template = taskTemplates.get(opsTicketsTask.getTaskTemplateId());
		if (template == null) {
			throw new NullPointerException(
					String.format("template ID not found %s", opsTicketsTask.getTaskTemplateId()));
		}
		return template.toEntity(opsTicketsTask.getConfig());
	}

	@Override
	public <T> T getTaskConfig(OpsTicketsTask opsTicketsTask, Class<T> caster) {
		Object obj = getTaskConfig(opsTicketsTask);
		return caster.cast(obj);
	}

}
