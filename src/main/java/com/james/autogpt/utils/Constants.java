package com.james.autogpt.utils;

public class Constants {
	public static final int API_RESULT_STATUS_SUCCESS = 0;

	public static final String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

	public static final String TICKETS_TASK_STATUS_DRAFT = "DRAFT";
	// IN-PROGRESS
	public static final String TICKETS_TASK_STATUS_INPROGRESS = "IN-PROGRESS";
	// TICKETS_TASK_STATUS_COMPLETE
	public static final String TICKETS_TASK_STATUS_COMPLETE = "COMPLETE";
	public static final String TICKETS_TASK_STATUS_DISPOSED = "DISPOSED";

	public static final String TICKETS_TASK_EXEC_STATUS_INVALID = "INVALID";
	public static final String TICKETS_TASK_EXEC_STATUS_SUCCESS = "SUCCESS";
	public static final String TICKETS_TASK_EXEC_STATUS_ERROR = "ERROR";


	public static final String AGENTIC_CYCLE_THINK_CORE_PROMPT = "You're at step 'Think' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle. \n"
			+ "Collects and concentrates sources of truth to prepare for reasoning like What is the task? or What's known?";
	public static final String AGENTIC_CYCLE_REASON_CORE_PROMPT = "You're at step 'Reason' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle. \n"
			+ "Accroding Result from Think step, you will generates a focused breakdown like What is the task? or What's known?";
	public static final String AGENTIC_CYCLE_PLAN_CORE_PROMPT = "You're at step 'Plan' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle. \n"
			+ "Accroding Result from Reason step, you will plan for the tasks current subgoal when task need to drill down item by item";
	// public static final String AGENTIC_CYCLE_CRITICIZE_CORE_PROMPT = "You're at step 'Criticize' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle. \n"
	// 		+ "Criticizes the plan for the current subgoal like What is the task? or What's known?";
	public static final String AGENTIC_CYCLE_ACT_CORE_PROMPT = "You're at step 'Act' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle";
	public static final String AGENTIC_CYCLE_SPEAK_CORE_PROMPT = "You're at step 'Speak' of the Agentic Loop. cycle will be 'Think → Reason → Plan → Criticize → Act (Command) → Speak' cycle";

}
