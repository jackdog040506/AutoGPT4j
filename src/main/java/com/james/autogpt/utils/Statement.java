package com.james.autogpt.utils;

import java.time.LocalDateTime;

import org.slf4j.Logger;

public class Statement {

	private final StringBuilder sb = new StringBuilder();
//	private final AtomicInteger lineNumber = new AtomicInteger(0);

	public void append(Logger logger, String raw, Object... args) {
//		int ln = 0;
//		synchronized (lineNumber) {
//			ln = lineNumber.addAndGet(1);
//		}
		String content = "ERROR";
		try {
			content = args.length == 0 ? raw : String.format(raw, args);
		} catch (Exception e) {
			content = raw;
		}
		sb
//				.append(ln)//
//				.append("\\t| ")
				.append(LocalDateTime.now().format(DateTimeUtil.ISO_FORMATTER))
				.append(" | ")
				.append(content)
				.append("\n");
		logger.info(content);
//		return ln;
	}

	public StringBuilder getSb() {
		return sb;
	}

	@Override
	public String toString() {
		return sb.toString();
	}

}
