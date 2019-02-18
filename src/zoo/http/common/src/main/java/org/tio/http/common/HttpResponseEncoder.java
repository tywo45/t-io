package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.http.common.utils.HttpDateTimer;
import org.tio.utils.SysConst;

/**
 * http server中使用
 * @author tanyaowu
 * 2017年8月4日 上午9:41:12
 */
public class HttpResponseEncoder {

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpResponseEncoder.class);

	public static final int MAX_HEADER_LENGTH = 20480;

	public static final int HEADER_SERVER_LENGTH = HeaderName.Server.bytes.length + HeaderValue.Server.TIO.bytes.length + 3;

	public static final int HEADER_DATE_LENGTH_1 = HeaderName.Date.bytes.length + 3;

	public static final int HEADER_FIXED_LENGTH = HEADER_SERVER_LENGTH + HEADER_DATE_LENGTH_1;

	/**
	 *
	 * @param httpResponse
	 * @param groupContext
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static ByteBuffer encode(HttpResponse httpResponse, GroupContext groupContext, ChannelContext channelContext) throws UnsupportedEncodingException {
		int bodyLength = 0;
		byte[] body = httpResponse.getBody();
		if (body != null) {
			bodyLength = body.length;
		}

		HttpResponseStatus httpResponseStatus = httpResponse.getStatus();

		//		byte[] respLineStatusBytes = getBytes(Integer.toString(httpResponseStatus.getStatus()));
		//		byte[] respLineDescriptionBytes = getBytes(httpResponseStatus.getDescription());
		int respLineLength = httpResponseStatus.responseLineBinary.length;//http1_1Bytes.length + httpResponseStatus.getHeaderBinary().length + 3; //一个空格+\r\n

		//		StringBuilder sb = new StringBuilder(512);

		Map<HeaderName, HeaderValue> headers = httpResponse.getHeaders();
		httpResponse.addHeader(HeaderName.Content_Length, HeaderValue.from(Integer.toString(bodyLength)));
		int headerLength = httpResponse.getHeaderByteCount();

		//		for (Entry<String, String> entry : headerSet) {
		//			headerLength += entry.getKey().length();
		//			headerLength += (entry.getValue().length() * 3);
		//		}
		//		headerLength += (headers.size() * 3); //冒号和\r\n

		if (httpResponse.getCookies() != null) {
			for (Cookie cookie : httpResponse.getCookies()) {
				headerLength += HeaderName.SET_COOKIE.bytes.length;
				byte[] bs = cookie.toString().getBytes(httpResponse.getCharset());
				cookie.setBytes(bs);
				headerLength += (bs.length);
			}
			headerLength += httpResponse.getCookies().size() * 3; //冒号和\r\n
		}

		HeaderValue httpDateValue = HttpDateTimer.httpDateValue;

		headerLength += HEADER_FIXED_LENGTH + httpDateValue.bytes.length;

		ByteBuffer buffer = ByteBuffer.allocate(respLineLength + headerLength + bodyLength);
		buffer.put(httpResponseStatus.responseLineBinary);

		buffer.put(HeaderName.Server.bytes);
		buffer.put(SysConst.COL);
		buffer.put(HeaderValue.Server.TIO.bytes);
		buffer.put(SysConst.CR_LF);

		buffer.put(HeaderName.Date.bytes);
		buffer.put(SysConst.COL);
		buffer.put(httpDateValue.bytes);
		buffer.put(SysConst.CR_LF);

		Set<Entry<HeaderName, HeaderValue>> headerSet = headers.entrySet();
		for (Entry<HeaderName, HeaderValue> entry : headerSet) {
			buffer.put(entry.getKey().bytes);
			buffer.put(SysConst.COL);
			buffer.put(entry.getValue().bytes);
			buffer.put(SysConst.CR_LF);
		}

		//处理cookie
		if (httpResponse.getCookies() != null) {
			for (Cookie cookie : httpResponse.getCookies()) {
				buffer.put(HeaderName.SET_COOKIE.bytes);
				buffer.put(SysConst.COL);
				buffer.put(cookie.getBytes());
				buffer.put(SysConst.CR_LF);
			}
		}

		buffer.put(SysConst.CR_LF);

		if (bodyLength > 0) {
			buffer.put(body);
		}
		buffer.flip();
		return buffer;
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 */
	private HttpResponseEncoder() {

	}
}
