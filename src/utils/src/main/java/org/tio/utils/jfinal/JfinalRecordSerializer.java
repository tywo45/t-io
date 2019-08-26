package org.tio.utils.jfinal;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.jfinal.plugin.activerecord.Record;

/**
 * @author tanyaowu
 * 2017年8月20日 上午8:49:05
 */
public class JfinalRecordSerializer implements ObjectSerializer {
	public static final JfinalRecordSerializer me = new JfinalRecordSerializer();

	/**
	 *
	 * @author tanyaowu
	 */
	public JfinalRecordSerializer() {
	}

	/**
	 * @param serializer
	 * @param object
	 * @param fieldName
	 * @param fieldType
	 * @param features
	 * @throws IOException
	 * @author tanyaowu
	 */
	@Override
	public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
		if (object == null) {
			serializer.out.writeNull();
			return;
		}

		Record record = (Record) object;

		Map<String, Object> map = record.getColumns();
		serializer.write(map);
	}
}
