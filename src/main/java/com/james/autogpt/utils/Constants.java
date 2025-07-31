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

	public static final String PROMPT_CONDENSE_NEWS_EVENT = "You are a helpful assistant that condenses news events into a concise summary."
			+ "give a concise summary of the news event in a single sentence, then return format \r\n"
			+ "  INPUT: \r\n"
			+ "    newsContent: {newsContent} \r\n"
			+ "  OUTPUT: \r\n"
			+ "    format: {format}";
}
