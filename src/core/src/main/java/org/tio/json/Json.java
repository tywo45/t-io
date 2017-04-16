package org.tio.json;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月16日 上午11:36:53
 */
public abstract class Json
{
	private static Logger log = LoggerFactory.getLogger(Json.class);

	private static SerializeConfig mapping = new SerializeConfig();

	static
	{
		mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Time.class, new SimpleDateFormatSerializer("HH:mm:ss"));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String xtttx = toJson(null);

		//		Timestamp Timestamp = new Timestamp(System.currentTimeMillis());
		//		intArr.getClass().isArray();
		//		Json.jsonStringToBean_2("y", String.class);

		int[] intArr3 = new int[] { 4, 2 };

		Map<String, Object> map = new HashMap<>();
		map.put("name", "tanyaowu");
		map.put("id", "tanyaowu");

		String jsonString = Json.toJson(intArr3);
		System.out.println(jsonString);

		int[] xx = Json.toBean(jsonString, int[].class);
		System.out.println(xx);

		jsonString = Json.toJsonByExcludes(map, "name");
		System.out.println(jsonString);

		jsonString = Json.toJsonByIncludes(map, "name");
		System.out.println(jsonString);

	}

	/**
	 * 设置java.util.Date和java.sql.Date的格式(用于fastjson)
	 * @param format
	 */
	public static void setDateFormat(String format)
	{
		mapping.put(Date.class, new SimpleDateFormatSerializer(format));
		mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer(format));
	}

	/**
	 * 设置java.sql.Time的格式(用于fastjson)
	 * @param format
	 */
	public static void setTimeFormat(String format)
	{
		mapping.put(java.sql.Time.class, new SimpleDateFormatSerializer(format));
	}

	/**
	 * 设置java.sql.Timestamp的格式(用于fastjson)
	 * @param format
	 */
	public static void setTimestampFormat(String format)
	{
		mapping.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer(format));
	}

	/**
	 * fastjson版
	 * @param <T>
	 * @param jsonString
	 * @param t
	 * @return
	 */
	public static <T> T toBean(String jsonString, Class<T> tt)
	{
		try
		{
			if (StringUtils.isBlank(jsonString))
			{
				return null;
			}

			T t = JSON.parseObject(jsonString, tt);
			return t;
		} catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static <T> List<T> toList(String jsonString, Class<T> tt)
	{
		try
		{
			if (StringUtils.isBlank(jsonString))
			{
				return null;
			}

			List<T> ts = JSON.parseArray(jsonString, tt);
			return ts;
		} catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	//	private static JsonConfig defaultJsonConfig = null;
	//
	//	public static JsonConfig getDefaultJsonConfig() {
	//		if (defaultJsonConfig == null) {
	//			defaultJsonConfig = new JsonConfig();
	//			JsonValueProcessor jsonValueProcessor = new JsonValueProcessor() {
	//				private final String format = "yyyy-MM-dd";
	//				SimpleDateFormat yyyyMMddFormat = new SimpleDateFormat(format);
	//
	//				private final String format1 = format + " HH:mm:ss"; // yyyy-MM-dd
	//																		// HH:mm:ss.SSS
	//				SimpleDateFormat yyyyMMddHmsFormat = new SimpleDateFormat(format1);
	//
	//				private final String format2 = "HH:mm:ss";
	//				SimpleDateFormat hmsFormat = new SimpleDateFormat(format2);
	//
	//				public Object processObjectValue(String key, Object value, JsonConfig arg2) {
	//					String ret = "";
	//					if (value == null) {
	//						return "";
	//					} else if (value instanceof java.sql.Timestamp) {
	//						ret = yyyyMMddHmsFormat.format((Date) value);
	//					} else if (value instanceof java.sql.Time) {
	//						ret = hmsFormat.format((Date) value);
	//					} else if (value instanceof java.util.Date) {
	//						ret = yyyyMMddFormat.format((Date) value);
	//					} else if (value instanceof oracle.sql.TIMESTAMP) {
	//						oracle.sql.TIMESTAMP ts = (oracle.sql.TIMESTAMP) value;
	//						java.sql.Timestamp ts1 = null;
	//						try {
	//							ts1 = ts.timestampValue();
	//						} catch (SQLException e) {
	//							log.error(e.toString(), e);
	//						}
	//
	//						ret = yyyyMMddHmsFormat.format(new Date(ts1.getTime()));
	//					}
	//					return ret;
	//				}
	//
	//				public Object processArrayValue(Object value, JsonConfig arg1) {
	//					return null;
	//				}
	//			};
	//			defaultJsonConfig.registerJsonValueProcessor(java.util.Date.class, jsonValueProcessor);
	//			defaultJsonConfig.registerJsonValueProcessor(java.sql.Date.class, jsonValueProcessor);
	//			defaultJsonConfig.registerJsonValueProcessor(java.sql.Time.class, jsonValueProcessor);
	//			defaultJsonConfig.registerJsonValueProcessor(java.sql.Timestamp.class, jsonValueProcessor);
	//
	//			PropertyFilter propertyFilter = new PropertyFilter() {
	//				public boolean apply(Object source, String name, Object value) {
	//					if (source instanceof JsonFilter) {
	//						JsonFilter jsonFilter = (JsonFilter) source;
	//						return !jsonFilter.accept(name, value);
	//					}
	//					return false;
	//				}
	//			};
	//			defaultJsonConfig.setJsonPropertyFilter(propertyFilter);
	//		}
	//		return defaultJsonConfig;
	//	}

	//	public static String beanToJsonString(Object bean, JsonConfig jsonConfig) {
	//		if (jsonConfig == null) {
	//			jsonConfig = getDefaultJsonConfig();
	//		}
	//
	//		if (bean == null) {
	//			return "";
	//		} else if (bean instanceof String) {
	//			return "" + bean;
	//		}
	//
	//		try {
	//			if (JSONUtils.isArray(bean)) {
	//				return JSONArray.fromObject(bean, jsonConfig).toString();
	//			} else {
	//				return JSONObject.fromObject(bean, jsonConfig).toString();
	//			}
	//		} catch (Exception e) {
	//			log.error(e.toString(), e);
	//			throw new RuntimeException(e.toString(), e);
	//		}
	//	}

	/**
	 * 
	 * @param bean
	 * @return
	 */
	//	public static String beanToJsonString(Object bean) {
	//		return beanToJsonString(bean, getDefaultJsonConfig());
	//	}

	/**
	 * jackson
	 * @param bean
	 * @return
	 */
	//	private static String beanToJsonString1(Object bean) {
	//		ObjectMapper mapper = new ObjectMapper();
	//		try {
	//			return mapper.writeValueAsString(bean);
	//		} catch (JsonGenerationException e) {
	//			log.error(e.toString(), e);
	//			throw new RuntimeException(e.toString(), e);
	//		} catch (IOException e) {
	//			log.error(e.toString(), e);
	//			throw new RuntimeException(e.toString(), e);
	//		}
	//	}

	/**
	 * fastjson版
	 * 
	 * @param bean
	 * @return
	 */
	public static String toJson(Object bean)
	{
		try
		{
			return JSON.toJSONString(bean, mapping, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * 
	 * @param bean
	 * @param excludeKeys
	 * @return
	 */
	public static String toJsonByExcludes(Object bean, final String... excludeKeys)
	{
		SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();

		for (String key : excludeKeys)
		{
			simplePropertyPreFilter.getExcludes().add(key);
		}

		try
		{
			return JSON.toJSONString(bean, simplePropertyPreFilter, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	public static String toJsonByIncludes(Object bean, final String... includeKeys)
	{
		SimplePropertyPreFilter simplePropertyPreFilter = new SimplePropertyPreFilter();

		for (String key : includeKeys)
		{
			simplePropertyPreFilter.getIncludes().add(key);
		}

		try
		{
			return JSON.toJSONString(bean, simplePropertyPreFilter, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e)
		{
			log.error("", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 */
	public Json()
	{

	}

	/**
	 * 
	 * @param <T>
	 * @param jsonString
	 * @param t
	 * @return
	 */
	//	public static <T> T jsonStringToBean(String jsonString, Class<T> t) {
	//		try {
	//			if (jsonString == null || "".equals(jsonString)) {
	//				return null;
	//			}
	//			ObjectMapper mapper = new ObjectMapper();
	//			T obj = (T) mapper.readValue(jsonString, t);
	//			return obj;
	//		} catch (Exception e) {
	//			log.error(e.toString(), e);
	//			throw new RuntimeException(e.toString(), e);
	//		}
	//	}

	/**
	 * 
	 * @param <T>
	 * @param jsonString
	 * @param t
	 * @return
	 */
	//	public static <T> T jsonStringToBean2(String jsonString, Class<T> t) {
	//		try {
	//			return (T) JSONObject.toBean(JSONObject.fromObject(jsonString), t.getClass());// .fromObject(bean,
	//																							// jsonConfig).toString();
	//		} catch (Exception e) {
	//			log.error(e.toString(), e);
	//			throw new RuntimeException(e.toString(), e);
	//		}
	//	}
}
