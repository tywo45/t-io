package org.tio.utils.date;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 弄个全局的日期格式化类，这里的对象是线程安全的
 * @author tanyaowu
 */
public class DateFmt {
	public static final DateTimeFormatter	yyyyMMddHHmmssSSS		= DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
	public static final DateTimeFormatter	yyyyMMddHHmmss			= DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	public static final DateTimeFormatter	yyyyMMddHHmm			= DateTimeFormatter.ofPattern("yyyyMMddHHmm");
	public static final DateTimeFormatter	yyyyMMddHH				= DateTimeFormatter.ofPattern("yyyyMMddHH");
	public static final DateTimeFormatter	yyyyMMdd				= DateTimeFormatter.ofPattern("yyyyMMdd");
	public static final DateTimeFormatter	yyyyMM					= DateTimeFormatter.ofPattern("yyyyMM");
	public static final DateTimeFormatter	yyyy					= DateTimeFormatter.ofPattern("yyyy");
	public static final DateTimeFormatter	HHmmss					= DateTimeFormatter.ofPattern("HHmmss");
	public static final DateTimeFormatter	HHmm					= DateTimeFormatter.ofPattern("HHmm");
	public static final DateTimeFormatter	HH						= DateTimeFormatter.ofPattern("HH");
	public static final DateTimeFormatter	yyyy_MM_dd_HHmmssSSS	= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	public static final DateTimeFormatter	yyyy_MM_dd_HHmmss		= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private static final Map<String, DateTimeFormatter> map = new HashMap<>();
	static {
		map.put("yyyyMMddHHmmssSSS", yyyyMMddHHmmssSSS);
		map.put("yyyyMMddHHmmss", yyyyMMddHHmmss);
		map.put("yyyyMMddHHmm", yyyyMMddHHmm);
		map.put("yyyyMMddHH", yyyyMMddHH);
		map.put("yyyyMMdd", yyyyMMdd);
		map.put("yyyyMM", yyyyMM);
		map.put("yyyy", yyyy);
		map.put("HHmmss", HHmmss);
		map.put("HHmm", HHmm);
		map.put("HH", HH);

		map.put("yyyy_MM_dd_HHmmssSSS", yyyy_MM_dd_HHmmssSSS);
		map.put("yyyy_MM_dd_HHmmss", yyyy_MM_dd_HHmmss);
	}

	/**
	 * 
	 * @param pattern
	 * @return
	 */
	public static final DateTimeFormatter of(String pattern) {
		DateTimeFormatter ret = map.get(pattern);
		if (ret != null) {
			return ret;
		}
		ret = DateTimeFormatter.ofPattern(pattern);
		map.put(pattern, ret);
		return ret;
	}
}
