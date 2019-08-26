package org.tio.http.common;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 部分代码参考了: https://github.com/helyho/Voovan
 * @author tanyaowu
 * 2017年5月29日 上午7:45:58
 */
public class Cookie {
	private static Logger log = LoggerFactory.getLogger(Cookie.class);

	/**
	 * 通过 Map 构建一个 Cookie 对象
	 * @param cookieMap Cookie 属性 Map
	 * @return Cookie 对象
	 */
	public static Cookie buildCookie(Map<String, String> cookieMap, HttpConfig httpConfig) {
		Cookie cookie = new Cookie();
		for (Entry<String, String> cookieMapItem : cookieMap.entrySet()) {
			switch (cookieMapItem.getKey().toLowerCase()) {
			case "domain":
				cookie.setDomain(cookieMapItem.getValue());
				break;
			case "path":
				cookie.setPath(cookieMapItem.getValue());
				break;
			case "max-age":
				cookie.setMaxAge(Long.parseLong(cookieMapItem.getValue()));
				break;
			case "secure":
				cookie.setSecure(true);
				break;
			case "httponly":
				cookie.setHttpOnly(true);
				break;
			case "expires":
				cookie.setExpires(cookieMapItem.getValue());
				break;
			default:
				cookie.setName(cookieMapItem.getKey());
				try {
					cookie.setValue(URLDecoder.decode(cookieMapItem.getValue(), httpConfig.getCharset()));
				} catch (Exception e) {
					log.error("cookie值解码时异常：" + cookieMapItem.getValue(), e);
				}
				break;
			}
		}
		return cookie;
	}

	public static Map<String, String> getEqualMap(String cookieline) {
		Map<String, String> equalMap = new HashMap<>();
		String[] searchedStrings = searchByRegex(cookieline, "([^ ;,]+=[^ ;,]+)");
		for (String groupString : searchedStrings) {
			//这里不用 split 的原因是有可能等号后的值字符串中出现等号
			String[] equalStrings = new String[2];
			int equalCharIndex = groupString.indexOf("=");
			equalStrings[0] = groupString.substring(0, equalCharIndex);
			equalStrings[1] = groupString.substring(equalCharIndex + 1, groupString.length());
			if (equalStrings.length == 2) {
				String key = equalStrings[0];
				String value = equalStrings[1];
				if (value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length() - 1);
				}
				equalMap.put(key, value);
			}
		}
		return equalMap;
	}

	public static String[] searchByRegex(String source, String regex) {
		if (source == null) {
			return null;
		}

		Map<Integer, Pattern> regexPattern = new HashMap<>();

		Pattern pattern = null;
		if (regexPattern.containsKey(regex.hashCode())) {
			pattern = regexPattern.get(regex.hashCode());
		} else {
			pattern = Pattern.compile(regex);
			regexPattern.put(regex.hashCode(), pattern);
		}
		Matcher matcher = pattern.matcher(source);
		ArrayList<String> result = new ArrayList<>();
		while (matcher.find()) {
			result.add(matcher.group());
		}
		return result.toArray(new String[0]);
	}

	private String	domain	= null;
	private String	path	= null;
	private Long	maxAge	= null;

	private String	expires	= null;
	private boolean	secure	= false;

	private boolean httpOnly = false;

	private String name;

	private String value;

	private byte[] bytes;

	/**
	 * 创建一个 Cookie
	 * @param domain	cookie的受控域
	 * @param value		名称
	 * @param value		值
	 * @param maxAge	失效时间,单位秒
	 * @return Cookie 对象
	 */
	public Cookie(String domain, String name, String value, Long maxAge) {
		setName(name);
		setValue(value);
		setPath("/");
		setDomain(domain);
		setMaxAge(maxAge);
		setHttpOnly(false);
	}

	/**
	 * 
	 * @author tanyaowu
	 */
	private Cookie() {
	}

	public String getDomain() {
		return domain;
	}

	public String getExpires() {
		return expires;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getValue() {
		return value;
	}

	public boolean isHttpOnly() {
		return httpOnly;
	}

	public boolean isSecure() {
		return secure;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public void setHttpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setSecure(boolean secure) {
		this.secure = secure;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return (this.name != null || this.value != null ? this.name + "=" + this.value : "") + (this.domain != null ? "; Domain=" + this.domain : "")
		        + (this.maxAge != null ? "; Max-Age=" + this.maxAge : "") + (this.path != null ? "; Path=" + this.path : " ") + (this.httpOnly ? "; httponly; " : "")
		        + (this.secure ? "; Secure" : "");
	}

	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @param bytes the bytes to set
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
