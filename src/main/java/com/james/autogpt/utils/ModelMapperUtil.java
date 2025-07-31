package com.james.autogpt.utils;

import org.modelmapper.ModelMapper;

public class ModelMapperUtil {

	private ModelMapperUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static final ModelMapper INSTENCE = new ModelMapper();

	static {
	}

}
