package com.james.autogpt.dto;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.james.autogpt.utils.Constants;
import com.james.autogpt.utils.MessageUtils;

import lombok.ToString;

@ToString(of = { "code", "message", "tid" })
public class Result<T> {

	private int code;
	private String message;
	private T data;
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

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public static <T> Result<T> ofSuccess(T data) {
		Result<T> result = new Result<>();
		result.setCode(Constants.API_RESULT_STATUS_SUCCESS);
		result.setData(data);
		result.setMessage("OK");
		return result;
	}

	public static <T> Result<T> ofSuccess(T data, String msg) {
		Result<T> result = new Result<>();
		result.setCode(Constants.API_RESULT_STATUS_SUCCESS);
		result.setData(data);
		result.setMessage(msg);
		return result;
	}

	public static <T> Result<T> ofError(int code, String message) {
		Result<T> result = new Result<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

	public static <T> Result<T> ofError(HttpStatus code, String message) {
		return ofError(code.value(), message);
	}

	public static <T> Result<T> ofUnauth() {
		Result<T> result = new Result<>();
		result.setCode(403);
		result.setMessage("no permission");
		return result;
	}

	public static <T> Result<T> ofErrorCode(int code, String messageCode, Object... args) {
		String out = MessageUtils.getMessage(messageCode, args);
		return ofError(code, out);
	}

	public static <T> Result<T> ofErrorCode(HttpStatus code, String messageCode, Object... args) {
		return ofErrorCode(code.value(), messageCode, args);
	}

	public static <T> Result<T> ofError(Result<?> transients) {
		Result<T> result = new Result<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

	public static <T> Result<T> ofError(ResultList<?> transients) {
		Result<T> result = new Result<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

}
