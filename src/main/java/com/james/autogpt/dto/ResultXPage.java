package com.james.autogpt.dto;

import java.util.Collection;

import org.springframework.data.domain.Page;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResultXPage<T> extends ResultPage<T>  {
	private Long unReadElements;

	public static <T> ResultXPage<T> ofSuccess(Collection<T> collection, Page<?> data) {
		ResultXPage<T> result = new ResultXPage<>();
		result.setCode(200);
		result.setData(collection);
		result.setMessage("OK");
		result.importPageInfo(data);
		return result;
	}
	public static <T> ResultXPage<T> ofSuccess(Collection<T> collection, Page<?> data, Long unReadElements) {
		ResultXPage<T> result = new ResultXPage<>();
		result.setCode(200);
		result.setData(collection);
		result.setMessage("OK");
		result.importPageInfo(data);
		result.setUnReadElements(unReadElements);
		return result;
	}

	public static <T> ResultXPage<T> ofError(int code, String message) {
		ResultXPage<T> result = new ResultXPage<>();
		result.setCode(code);
		result.setMessage(message);
		return result;
	}
}
