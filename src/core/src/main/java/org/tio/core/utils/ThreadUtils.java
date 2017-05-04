package org.tio.core.utils;

public class ThreadUtils {
	public static String stackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < elements.length; i++) {
			buf.append("\r\n	").append(elements[i].getClassName()).append(".").append(elements[i].getMethodName()).append("(").append(elements[i].getFileName()).append(":")
					.append(elements[i].getLineNumber()).append(")");
		}
		return buf.toString();
	}
}
