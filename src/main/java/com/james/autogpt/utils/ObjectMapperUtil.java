package com.james.autogpt.utils;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ObjectMapperUtil {
	private ObjectMapperUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static final ObjectMapper controllerOM = new ObjectMapper();
	public static final ObjectMapper OM = new ObjectMapper();

	public static final ObjectMapper redisOM = new ObjectMapper();
	static {
		controllerOM.registerModule(new JavaTimeModule());
		controllerOM.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		OM.registerModule(new JavaTimeModule());

		redisOM.activateDefaultTyping(//
				redisOM.getPolymorphicTypeValidator(), //
				ObjectMapper.DefaultTyping.NON_FINAL, //
				JsonTypeInfo.As.PROPERTY);
		redisOM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static final TypeReference<Map<String, Object>> TYPE_MAP = new TypeReference<>() {
	};

	public static Map<String, Object> toMap(Object obj) {
		return OM.convertValue(obj, new TypeReference<Map<String, Object>>() {
		});
	}

	public static String writeValueAsString(Object object) {
		try {
			return OM.writeValueAsString(object);
		} catch (Exception e) {
			return String.format("parse error %s", e.getMessage());
		}
	}

	public static <T> T readValue(String raw, Class<T> clazz) {
		try {
			return OM.readValue(raw, clazz);
		} catch (Exception e) {
			log.error(String.format("parse error %s", e.getMessage()));
			return null;
		}
	}

}
