package org.tio.http.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.hutool.ZipUtil;

/**
 * @author tanyaowu
 * 2017年8月18日 下午5:47:00
 */
public class HttpGzipUtils {
	private static Logger log = LoggerFactory.getLogger(HttpGzipUtils.class);

	/**
	 * 
	 * @param request
	 * @param response
	 * @author tanyaowu
	 */
	public static void gzip(HttpRequest request, HttpResponse response) {
		if (response == null) {
			return;
		}

		//
		//		// 已经gzip过了，就不必再压缩了
		//		if (response.isHasGzipped()) {
		//			return;
		//		}

		if (request != null && request.getIsSupportGzip()) {
			gzip(response);
		} else {
			if (request != null) {
				log.warn("{}, 不支持gzip, {}", request.getClientIp(), request.getHeader(HttpConst.RequestHeaderKey.User_Agent));
			} else {
				log.info("request对象为空");
			}

		}
	}

	/**
	 * 
	 * @param response
	 * @author tanyaowu
	 */
	public static void gzip(HttpResponse response) {
		if (response == null) {
			return;
		}

		// 已经gzip过了，就不必再压缩了
		if (response.isHasGzipped()) {
			return;
		}

		byte[] bs = response.getBody();
		if (bs != null && bs.length >= 300) {
			byte[] bs2 = ZipUtil.gzip(bs);
			if (bs2.length < bs.length) {
				response.setBody(bs2);
				response.setHasGzipped(true);
				response.addHeader(HeaderName.Content_Encoding, HeaderValue.Content_Encoding.gzip);
			}
		}
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private HttpGzipUtils() {
	}
}
