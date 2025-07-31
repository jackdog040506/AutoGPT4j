package com.james.autogpt.dto;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import com.james.autogpt.utils.MessageUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResultPage<T> extends ResultList<T> {
	private Integer currentPage = 0;
	private Integer totalPages = 0;
	private Long totalElements = 0L;
	private Integer size = 0;

	public void importPageInfo(Page<?> data) {
		this.setCurrentPage(data.getNumber());
		this.setTotalPages(data.getTotalPages());
		this.setTotalElements(data.getTotalElements());
		this.setSize(data.getSize());
	}

	public void importPageInfo(ResultPage<?> data) {
		this.setCurrentPage(data.getCurrentPage());
		this.setTotalPages(data.getTotalPages());
		this.setTotalElements(data.getTotalElements());
		this.setSize(data.getSize());
	}

	public static <T> ResultPage<T> ofSuccess(Collection<T> collection, Page<?> data) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(200);
		result.setData(collection);
		result.setMessage("OK");
		result.importPageInfo(data);
		return result;
	}

	public static <T> ResultPage<T> ofSuccess(Collection<T> collection, ResultPage<?> data) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(200);
		result.setData(collection);
		result.setMessage("OK");
		result.importPageInfo(data);
		return result;
	}

	public static <T> ResultPage<T> ofSuccess(Page<T> data) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(200);
		result.setData(data.toList());
		result.setMessage("OK");
		result.importPageInfo(data);
		return result;
	}

	public static <T> ResultPage<T> ofSuccess(Collection<T> data) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(200);
		result.setData(data);
		result.setMessage("OK");
		return result;
	}

	public static <T> ResultPage<T> ofError(int code, String message) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}

	public static <T> ResultPage<T> ofError(HttpStatus httpStatus, String message) {
		return ofError(httpStatus.value(), message);
	}
	
	public static <T> ResultPage<T> ofErrorCode(int code, String messageCode, Object... args) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(code);
		result.setMessage(MessageUtils.getMessage(messageCode, args));
		return result;
	}

	public static <T> ResultPage<T> ofError(Result<?> transients) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

	public static <T> ResultPage<T> ofError(ResultList<?> transients) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}

	public static <T> ResultPage<T> ofError(ResultPage<?> transients) {
		ResultPage<T> result = new ResultPage<>();
		result.setCode(transients.getCode());
		result.setMessage(transients.getMessage());
		return result;
	}
}
