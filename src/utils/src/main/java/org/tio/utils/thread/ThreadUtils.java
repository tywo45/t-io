package org.tio.utils.thread;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:41:46
 */
public class ThreadUtils {
	public static String stackTrace() {
		StackTraceElement[] elements = Thread.currentThread().getStackTrace();
		StringBuilder buf = new StringBuilder();
		for (StackTraceElement element : elements) {
			buf.append("\r\n	").append(element.getClassName()).append(".").append(element.getMethodName()).append("(").append(element.getFileName()).append(":")
			        .append(element.getLineNumber()).append(")");
		}
		return buf.toString();
	}
}
