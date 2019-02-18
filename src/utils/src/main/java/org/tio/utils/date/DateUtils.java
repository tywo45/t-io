package org.tio.utils.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author tanyaowu 2018年6月10日 上午7:58:23
 */
public class DateUtils {

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
}
