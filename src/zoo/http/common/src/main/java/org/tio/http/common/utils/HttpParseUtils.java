package org.tio.http.common.utils;

/**
 * @author tanyaowu
 * 2017年7月27日 上午10:09:19
 */
public class HttpParseUtils {

	/**
	 * obtain sub attribute
	 * @param str 形如:"multipart/form-data; boundary=ujjLiiJBznFt70fG1F4EUCkIupn7H4tzm", "application/x-www-form-urlencoded; charset=UTF-8", "form-data; value="before""
	 * @param value 形如："boundary", "charset", "value"
	 * @return 形如："ujjLiiJBznFt70fG1F4EUCkIupn7H4tzm", "UTF-8", "before"
	 * @author tanyaowu
	 */
	public static String getSubAttribute(String str, String name) {
		int indexOfName = str.indexOf(name + "=");
		if (indexOfName == -1) {
			return null;
		}

		int valueStartIndex = indexOfName + 1 + name.length();
		char[] cs = new char[str.length() - valueStartIndex];
		int i = 0;
		for (; i < cs.length; i++) {
			char c = str.charAt(i + valueStartIndex);
			if (c == ';') {
				break;
			}
			cs[i] = c;
		}

		if (cs.length > 1 && (cs[0] == '"' && cs[i - 1] == '"')) {
			return String.copyValueOf(cs, 1, i - 2);
		} else {
			if (i == cs.length) {
				return new String(cs);
			} else {
				return String.copyValueOf(cs, 0, i);
			}
		}
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private HttpParseUtils() {
	}
}
