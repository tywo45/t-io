package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu 
 * 2018年7月1日 上午9:51:04
 */
public class HeaderValue {

	public static class EnumerableValue {
		private static final Map<String, HeaderValue> map = new HashMap<>();

		public static HeaderValue from(String value) {
			if (StrUtil.isBlank(value)) {
				return null;
			}
			HeaderValue ret = map.get(value);
			if (ret == null) {
				synchronized (map) {
					ret = map.get(value);
					if (ret == null) {
						ret = HeaderValue.from(value);
						map.put(value, ret);
					}
				}
			}
			return ret;
		}

	}

	public static class Connection extends EnumerableValue {
		public static final HeaderValue	keep_alive	= HeaderValue.from(HttpConst.ResponseHeaderValue.Connection.keep_alive);
		public static final HeaderValue	close		= HeaderValue.from(HttpConst.ResponseHeaderValue.Connection.close);
		public static final HeaderValue	Upgrade		= HeaderValue.from(HttpConst.ResponseHeaderValue.Connection.Upgrade);
	}

	public static class Upgrade extends EnumerableValue {
		public static final HeaderValue WebSocket = HeaderValue.from(HttpConst.ResponseHeaderValue.Upgrade.WebSocket);
	}

	public static class Keep_Alive extends EnumerableValue {
		public static final HeaderValue TIMEOUT_10_MAX_20 = HeaderValue.from("timeout=10, max=20");
	}

	public static class Cache_Control {
		public static final HeaderValue MAX_AGE_60 = HeaderValue.from("max-age:60");
	}

	public static class Server {
		public static final HeaderValue TIO = HeaderValue.from(org.tio.http.common.HttpConst.SERVER_INFO);
	}

	public static class Content_Encoding extends EnumerableValue {
		public static final HeaderValue gzip = HeaderValue.from("gzip");
	}

	public static class Tio_From_Cache extends EnumerableValue {
		public static final HeaderValue	TRUE	= HeaderValue.from("true");
		public static final HeaderValue	FALSE	= HeaderValue.from("false");
	}

	public static class Tio_Webpack_Used_Cache extends EnumerableValue {
		public static final HeaderValue V_1 = HeaderValue.from("1");
	}

	public static class Content_Type extends EnumerableValue {
		public static final HeaderValue TEXT_PLAIN_TXT = HeaderValue.Content_Type.from(MimeType.TEXT_PLAIN_TXT.getType());

		public static final HeaderValue TEXT_PLAIN_JSON = HeaderValue.Content_Type.from(MimeType.TEXT_PLAIN_JSON.getType());

		public static final HeaderValue TEXT_HTML_HTML = HeaderValue.Content_Type.from(MimeType.TEXT_HTML_HTML.getType());

		public static final HeaderValue APPLICATION_ACAD_DWG = HeaderValue.Content_Type.from(MimeType.APPLICATION_ACAD_DWG.getType());

		public static final HeaderValue DEFAULT_TYPE = HeaderValue.Content_Type.from("application/octet-stream");

	}

	public static class Access_Control_Allow_Origin extends EnumerableValue {

	}

	public static class Access_Control_Allow_Headers extends EnumerableValue {

	}

	public static class Access_Control_Allow_Methods extends EnumerableValue {

	}

	public static class Access_Control_Max_Age extends EnumerableValue {

	}

	public final String value;

	public final byte[] bytes;

	private HeaderValue(String name) {
		this.value = name;
		this.bytes = name.getBytes();
	}

	private HeaderValue(String name, String charset) throws UnsupportedEncodingException {
		this.value = name;
		this.bytes = name.getBytes(charset);
	}

	public static HeaderValue from(String name) {
		return new HeaderValue(name);
	}

	public static HeaderValue from(String name, String charset) throws UnsupportedEncodingException {
		return new HeaderValue(name, charset);
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HeaderValue other = (HeaderValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
