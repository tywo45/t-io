package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;

/**
 *
 * @author tanyaowu
 * 2017年8月4日 上午9:41:12
 */
public class HttpResponseEncoder {
	public static enum Step {
		firstline, header, body
	}

	private static Logger log = LoggerFactory.getLogger(HttpResponseEncoder.class);

	public static final int MAX_HEADER_LENGTH = 20480;

	private static final byte[] rnBytes = "\r\n".getBytes();
	private static final byte colonByte = ":".getBytes()[0];
	private static final byte spaceByte = " ".getBytes()[0];
	private static final byte[] http1_1Bytes = "HTTP/1.1".getBytes();

	private static final Map<String, byte[]> bytesMap = new HashMap<>(16);

	private static byte[] getBytes(String str) {
		byte[] ret = bytesMap.get(str);
		if (ret == null) {
			try {
				ret = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
			bytesMap.put(str, ret);
		}
		return ret;
	}

	private static int add(byte[][] list, byte[] bs, int index) {
		list[index] = bs;
		return bs.length;
	}

	/**
	 *
	 * @param httpResponse
	 * @param groupContext
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public static ByteBuffer encode(HttpResponse httpResponse, GroupContext groupContext, ChannelContext channelContext) {
		//		byte[] encodedBytes = httpResponse.getEncodedBytes();
		//		if (encodedBytes != null) {
		//			ByteBuffer ret = ByteBuffer.wrap(encodedBytes);
		//			ret.position(ret.limit());
		//			return ret;
		//		}

		//
		int bodyLength = 0;
		byte[] body = httpResponse.getBody();
		if (body != null) {
			bodyLength = body.length;
		}

		HttpResponseStatus httpResponseStatus = httpResponse.getStatus();
		byte[] respLineStatusBytes = getBytes(httpResponseStatus.getStatus() + " ");
		byte[] respLineDescriptionBytes = getBytes(httpResponseStatus.getDescription());
		int respLineLength = http1_1Bytes.length + respLineStatusBytes.length + respLineDescriptionBytes.length + 4; //两个空格+\r\n

		//		StringBuilder sb = new StringBuilder(512);
		int headerLength = 0;
		Map<String, String> headers = httpResponse.getHeaders();
		headers.put(HttpConst.ResponseHeaderKey.Content_Length, String.valueOf(bodyLength));
		Set<Entry<String, String>> headerSet = headers.entrySet();
		for (Entry<String, String> entry : headerSet) {
			headerLength += entry.getKey().length();
			headerLength += (entry.getValue().length() * 3);
		}
		headerLength += (headers.size() * 3); //冒号和\r\n

		if (httpResponse.getCookies() != null) {
			for (Cookie cookie : httpResponse.getCookies()) {
				headerLength += HttpConst.ResponseHeaderKey.Set_Cookie.length();
				headerLength += (cookie.toString().length() * 3);
			}
			headerLength += httpResponse.getCookies().size() * 3; //冒号和\r\n
		}
		
		headerLength += 2;  //最后的\r\n

		ByteBuffer buffer = ByteBuffer.allocate(respLineLength + headerLength + bodyLength);
		buffer.put(http1_1Bytes);
		buffer.put(spaceByte);
		buffer.put(respLineStatusBytes);
		buffer.put(spaceByte);
		buffer.put(respLineDescriptionBytes);
		buffer.put(rnBytes);
		try {
			for (Entry<String, String> entry : headerSet) {
				buffer.put(getBytes(entry.getKey()));
				buffer.put(colonByte);
				buffer.put(entry.getValue().getBytes(httpResponse.getCharset()));
				buffer.put(rnBytes);
			}

			//处理cookie
			if (httpResponse.getCookies() != null) {
				for (Cookie cookie : httpResponse.getCookies()) {
					buffer.put(getBytes(HttpConst.ResponseHeaderKey.Set_Cookie));
					buffer.put(colonByte);
					buffer.put(cookie.toString().getBytes(httpResponse.getCharset()));
					buffer.put(rnBytes);
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
		
		buffer.put(rnBytes);
		
		if (bodyLength > 0) {
			buffer.put(body);
		}
		buffer.flip();
		return buffer;
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 */
	public HttpResponseEncoder() {

	}
}
