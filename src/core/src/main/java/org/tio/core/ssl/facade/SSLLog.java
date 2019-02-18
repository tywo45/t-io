package org.tio.core.ssl.facade;

/**
 *
 */
public class SSLLog {

	private static boolean debugEnabled = false;

	public static void setDebugEnabled(boolean debugEnabled) {
		SSLLog.debugEnabled = debugEnabled;
	}

	public static boolean isDebugEnabled() {
		return debugEnabled;
	}

	public static void debug(final String tag, final String message, final String... args) {
		if (debugEnabled) {
			//System.out.println(String.format("[%s]: ", tag) + String.format(message, args));
		}
	}
}
