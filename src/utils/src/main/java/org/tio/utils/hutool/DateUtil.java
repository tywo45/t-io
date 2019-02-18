/**
 * 
 */
package org.tio.utils.hutool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tanyaowu
 *
 */
public class DateUtil {

	/**
	 * 
	 */
	public DateUtil() {
	}

	private static Date toDate(String dateStr, String patternStr) {
		DateFormat fmt = new SimpleDateFormat(patternStr);
		Date date;
		try {
			date = fmt.parse(dateStr);
		} catch (ParseException e) {
			return null;
		}
		return date;
	}

	public static String guessPattern(String dateStr) {
		// 去掉两边空格并去掉中文日期中的“日”，以规范长度
		dateStr = dateStr.trim().replace("日", "");
		int length = dateStr.length();

		if (Validator.isNumber(dateStr)) {
			// 纯数字形式
			if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
				return DatePattern.PURE_DATETIME_PATTERN;
			} else if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
				return DatePattern.PURE_DATETIME_MS_PATTERN;
			} else if (length == DatePattern.PURE_DATE_PATTERN.length()) {
				return DatePattern.PURE_DATE_PATTERN;
			} else if (length == DatePattern.PURE_TIME_PATTERN.length()) {
				return DatePattern.PURE_TIME_PATTERN;
			}
		}

		if (length == DatePattern.NORM_DATETIME_PATTERN.length() || length == DatePattern.NORM_DATETIME_PATTERN.length() + 1) {
			return DatePattern.NORM_DATETIME_PATTERN;
		} else if (length == DatePattern.NORM_DATE_PATTERN.length()) {
			return DatePattern.NORM_DATE_PATTERN;
		} else if (length == DatePattern.NORM_TIME_PATTERN.length() || length == DatePattern.NORM_TIME_PATTERN.length() + 1) {
			return DatePattern.NORM_TIME_PATTERN;
		} else if (length == DatePattern.NORM_DATETIME_MINUTE_PATTERN.length() || length == DatePattern.NORM_DATETIME_MINUTE_PATTERN.length() + 1) {
			return DatePattern.NORM_DATETIME_MINUTE_PATTERN;
		} else if (length >= DatePattern.NORM_DATETIME_MS_PATTERN.length() - 2) {
			return DatePattern.NORM_DATETIME_MS_PATTERN;
		}

		return null;
	}

	public static Date parseToDate(String dateStr) {
		String p = guessPattern(dateStr);
		if (p == null) {
			return null;
		}
		return toDate(dateStr, p);
	}

	public static java.sql.Date parseToSqlDate(String dateStr) {
		Date d = parseToDate(dateStr);
		if (d == null) {
			return null;
		}
		return new java.sql.Date(d.getTime());
	}

	public static java.sql.Timestamp parseToTimestamp(String dateStr) {
		Date d = parseToDate(dateStr);
		if (d == null) {
			return null;
		}
		return new java.sql.Timestamp(d.getTime());
	}

	public static java.sql.Time parseToTime(String dateStr) {
		Date d = parseToDate(dateStr);
		if (d == null) {
			return null;
		}
		return new java.sql.Time(d.getTime());
	}

}
