package org.tio.http.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tanyaowu 
 * 2018年7月1日 上午9:51:04
 */
public class HeaderName {
	private static final Map<String, HeaderName> map = new HashMap<>();

	public static final HeaderName SET_COOKIE = new HeaderName(HttpConst.ResponseHeaderKey.Set_Cookie);

	public static final HeaderName CONTENT_TYPE = new HeaderName(HttpConst.ResponseHeaderKey.Content_Type);

	public static final HeaderName CACHE_CONTROL = new HeaderName(HttpConst.ResponseHeaderKey.Cache_Control);

	public static final HeaderName LOCATION = new HeaderName(HttpConst.ResponseHeaderKey.Location);

	public static final HeaderName Connection = new HeaderName(HttpConst.ResponseHeaderKey.Connection);

	public static final HeaderName Keep_Alive = new HeaderName(HttpConst.ResponseHeaderKey.Keep_Alive);

	public static final HeaderName Content_Length = new HeaderName(HttpConst.ResponseHeaderKey.Content_Length);

	public static final HeaderName Access_Control_Allow_Origin = new HeaderName(HttpConst.ResponseHeaderKey.Access_Control_Allow_Origin);

	public static final HeaderName Access_Control_Allow_Headers = new HeaderName(HttpConst.ResponseHeaderKey.Access_Control_Allow_Headers);

	public static final HeaderName Access_Control_Allow_Methods = new HeaderName(HttpConst.ResponseHeaderKey.Access_Control_Allow_Methods);

	public static final HeaderName Access_Control_Max_Age = new HeaderName(HttpConst.ResponseHeaderKey.Access_Control_Max_Age);
	//	httpResponse.addHeader("Access-Control-Allow-Origin", "*");
	//    httpResponse.addHeader("Access-Control-Allow-Methods", "*");Access_Control_Allow_Headers
	//    httpResponse.addHeader("Access-Control-Allow-Headers", "Content-Type");
	//    httpResponse.addHeader("Access-Control-Max-Age", "1800");

	public static final HeaderName Content_Disposition = new HeaderName(HttpConst.ResponseHeaderKey.Content_Disposition);

	public static final HeaderName Content_Encoding = new HeaderName(HttpConst.ResponseHeaderKey.Content_Encoding);

	public static final HeaderName Date = new HeaderName(HttpConst.ResponseHeaderKey.Date);

	public static final HeaderName Expires = new HeaderName(HttpConst.ResponseHeaderKey.Expires);

	public static final HeaderName Last_Modified = new HeaderName(HttpConst.ResponseHeaderKey.Last_Modified);

	public static final HeaderName Refresh = new HeaderName(HttpConst.ResponseHeaderKey.Refresh);

	public static final HeaderName Sec_WebSocket_Accept = new HeaderName(HttpConst.ResponseHeaderKey.Sec_WebSocket_Accept);

	public static final HeaderName Server = new HeaderName(HttpConst.ResponseHeaderKey.Server);

	public static final HeaderName Upgrade = new HeaderName(HttpConst.ResponseHeaderKey.Upgrade);

	public static final HeaderName Content_Type = new HeaderName(HttpConst.ResponseHeaderKey.Content_Type);

	public static final HeaderName Location = new HeaderName(HttpConst.ResponseHeaderKey.Location);

	public static final HeaderName Cache_Control = new HeaderName(HttpConst.ResponseHeaderKey.Cache_Control);

	public static final HeaderName tio_from_cache = new HeaderName(HttpConst.ResponseHeaderKey.tio_from_cache);

	public static final HeaderName tio_webpack_used_cache = new HeaderName(HttpConst.ResponseHeaderKey.tio_webpack_used_cache);

	public final String name;

	public final byte[] bytes;

	private HeaderName(String name) {
		this.name = name;
		this.bytes = name.getBytes();
		map.put(name, this);
	}

	public static HeaderName from(String name) {
		HeaderName ret = map.get(name);
		if (ret == null) {
			synchronized (map) {
				ret = map.get(name);
				if (ret == null) {
					ret = new HeaderName(name);
				}
			}
		}
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		HeaderName other = (HeaderName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return name;
	}
}
