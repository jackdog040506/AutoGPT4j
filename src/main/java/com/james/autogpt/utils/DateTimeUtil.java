package com.james.autogpt.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

	private DateTimeUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static ZonedDateTime convertToZonedDateTime(Timestamp timestamp) {
		return timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
	}

	public static final DateTimeFormatter ISO_FORMATTER_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	public static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final DateTimeFormatter DT_ORDER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

	// ecpay
	public static final ThreadLocal<DateFormat> PAYMENT_ISO_FORMAT = new ThreadLocal<>() {

		@Override
		public SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		}

	};

	public static String timestampToIsoString(Timestamp timestamp, String zoneId) {
		return java.time.Instant
				.ofEpochMilli(timestamp.getTime())
				.atZone(java.time.ZoneId.of(zoneId))
				.format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME);
	}

//	public static boolean isTimestampBetween(Timestamp timestamp, Timestamp start, Timestamp end) {
//		return timestamp.after(start) && timestamp.before(end);
//	}

	public static boolean isTimestampBetween(Timestamp timestamp, Timestamp start, Timestamp end) {
		return !timestamp.before(start) && !timestamp.after(end);
	}

}
