package org.tio.http.common;

/**
 * @author tanyaowu
 * 2017年6月28日 下午2:23:16
 */
public enum Method {
	GET("GET"), POST("POST"), HEAD("HEAD"), PUT("PUT"), TRACE("TRACE"), OPTIONS("OPTIONS"), PATCH("PATCH");
	public static Method from(String method) {
		if (method == null) {
			return null;
		}
		switch (method) {
		case "GET":
			return GET;
		case "POST":
			return POST;
		case "HEAD":
			return HEAD;
		case "PUT":
			return PUT;
		case "TRACE":
			return TRACE;
		case "OPTIONS":
			return OPTIONS;
		case "PATCH":
			return PATCH;
		default:
			return null;
		}
	}

	String value;

	private Method(String value) {
		this.value = value;
	}
}
