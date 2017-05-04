package org.tio.json;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月16日 上午11:36:53
 */
public abstract class Json {
	private static SerializeConfig mapping = new SerializeConfig();

	static {
		mapping.put(Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Date.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Timestamp.class, new SimpleDateFormatSerializer("yyyy-MM-dd HH:mm:ss"));
		mapping.put(java.sql.Time.class, new SimpleDateFormatSerializer("HH:mm:ss"));
	}

	public static <T> T toBean(String jsonString, Class<T> tt) {
		try {
			if (StringUtils.isBlank(jsonString)) {
				return null;
			}

			T t = JSON.parseObject(jsonString, tt);
			return t;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String toJson(Object bean) {
		try {
			return JSON.toJSONString(bean, mapping, SerializerFeature.DisableCircularReferenceDetect);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
}
