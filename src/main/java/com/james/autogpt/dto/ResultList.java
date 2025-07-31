package com.james.autogpt.dto;

import java.util.Collection;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.james.autogpt.utils.Constants;
import com.james.autogpt.utils.MessageUtils;

import lombok.ToString;

@ToString(of = { "code", "message", "tid" })
public class ResultList<T> {

	private int code;
	private String message;
	private Collection<T> data;
	private String tid;

	@JsonIgnore
	public boolean isOk() {
		return Constants.API_RESULT_STATUS_SUCCESS == code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Collection<T> getData() {
		return data;
	}

	public void setData(Collection<T> data) {
		this.data = data;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public static <T> ResultList<T> ofSuccess(Collection<T> data) {
		ResultList<T> result = new ResultList<>();
		result.setCode(200);
		result.setData(data);
		result.setMessage("OK");
		return result;
	}

	public static <T> ResultList<T> ofError(int code, String message) {
		ResultList<T> result = new ResultList<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

	public static <T> ResultList<T> ofError(HttpStatus httpStatus, String message) {
		return ofError(httpStatus.value(), message);
	}

	public static <T> ResultList<T> ofErrorCode(int code, String messageCode, Object... args) {
		String out = MessageUtils.getMessage(messageCode, args);
		return ofError(code, out);
	}

	public static <T> ResultList<T> ofError(Result<?> transients) {
		ResultList<T> result = new ResultList<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

	public static <T> ResultList<T> ofError(ResultList<?> transients) {
		ResultList<T> result = new ResultList<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

}
