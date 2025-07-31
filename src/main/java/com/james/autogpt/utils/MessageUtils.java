package com.james.autogpt.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.StringUtils;

public class MessageUtils {

	private MessageUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static final String DEFAULT_ENCODING = "UTF-8";
	public static final Locale DEFAULT_LOCALE = Locale.CHINESE;

	private static final ResourceBundleMessageSource resourceBundle;

	static {
		resourceBundle = new ResourceBundleMessageSource();
		resourceBundle.setDefaultEncoding(DEFAULT_ENCODING);
		resourceBundle.setBasename("message");
		resourceBundle.setDefaultLocale(DEFAULT_LOCALE);
		resourceBundle.setUseCodeAsDefaultMessage(true);
	}

	public static String getMessage(String code, Object... args) {
		if (code == null) {
			return code;
		}
		return resourceBundle.getMessage(code, args, getLocale());
	}

	public static Locale getLocale() {
		return LocaleContextHolder.getLocale();
	}

	public static boolean isDefaultLocale() {
		return DEFAULT_LOCALE.equals(getLocale());
	}

	public static Set<String> getCandidateLocales() {
		Set<String> locales = new HashSet<>();
		if (isDefaultLocale()) {
			return locales;
		}
		Locale locale = getLocale();
		if (StringUtils.hasText(locale.getLanguage())) {
			locales.add(locale.getLanguage().toLowerCase());
		}
		if (StringUtils.hasText(locale.getLanguage()) && StringUtils.hasText(locale.getCountry())) {
			locales.add(String.format("%s_%s", locale.getLanguage(), locale.getCountry()).toLowerCase());
		}
		return locales;
	}

}
