package com.james.autogpt.model;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class OpsScheduleSettingsPk implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String scheduleId;

	private String groupId;
}
