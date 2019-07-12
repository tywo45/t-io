/**
 * 
 */
package org.tio.utils.hutool;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

	/**
	 * 当前时间生成符合http响应头中的Date格式的字符串
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public static String httpDate() {
		return httpDate(new Date());
	}

	// private static final FastDateFormat HTTP_DATE_FORMATTER = FastDateFormat.getInstance("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	/**
	 * 把date生成符合http响应头中的Date格式的字符串
	 * 
	 * @param date
	 * @return
	 * @author tanyaowu
	 */
	public static String httpDate(Date date) {
		SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		return greenwichDate.format(date);
		// return HTTP_DATE_FORMATTER.format(date);
	}

	public static String httpDate(long millis) {
		return httpDate(new Date(millis));
		// return HTTP_DATE_FORMATTER.format(millis);
	}

	/**
	 * 格式化日期时间<br>
	 * 格式 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date 被格式化的日期
	 * @return 格式化后的日期
	 */
	public static String formatDateTime(Date date) {
		if (null == date) {
			return null;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 两个日期相隔的天数
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int daysBetween(Date date1, Date date2) {
		long time1 = date1.getTime();
		long time2 = date2.getTime();
		long iv = (time2 - time1) / (1000 * 3600 * 24);
		return Integer.parseInt(String.valueOf(iv));
	}

}
