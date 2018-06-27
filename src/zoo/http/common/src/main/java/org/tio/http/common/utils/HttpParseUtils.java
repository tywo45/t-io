package org.tio.http.common.utils;

/**
 * @author tanyaowu
 * 2017年7月27日 上午10:09:19
 */
public class HttpParseUtils {

	/**
	 * obtain sub attribute
	 * @param str 形如:"multipart/form-data; boundary=ujjLiiJBznFt70fG1F4EUCkIupn7H4tzm", "application/x-www-form-urlencoded; charset=UTF-8", "form-data; name="before""
	 * @param name 形如："boundary", "charset", "name"
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
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {
		String str = "multipart/form-data; boundary=ujjLiiJBznFt70fG1F4EUCkIupn7H4tzm";
		String name = "boundary";
		String attr = getSubAttribute(str, name);
		System.out.println(attr);

		str = "multipart/form-data;";
		name = "boundary";
		attr = getSubAttribute(str, name);
		System.out.println(attr);

		str = "application/x-www-form-urlencoded; charset=UTF-8; name=tanyaowu";
		name = "charset";
		attr = getSubAttribute(str, name);
		System.out.println(attr);

		str = "application/x-www-form-urlencoded; charset=UTF-8;";
		name = "charset";
		attr = getSubAttribute(str, name);
		System.out.println(attr);

		str = "application/x-www-form-urlencoded; charset=UTF-8";
		name = "charset";
		attr = getSubAttribute(str, name);
		System.out.println(attr);

		str = "application/x-www-form-urlencoded; charset=UTF-8; name=tanyaowu";
		name = "name";
		attr = getSubAttribute(str, name);
		System.out.println(attr);
		
		str = "form-data; name=\"before\"";
		name = "name";
		attr = getSubAttribute(str, name);
		System.out.println(attr);
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private HttpParseUtils() {
	}
}
