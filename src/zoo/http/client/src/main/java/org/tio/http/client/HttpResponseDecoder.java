package org.tio.http.client;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.http.common.HttpConst;
import org.tio.http.common.ResponseLine;
import org.tio.utils.SysConst;
import org.tio.utils.hutool.StrUtil;

/**
 * http client中使用
 * 暂时不支持文件下载和chunk，暂时是用来做http性能测试用的
 * @author tanyaowu
 *
 */
public class HttpResponseDecoder {
	public static enum Step {
		firstline, header, body
	}

	private static Logger log = LoggerFactory.getLogger(HttpResponseDecoder.class);

	/**
	 *   头部，最多有多少字节
	 */
	public static final int MAX_LENGTH_OF_HEADER = 20480;

	/**
	 *      头部，每行最大的字节数
	 */
	public static final int MAX_LENGTH_OF_HEADERLINE = 2048;

	/**
	 *   请求行的最大长度
	 */
	public static final int MAX_LENGTH_OF_RESPONSELINE = 2048;

	/**
	 * 
	 * @param buffer
	 * @param limit
	 * @param position
	 * @param readableLength
	 * @param channelContext
	 * @return
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	public static ClientHttpResponse decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		//		int initPosition = position;
		//		int receivedCount = 0;
		//		Step step = Step.firstline;
		//		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] bodyBytes = null;
		StringBuilder headerSb = null;//new StringBuilder(512);
		ResponseLine firstLine = null;
		boolean appendRequestHeaderString = true;

		if (appendRequestHeaderString) {
			headerSb = new StringBuilder(512);
		}

		// request line start
		firstLine = parseResponseLine(buffer, channelContext);
		if (firstLine == null) {
			return null;
		}
		// request line end

		// request header start
		boolean headerCompleted = parseHeaderLine(buffer, headers, 0);
		if (!headerCompleted) {
			return null;
		}
		String contentLengthStr = headers.get(HttpConst.ResponseHeaderKey.Content_Length);

		if (StrUtil.isBlank(contentLengthStr)) {
			contentLength = 0;
		} else {
			contentLength = Integer.parseInt(contentLengthStr);
		}

		int headerLength = (buffer.position() - position);
		int allNeedLength = headerLength + contentLength; //这个packet所需要的字节长度(含头部和体部)

		if (readableLength < allNeedLength) {
			channelContext.setPacketNeededLength(allNeedLength);
			return null;
		}
		// request header end

		// ----------------------------------------------- request body start

		ClientHttpResponse httpResponse = new ClientHttpResponse();
		httpResponse.setChannelContext(channelContext);
		//		httpResponse.setHttpConfig((HttpConfig) channelContext.tioConfig.getAttribute(TioConfigKey.HTTP_SERVER_CONFIG));

		if (appendRequestHeaderString) {
			httpResponse.setHeaderString(headerSb.toString());
		} else {
			httpResponse.setHeaderString("");
		}

		httpResponse.setResponseLine(firstLine);
		httpResponse.setHeaders(headers);
		httpResponse.setContentLength(contentLength);
		String connection = headers.get(HttpConst.ResponseHeaderKey.Connection);
		if (connection != null) {
			httpResponse.setConnection(connection.toLowerCase());
		}

		if (contentLength == 0) {
			//			if (StrUtil.isNotBlank(firstLine.getQuery())) {
			//				decodeParams(httpResponse.getParams(), firstLine.getQuery(), httpResponse.getCharset(), channelContext);
			//			}
		} else {
			bodyBytes = new byte[contentLength];
			buffer.get(bodyBytes);
			httpResponse.setBody(bodyBytes);
			//解析消息体
			parseBody(httpResponse, bodyBytes, channelContext);
		}
		// ----------------------------------------------- request body end

		return httpResponse;

	}

	/**
	 * 
	 * @param params
	 * @param queryString
	 * @param charset
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void decodeParams(Map<String, Object[]> params, String queryString, String charset, ChannelContext channelContext) {
		if (StrUtil.isBlank(queryString)) {
			return;
		}

		String[] keyvalues = queryString.split("&");
		for (String keyvalue : keyvalues) {
			String[] keyvalueArr = keyvalue.split("=");
			if (keyvalueArr.length != 2) {
				continue;
			}

			String key = keyvalueArr[0];
			String value = null;
			try {
				value = URLDecoder.decode(keyvalueArr[1], charset);
			} catch (UnsupportedEncodingException e) {
				log.error(channelContext.toString(), e);
			}

			Object[] existValue = params.get(key);
			if (existValue != null) {
				String[] newExistValue = new String[existValue.length + 1];
				System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
				newExistValue[newExistValue.length - 1] = value;
				params.put(key, newExistValue);
			} else {
				String[] newExistValue = new String[] { value };
				params.put(key, newExistValue);
			}
		}
		return;
	}

	/**
	 * 先粗暴地简单解析一下
	 * @param httpResponse
	 * @param bodyBytes
	 * @param channelContext
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	private static void parseBody(ClientHttpResponse httpResponse, byte[] bodyBytes, ChannelContext channelContext) throws AioDecodeException {
		if (bodyBytes != null) {
			try {
				httpResponse.setBodyString(new String(bodyBytes, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @param headers
	 * @return 头部是否解析完成，true: 解析完成, false: 没有解析完成
	 * @author tanyaowu
	 */
	public static boolean parseHeaderLine(ByteBuffer buffer, Map<String, String> headers, int headerLength) throws AioDecodeException {
		//		if (!buffer.hasArray()) {
		//			return parseHeaderLine2(buffer, headers, headerLength, httpConfig);
		//		}

		byte[] allbs = buffer.array();
		int initPosition = buffer.position();
		int lastPosition = initPosition;
		int remaining = buffer.remaining();
		if (remaining == 0) {
			return false;
		} else if (remaining > 1) {
			byte b1 = buffer.get();
			byte b2 = buffer.get();
			if (SysConst.CR == b1 && SysConst.LF == b2) {
				return true;
			} else if (SysConst.LF == b1) {
				return true;
			}
		} else {
			if (SysConst.LF == buffer.get()) {
				return true;
			}
		}

		String name = null;
		String value = null;
		boolean hasValue = false;

		boolean needIteration = false;
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (name == null) {
				if (b == SysConst.COL) {
					int len = buffer.position() - lastPosition - 1;
					name = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				} else if (b == SysConst.LF) {
					byte lastByte = buffer.get(buffer.position() - 2);
					int len = buffer.position() - lastPosition - 1;
					if (lastByte == SysConst.CR) {
						len = buffer.position() - lastPosition - 2;
					}
					name = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
					headers.put(name.toLowerCase(), "");

					needIteration = true;
					break;
				}
				continue;
			} else if (value == null) {
				if (b == SysConst.LF) {
					byte lastByte = buffer.get(buffer.position() - 2);
					int len = buffer.position() - lastPosition - 1;
					if (lastByte == SysConst.CR) {
						len = buffer.position() - lastPosition - 2;
					}
					value = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();

					headers.put(name.toLowerCase(), StrUtil.trimEnd(value));
					needIteration = true;
					break;
				} else {
					if (!hasValue && b == SysConst.SPACE) {
						lastPosition = buffer.position();
					} else {
						hasValue = true;
					}
				}
			}
		}

		if (needIteration) {
			int myHeaderLength = buffer.position() - initPosition;
			if (myHeaderLength > MAX_LENGTH_OF_HEADER) {
				throw new AioDecodeException("header is too long");
			}
			return parseHeaderLine(buffer, headers, myHeaderLength + headerLength);
		}

		if (remaining > MAX_LENGTH_OF_HEADERLINE) {
			throw new AioDecodeException("header line is too long");
		}
		return false;
	}

	/**
	 * parse response line(the first line)
	 * @param line HTTP/1.1 200 OK
	 * @param channelContext
	 * @return
	 *
	 * @author tanyaowu
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static ResponseLine parseResponseLine(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		//		if(!buffer.hasArray()) {
		//			return parseRequestLine2(buffer, channelContext, httpConfig);
		//		}

		byte[] allbs = buffer.array();

		int initPosition = buffer.position();

		//		int remaining = buffer.remaining();

		String protocol = null;
		String version = null;
		Integer status = null;
		String desc = null;
		int lastPosition = initPosition;//buffer.position();
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (protocol == null) {
				if (b == '/') {
					int len = buffer.position() - lastPosition - 1;
					protocol = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				}
				continue;
			} else if (version == null) {
				if (b == SysConst.SPACE) {
					int len = buffer.position() - lastPosition - 1;
					version = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				}
				continue;
			} else if (status == null) {
				if (b == SysConst.SPACE) {
					int len = buffer.position() - lastPosition - 1;
					String statusStr = new String(allbs, lastPosition, len);
					try {
						status = Integer.parseInt(statusStr);
					} catch (NumberFormatException e) {
						throw new AioDecodeException(e);
					}
					lastPosition = buffer.position();
				}
				continue;
			} else if (desc == null) {
				if (b == SysConst.LF) {
					byte lastByte = buffer.get(buffer.position() - 2);
					int len = buffer.position() - lastPosition - 1;
					if (lastByte == SysConst.CR) {
						len = buffer.position() - lastPosition - 2;
					}
					desc = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();

					ResponseLine responseLine = new ResponseLine(protocol, version, status, desc);
					return responseLine;
				}
				continue;
			}
		}

		if ((buffer.position() - initPosition) > MAX_LENGTH_OF_RESPONSELINE) {
			throw new AioDecodeException("response line is too long");
		}
		return null;
	}
}
