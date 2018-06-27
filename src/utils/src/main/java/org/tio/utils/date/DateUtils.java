package org.tio.utils.date;

import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.time.FastDateFormat;

/**
 * @author tanyaowu 
 * 2018年6月10日 上午7:58:23
 */
public class DateUtils {

	/**
	 * 当前时间生成符合http响应头中的Date格式的字符串
	 * @return
	 * @author tanyaowu
	 */
	public static String httpDate() {
		return httpDate(new Date());
	}

	private static final FastDateFormat HTTP_DATE_FORMATTER = FastDateFormat.getInstance("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);

	/**
	 * 把date生成符合http响应头中的Date格式的字符串
	 * @param date
	 * @return
	 * @author tanyaowu
	 */
	public static String httpDate(Date date) {
		//		SimpleDateFormat greenwichDate = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		return HTTP_DATE_FORMATTER.format(date);
	}
}
