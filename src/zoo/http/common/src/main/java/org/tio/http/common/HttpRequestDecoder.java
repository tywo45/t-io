package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.http.common.HttpConst.RequestBodyFormat;
import org.tio.http.common.utils.HttpParseUtils;
import org.tio.utils.SysConst;
import org.tio.utils.hutool.StrUtil;

/**
 * http server中使用
 * @author tanyaowu
 *
 */
public class HttpRequestDecoder {
	public static enum Step {
		firstline, header, body
	}

	private static Logger log = LoggerFactory.getLogger(HttpRequestDecoder.class);

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
	public static final int MAX_LENGTH_OF_REQUESTLINE = 2048;

	/**
	 * 
	 * @param buffer
	 * @param limit
	 * @param position
	 * @param readableLength
	 * @param channelContext
	 * @param httpConfig 可能为null
	 * @return
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	public static HttpRequest decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext, HttpConfig httpConfig)
	        throws AioDecodeException {
		//		int initPosition = position;
		//		int count = 0;
		//		Step step = Step.firstline;
		//		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] bodyBytes = null;
		StringBuilder headerSb = null;//new StringBuilder(512);
		RequestLine firstLine = null;
		boolean appendRequestHeaderString = httpConfig.isAppendRequestHeaderString();
		;
		//		if (httpConfig != null) {
		//			
		//		}
		if (appendRequestHeaderString) {
			headerSb = new StringBuilder(512);
		}

		// request line start
		firstLine = parseRequestLine(buffer, channelContext);
		if (firstLine == null) {
			return null;
		}
		// request line end

		HttpRequest httpRequest = new HttpRequest(channelContext.getClientNode());
		httpRequest.setRequestLine(firstLine);
		httpRequest.setChannelContext(channelContext);
		httpRequest.setHttpConfig(httpConfig);

		//		HttpRequestHandler httpRequestHandler = (HttpRequestHandler)channelContext.groupContext.getAttribute(GroupContextKey.HTTP_REQ_HANDLER);
		//		if (httpRequestHandler != null) {
		//			httpRequest.setHttpConfig(httpRequestHandler.getHttpConfig(httpRequest));
		//		}

		// request header start
		boolean headerCompleted = parseHeaderLine(buffer, headers, 0, httpConfig);
		if (!headerCompleted) {
			return null;
		}
		String contentLengthStr = headers.get(HttpConst.RequestHeaderKey.Content_Length);

		if (StrUtil.isBlank(contentLengthStr)) {
			contentLength = 0;
		} else {
			contentLength = Integer.parseInt(contentLengthStr);
			if (contentLength > httpConfig.getMaxLengthOfPostBody()) {
				throw new AioDecodeException("post body length is too big[" + contentLength + "], max length is " + httpConfig.getMaxLengthOfPostBody() + " byte");
			}
		}

		int headerLength = (buffer.position() - position);
		int allNeedLength = headerLength + contentLength; //这个packet所需要的字节长度(含头部和体部)

		if (readableLength < allNeedLength) {
			channelContext.setPacketNeededLength(allNeedLength);
			return null;
		}
		// request header end

		// ----------------------------------------------- request body start
		if (httpConfig.checkHost) {
			if (!headers.containsKey(HttpConst.RequestHeaderKey.Host)) {
				throw new AioDecodeException("there is no host header");
			}
		}

		//		httpRequest.setHttpConfig((HttpConfig) channelContext.groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG));

		if (appendRequestHeaderString) {
			httpRequest.setHeaderString(headerSb.toString());
		} else {
			httpRequest.setHeaderString("");
		}

		httpRequest.setHeaders(headers);
		if (Tio.IpBlacklist.isInBlacklist(channelContext.groupContext, httpRequest.getClientIp())) {
			throw new AioDecodeException("[" + httpRequest.getClientIp() + "] in black list");
		}

		httpRequest.setContentLength(contentLength);

		String connection = headers.get(HttpConst.RequestHeaderKey.Connection);
		if (connection != null) {
			httpRequest.setConnection(connection.toLowerCase());
		}

		if (StrUtil.isNotBlank(firstLine.queryString)) {
			decodeParams(httpRequest.getParams(), firstLine.queryString, httpRequest.getCharset(), channelContext);
		}

		if (contentLength == 0) {
			//			if (StrUtil.isNotBlank(firstLine.getQuery())) {
			//				decodeParams(httpRequest.getParams(), firstLine.getQuery(), httpRequest.getCharset(), channelContext);
			//			}
		} else {
			bodyBytes = new byte[contentLength];
			buffer.get(bodyBytes);
			httpRequest.setBody(bodyBytes);
			//解析消息体
			parseBody(httpRequest, firstLine, bodyBytes, channelContext, httpConfig);
		}
		// ----------------------------------------------- request body end

		//解析User_Agent(浏览器操作系统等信息)
		//		String User_Agent = headers.get(HttpConst.RequestHeaderKey.User_Agent);
		//		if (StrUtil.isNotBlank(User_Agent)) {
		//			//			long start = System.currentTimeMillis();
		//			UserAgentAnalyzer userAgentAnalyzer = UserAgentAnalyzerFactory.getUserAgentAnalyzer();
		//			UserAgent userAgent = userAgentAnalyzer.parse(User_Agent);
		//			httpRequest.setUserAgent(userAgent);
		//		}

		//		StringBuilder logstr = new StringBuilder();
		//		logstr.append("\r\n------------------ websocket header start ------------------------\r\n");
		//		logstr.append(firstLine.getInitStr()).append("\r\n");
		//		Set<Entry<String, String>> entrySet = headers.entrySet();
		//		for (Entry<String, String> entry : entrySet) {
		//			logstr.append(StrUtil.leftPad(entry.getKey(), 30)).append(" : ").append(entry.getValue()).append("\r\n");
		//		}
		//		logstr.append("------------------ websocket header start ------------------------\r\n");
		//		log.error(logstr.toString());

		return httpRequest;

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
	 * 解析消息体
	 * @param httpRequest
	 * @param firstLine
	 * @param bodyBytes
	 * @param channelContext
	 * @param httpConfig
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	private static void parseBody(HttpRequest httpRequest, RequestLine firstLine, byte[] bodyBytes, ChannelContext channelContext, HttpConfig httpConfig)
	        throws AioDecodeException {
		parseBodyFormat(httpRequest, httpRequest.getHeaders());
		RequestBodyFormat bodyFormat = httpRequest.getBodyFormat();

		httpRequest.setBody(bodyBytes);

		//		if (bodyFormat == RequestBodyFormat.MULTIPART) {
		//			if (log.isInfoEnabled()) {
		//				String bodyString = null;
		//				if (bodyBytes != null && bodyBytes.length > 0) {
		//					if (log.isDebugEnabled()) {
		//						try {
		//							bodyString = new String(bodyBytes, httpRequest.getCharset());
		//							log.debug("{} multipart body value\r\n{}", channelContext, bodyString);
		//						} catch (UnsupportedEncodingException e) {
		//							log.error(channelContext.toString(), e);
		//						}
		//					}
		//				}
		//			}
		//
		//			//【multipart/form-data; boundary=----WebKitFormBoundaryuwYcfA2AIgxqIxA0】
		//			String initboundary = HttpParseUtils.getPerprotyEqualValue(httpRequest.getHeaders(), HttpConst.RequestHeaderKey.Content_Type, "boundary");
		//			log.debug("{}, initboundary:{}", channelContext, initboundary);
		//			HttpMultiBodyDecoder.decode(httpRequest, firstLine, bodyBytes, initboundary, channelContext, httpConfig);
		//		} else {
		//			String bodyString = null;
		//			if (bodyBytes != null && bodyBytes.length > 0) {
		//				try {
		//					bodyString = new String(bodyBytes, httpRequest.getCharset());
		//					httpRequest.setBodyString(bodyString);
		//					if (log.isInfoEnabled()) {
		//						log.info("{} body value\r\n{}", channelContext, bodyString);
		//					}
		//				} catch (UnsupportedEncodingException e) {
		//					log.error(channelContext.toString(), e);
		//				}
		//			}
		//
		//			if (bodyFormat == RequestBodyFormat.URLENCODED) {
		//				parseUrlencoded(httpRequest, firstLine, bodyBytes, bodyString, channelContext);
		//			}
		//		}

		switch (bodyFormat) {
		case MULTIPART:
			if (log.isInfoEnabled()) {
				String bodyString = null;
				if (bodyBytes != null && bodyBytes.length > 0) {
					if (log.isDebugEnabled()) {
						try {
							bodyString = new String(bodyBytes, httpRequest.getCharset());
							log.debug("{} multipart body value\r\n{}", channelContext, bodyString);
						} catch (UnsupportedEncodingException e) {
							log.error(channelContext.toString(), e);
						}
					}
				}
			}

			//【multipart/form-data; boundary=----WebKitFormBoundaryuwYcfA2AIgxqIxA0】
			String contentType = httpRequest.getHeader(HttpConst.RequestHeaderKey.Content_Type);
			String initboundary = HttpParseUtils.getSubAttribute(contentType, "boundary");//.getPerprotyEqualValue(httpRequest.getHeaders(), HttpConst.RequestHeaderKey.Content_Type, "boundary");
			log.debug("{}, initboundary:{}", channelContext, initboundary);
			HttpMultiBodyDecoder.decode(httpRequest, firstLine, bodyBytes, initboundary, channelContext, httpConfig);
			break;

		default:
			String bodyString = null;
			if (bodyBytes != null && bodyBytes.length > 0) {
				try {
					bodyString = new String(bodyBytes, httpRequest.getCharset());
					httpRequest.setBodyString(bodyString);
					if (log.isInfoEnabled()) {
						log.info("{} body value\r\n{}", channelContext, bodyString);
					}
				} catch (UnsupportedEncodingException e) {
					log.error(channelContext.toString(), e);
				}
			}

			if (bodyFormat == RequestBodyFormat.URLENCODED) {
				parseUrlencoded(httpRequest, firstLine, bodyBytes, bodyString, channelContext);
			}
			break;
		}
	}

	/**
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * Content-Type : application/x-www-form-urlencoded; charset=UTF-8
	 * @param httpRequest
	 * @param headers
	 * @author tanyaowu
	 */
	public static void parseBodyFormat(HttpRequest httpRequest, Map<String, String> headers) {
		String contentType = headers.get(HttpConst.RequestHeaderKey.Content_Type);
		String Content_Type = null;
		if (contentType != null) {
			Content_Type = contentType.toLowerCase();
		}

		if (Content_Type.startsWith(HttpConst.RequestHeaderValue.Content_Type.text_plain)) {
			httpRequest.setBodyFormat(RequestBodyFormat.TEXT);
		} else if (Content_Type.startsWith(HttpConst.RequestHeaderValue.Content_Type.multipart_form_data)) {
			httpRequest.setBodyFormat(RequestBodyFormat.MULTIPART);
		} else {
			httpRequest.setBodyFormat(RequestBodyFormat.URLENCODED);
		}

		if (StrUtil.isNotBlank(Content_Type)) {
			String charset = HttpParseUtils.getSubAttribute(Content_Type, "charset");//.getPerprotyEqualValue(headers, HttpConst.RequestHeaderKey.Content_Type, "charset");
			if (StrUtil.isNotBlank(charset)) {
				httpRequest.setCharset(charset);
			} else {
				httpRequest.setCharset(SysConst.DEFAULT_ENCODING);
			}
		}
	}

	/**
	 * 解析请求头的每一行
	 * @param buffer
	 * @param headers
	 * @param hasReceivedHeaderLength
	 * @param httpConfig
	 * @return 头部是否解析完成，true: 解析完成, false: 没有解析完成
	 * @throws AioDecodeException
	 * @author tanyaowu
	 */
	public static boolean parseHeaderLine(ByteBuffer buffer, Map<String, String> headers, int hasReceivedHeaderLength, HttpConfig httpConfig) throws AioDecodeException {
		if (!buffer.hasArray()) {
			return parseHeaderLine2(buffer, headers, hasReceivedHeaderLength, httpConfig);
		}

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

		int lineLength = buffer.position() - initPosition; //这一行(header line)的字节数
		//		log.error("lineLength:{}, headerLength:{}, headers:\r\n{}", lineLength, hasReceivedHeaderLength, Json.toFormatedJson(headers));
		if (lineLength > MAX_LENGTH_OF_HEADERLINE) {
			//			log.error("header line is too long, max length of header line is " + MAX_LENGTH_OF_HEADERLINE);
			throw new AioDecodeException("header line is too long, max length of header line is " + MAX_LENGTH_OF_HEADERLINE);
		}

		if (needIteration) {
			int headerLength = lineLength + hasReceivedHeaderLength; //header占用的字节数
			//			log.error("allHeaderLength:{}", allHeaderLength);
			if (headerLength > MAX_LENGTH_OF_HEADER) {
				//				log.error("header is too long, max length of header is " + MAX_LENGTH_OF_HEADER);
				throw new AioDecodeException("header is too long, max length of header is " + MAX_LENGTH_OF_HEADER);
			}
			return parseHeaderLine(buffer, headers, headerLength, httpConfig);
		}

		return false;
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @param headers
	 * @return 头部是否解析完成，true: 解析完成, false: 没有解析完成
	 * @author tanyaowu
	 */
	private static boolean parseHeaderLine2(ByteBuffer buffer, Map<String, String> headers, int headerLength, HttpConfig httpConfig) throws AioDecodeException {
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
					int nowPosition = buffer.position();
					byte[] bs = new byte[nowPosition - lastPosition - 1];
					buffer.position(lastPosition);
					buffer.get(bs);
					name = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);
				} else if (b == SysConst.LF) {
					int nowPosition = buffer.position();
					byte[] bs = null;
					byte lastByte = buffer.get(nowPosition - 2);

					if (lastByte == SysConst.CR) {
						bs = new byte[nowPosition - lastPosition - 2];
					} else {
						bs = new byte[nowPosition - lastPosition - 1];
					}

					buffer.position(lastPosition);
					buffer.get(bs);
					name = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);

					headers.put(name.toLowerCase(), null);
					needIteration = true;
					break;
					//					return true;
				}
				continue;
			} else if (value == null) {
				if (b == SysConst.LF) {
					int nowPosition = buffer.position();
					byte[] bs = null;
					byte lastByte = buffer.get(nowPosition - 2);

					if (lastByte == SysConst.CR) {
						bs = new byte[nowPosition - lastPosition - 2];
					} else {
						bs = new byte[nowPosition - lastPosition - 1];
					}

					buffer.position(lastPosition);
					buffer.get(bs);
					value = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);

					headers.put(name.toLowerCase(), StrUtil.trimEnd(value));
					needIteration = true;
					break;
					//					return true;
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
			return parseHeaderLine(buffer, headers, myHeaderLength + headerLength, httpConfig);
		}

		if (remaining > MAX_LENGTH_OF_HEADERLINE) {
			throw new AioDecodeException("header line is too long");
		}
		return false;
	}

	/**
	 * parse request line(the first line)
	 * @param line GET /tio?value=tanyaowu HTTP/1.1
	 * @param channelContext
	 * @return
	 *
	 * @author tanyaowu
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static RequestLine parseRequestLine(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		if (!buffer.hasArray()) {
			return parseRequestLine2(buffer, channelContext);
		}

		byte[] allbs = buffer.array();

		int initPosition = buffer.position();

		//		int remaining = buffer.remaining();
		String methodStr = null;
		String pathStr = null;
		String queryStr = null;
		String protocol = null;
		String version = null;
		int lastPosition = initPosition;//buffer.position();
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (methodStr == null) {
				if (b == SysConst.SPACE) {
					int len = buffer.position() - lastPosition - 1;
					methodStr = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				}
				continue;
			} else if (pathStr == null) {
				if (b == SysConst.SPACE || b == SysConst.ASTERISK) {
					int len = buffer.position() - lastPosition - 1;
					pathStr = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();

					if (b == SysConst.SPACE) {
						queryStr = "";
					}
				}
				continue;
			} else if (queryStr == null) {
				if (b == SysConst.SPACE) {
					int len = buffer.position() - lastPosition - 1;
					queryStr = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				}
				continue;
			} else if (protocol == null) {
				if (b == '/') {
					int len = buffer.position() - lastPosition - 1;
					protocol = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();
				}
				continue;
			} else if (version == null) {
				if (b == SysConst.LF) {
					byte lastByte = buffer.get(buffer.position() - 2);
					int len = buffer.position() - lastPosition - 1;
					if (lastByte == SysConst.CR) {
						len = buffer.position() - lastPosition - 2;
					}
					version = new String(allbs, lastPosition, len);
					lastPosition = buffer.position();

					RequestLine requestLine = new RequestLine();
					Method method = Method.from(methodStr);
					requestLine.setMethod(method);
					requestLine.setPath(pathStr);
					requestLine.setInitPath(pathStr);
					requestLine.setQueryString(queryStr);
					requestLine.setProtocol(protocol);
					requestLine.setVersion(version);

					//					requestLine.setLine(line);

					return requestLine;
				}
				continue;
			}
		}

		if ((buffer.position() - initPosition) > MAX_LENGTH_OF_REQUESTLINE) {
			throw new AioDecodeException("request line is too long");
		}
		return null;
	}

	private static RequestLine parseRequestLine2(ByteBuffer buffer, ChannelContext channelContext) throws AioDecodeException {
		int initPosition = buffer.position();
		//		int remaining = buffer.remaining();
		String methodStr = null;
		String pathStr = null;
		String queryStr = null;
		String protocol = null;
		String version = null;
		int lastPosition = initPosition;//buffer.position();
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (methodStr == null) {
				if (b == SysConst.SPACE) {
					int nowPosition = buffer.position();
					byte[] bs = new byte[nowPosition - lastPosition - 1];
					buffer.position(lastPosition);
					buffer.get(bs);
					methodStr = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);
				}
				continue;
			} else if (pathStr == null) {
				if (b == SysConst.SPACE || b == SysConst.ASTERISK) {
					int nowPosition = buffer.position();
					byte[] bs = new byte[nowPosition - lastPosition - 1];
					buffer.position(lastPosition);
					buffer.get(bs);
					pathStr = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);

					if (b == SysConst.SPACE) {
						queryStr = "";
					}
				}
				continue;
			} else if (queryStr == null) {
				if (b == SysConst.SPACE) {
					int nowPosition = buffer.position();
					byte[] bs = new byte[nowPosition - lastPosition - 1];
					buffer.position(lastPosition);
					buffer.get(bs);
					queryStr = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);
				}
				continue;
			} else if (protocol == null) {
				if (b == '/') {
					int nowPosition = buffer.position();
					byte[] bs = new byte[nowPosition - lastPosition - 1];
					buffer.position(lastPosition);
					buffer.get(bs);
					protocol = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);
				}
				continue;
			} else if (version == null) {
				if (b == SysConst.LF) {
					int nowPosition = buffer.position();
					byte[] bs = null;
					byte lastByte = buffer.get(nowPosition - 2);

					if (lastByte == SysConst.CR) {
						bs = new byte[nowPosition - lastPosition - 2];
					} else {
						bs = new byte[nowPosition - lastPosition - 1];
					}

					buffer.position(lastPosition);
					buffer.get(bs);
					version = new String(bs);
					lastPosition = nowPosition;
					buffer.position(nowPosition);

					RequestLine requestLine = new RequestLine();
					Method method = Method.from(methodStr);
					requestLine.setMethod(method);
					requestLine.setPath(pathStr);
					requestLine.setInitPath(pathStr);
					requestLine.setQueryString(queryStr);
					requestLine.setProtocol(protocol);
					requestLine.setVersion(version);

					//					requestLine.setLine(line);

					return requestLine;
				}
				continue;
			}
		}

		if ((buffer.position() - initPosition) > MAX_LENGTH_OF_REQUESTLINE) {
			throw new AioDecodeException("request line is too long");
		}
		return null;
	}

	/**
	 * 解析URLENCODED格式的消息体
	 * 形如： 【Content-Type : application/x-www-form-urlencoded; charset=UTF-8】
	 * @author tanyaowu
	 */
	private static void parseUrlencoded(HttpRequest httpRequest, RequestLine firstLine, byte[] bodyBytes, String bodyString, ChannelContext channelContext) {
		decodeParams(httpRequest.getParams(), bodyString, httpRequest.getCharset(), channelContext);
	}

	//	/**
	//	 * 解析查询
	//	 * @param httpRequest
	//	 * @param requestLine
	//	 * @param channelContext
	//	 */
	//	private static void parseQueryString(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) {
	//		String queryString = requestLine.getQueryString();
	//		decodeParams(httpRequest.getParams(), queryString, httpRequest.getCharset(), channelContext);
	//	}

	/**
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public HttpRequestDecoder() {

	}

}
