package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.exception.LengthOverflowException;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.http.common.HttpConst.RequestBodyFormat;
import org.tio.http.common.utils.HttpParseUtils;

import cn.hutool.core.util.StrUtil;
import jodd.util.StringUtil;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpRequestDecoder {
	public static enum Step {
		firstline, header, body
	}

	private static Logger log = LoggerFactory.getLogger(HttpRequestDecoder.class);

	/**
	 * 头部，最多有多少字节
	 */
	public static final int MAX_LENGTH_OF_HEADER = 20480;

	/**
	 * 头部，每行最大的字节数
	 */
	public static final int MAX_LENGTH_OF_HEADERLINE = 2048;

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
		Step step = Step.firstline;
		//		StringBuilder currLine = new StringBuilder();
		Map<String, String> headers = new HashMap<>();
		int contentLength = 0;
		byte[] bodyBytes = null;
		StringBuilder headerSb = null;//new StringBuilder(512);
		RequestLine firstLine = null;
		boolean appendRequestHeaderString = false;
		if (httpConfig != null) {
			appendRequestHeaderString = httpConfig.isAppendRequestHeaderString();
		}
		if (appendRequestHeaderString) {
			headerSb = new StringBuilder(512);
		}

		while (buffer.hasRemaining()) {
			String line;
			try {
				line = ByteBufferUtils.readLine(buffer, null, MAX_LENGTH_OF_HEADERLINE);
			} catch (LengthOverflowException e) {
				throw new AioDecodeException(e);
			}

			int newPosition = buffer.position();
			if (newPosition - position > MAX_LENGTH_OF_HEADER) {
				throw new AioDecodeException("max http header length " + MAX_LENGTH_OF_HEADER);
			}

			if (line == null) {
				return null;
			}

			if (appendRequestHeaderString) {
				headerSb.append(line).append("\n");
			}

			if ("".equals(line)) {//头部解析完成了
				String contentLengthStr = headers.get(HttpConst.RequestHeaderKey.Content_Length);
				if (StringUtils.isBlank(contentLengthStr)) {
					contentLength = 0;
				} else {
					contentLength = Integer.parseInt(contentLengthStr);
				}

				//				int readableLength = buffer.limit() - buffer.position();
				int headerLength = (buffer.position() - position);
				int allNeedLength = headerLength + contentLength; //这个packet所需要的字节长度(含头部和体部)
				if (readableLength >= allNeedLength) {
					step = Step.body;
					break;
				} else {
					channelContext.setPacketNeededLength(allNeedLength);
					return null;
				}
			} else {
				if (step == Step.firstline) {
					firstLine = parseRequestLine(line, channelContext);
					step = Step.header;
				} else if (step == Step.header) {
					parseHeaderLine(line, headers);
				}
				continue;
			}
		}

		if (step != Step.body) {
			return null;
		}

		if (!headers.containsKey(HttpConst.RequestHeaderKey.Host)) {
			throw new AioDecodeException("there is no host header");
		}

		HttpRequest httpRequest = new HttpRequest(channelContext.getClientNode());
		httpRequest.setChannelContext(channelContext);
		httpRequest.setHttpConfig((HttpConfig) channelContext.groupContext.getAttribute(GroupContextKey.HTTP_SERVER_CONFIG));

		if (appendRequestHeaderString) {
			httpRequest.setHeaderString(headerSb.toString());
		} else {
			httpRequest.setHeaderString("");
		}

		httpRequest.setRequestLine(firstLine);
		httpRequest.setHeaders(headers);
		httpRequest.setContentLength(contentLength);
		String connection = headers.get(HttpConst.RequestHeaderKey.Connection);
		if (connection != null) {
			httpRequest.setConnection(connection.toLowerCase());
		}

		parseQueryString(httpRequest, firstLine, channelContext);

		if (contentLength == 0) {
			//			if (StringUtils.isNotBlank(firstLine.getQuery())) {
			//				decodeParams(httpRequest.getParams(), firstLine.getQuery(), httpRequest.getCharset(), channelContext);
			//			}
		} else {
			bodyBytes = new byte[contentLength];
			buffer.get(bodyBytes);
			httpRequest.setBody(bodyBytes);
			//解析消息体
			parseBody(httpRequest, firstLine, bodyBytes, channelContext, httpConfig);
		}

		//解析User_Agent(浏览器操作系统等信息)
		//		String User_Agent = headers.get(HttpConst.RequestHeaderKey.User_Agent);
		//		if (StringUtils.isNotBlank(User_Agent)) {
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
		//			logstr.append(StringUtils.leftPad(entry.getKey(), 30)).append(" : ").append(entry.getValue()).append("\r\n");
		//		}
		//		logstr.append("------------------ websocket header start ------------------------\r\n");
		//		log.error(logstr.toString());

		return httpRequest;

	}

	public static void decodeParams(Map<String, Object[]> params, String queryString, String charset, ChannelContext channelContext) {
		if (StrUtil.isBlank(queryString)) {
			return;
		}

		//		// 去掉Path部分
		//		int pathEndPos = paramsStr.indexOf('?');
		//		if (pathEndPos > 0) {
		//			paramsStr = StrUtil.subSuf(paramsStr, pathEndPos + 1);
		//		}
		//		Map<String, Object[]> ret = new HashMap<>();
		String[] keyvalues = StringUtils.split(queryString, "&");
		for (String keyvalue : keyvalues) {
			String[] keyvalueArr = StringUtils.split(keyvalue, "=");
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
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 * @throws AioDecodeException 
	 *
	 */
	public static void main(String[] args) throws AioDecodeException {
		String line = "GET /tio?name=tanyaowu HTTP/1.1";
		parseRequestLine(line, null);

		Map<String, String> headers = new HashMap<>();
		line = "host:127.0.0.1  ";
		parseHeaderLine(line, headers);

		line = "host:  127.0.0.1  ";
		parseHeaderLine(line, headers);
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
		//							log.debug("{} multipart body string\r\n{}", channelContext, bodyString);
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
		//						log.info("{} body string\r\n{}", channelContext, bodyString);
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
							log.debug("{} multipart body string\r\n{}", channelContext, bodyString);
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
						log.info("{} body string\r\n{}", channelContext, bodyString);
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
		String Content_Type = StringUtils.lowerCase(headers.get(HttpConst.RequestHeaderKey.Content_Type));

		if (StringUtils.startsWith(Content_Type, HttpConst.RequestHeaderValue.Content_Type.text_plain)) {
			httpRequest.setBodyFormat(RequestBodyFormat.TEXT);
		} else if (StringUtils.startsWith(Content_Type, HttpConst.RequestHeaderValue.Content_Type.multipart_form_data)) {
			httpRequest.setBodyFormat(RequestBodyFormat.MULTIPART);
		} else {
			httpRequest.setBodyFormat(RequestBodyFormat.URLENCODED);
		}

		if (StringUtils.isNotBlank(Content_Type)) {
			String charset = HttpParseUtils.getSubAttribute(Content_Type, "charset");//.getPerprotyEqualValue(headers, HttpConst.RequestHeaderKey.Content_Type, "charset");
			if (StringUtils.isNotBlank(charset)) {
				httpRequest.setCharset(charset);
			} else {
				httpRequest.setCharset(httpRequest.getHttpConfig().getCharset());
			}
		}
	}

	/**
	 * 解析请求头的每一行
	 * @param line
	 * @param headers
	 * @return
	 * @author tanyaowu
	 */
	public static void parseHeaderLine(String line, Map<String, String> headers) {
		int len = line.length();
//		char[] cs = new char[len];
		int i = 0;
//		boolean forname = true;
		int indexOfColon = -1;
//		int valueLen = 0;
//		boolean lastIsColon = false;
		int skip = 0;
		for (; i < len; i++) {
//			char c = line.charAt(i);
			if (indexOfColon == -1) {
				if (line.charAt(i) == ':') {
//					forname = false;
					indexOfColon = i;
//					lastIsColon = true;
				}
			} else {
//				if (lastIsColon) {
					if (line.charAt(i) == ' ') {
						skip++;
					} else {
//						lastIsColon = false;
						break;
					}
//				}
				
//				if (forname) {
////					cs[i] = Character.toLowerCase(c);
//				} else {
//					if (lastIsColon) {
//						if (c == ' ') {
//							skip++;
//						} else {
//							lastIsColon = false;
//							break;
//						}
//					}
//
////					if (!lastIsColon) {
////						cs[i] = c;
////						valueLen++;
////					}
//				}
			}
		}

		if (indexOfColon == -1) {//没有value
			headers.put(line.trim().toLowerCase(), "");
			return;
		}
		String name = line.substring(0, indexOfColon).toLowerCase();//String.copyValueOf(cs, 0, indexOfColon);
		String value = StringUtil.trimRight(line.substring(indexOfColon + 1 + skip, len));//String.copyValueOf(cs, indexOfColon + 1 + skip, valueLen);
		headers.put(name.trim(), value);
//		if (valueLen > 0) {
//			String value = line.substring(indexOfColon + 1 + skip, len);//String.copyValueOf(cs, indexOfColon + 1 + skip, valueLen);
//			headers.put(name.trim(), StringUtil.trimRight(value));
//		} else {
//			headers.put(name.trim(), "");
//		}

		//		int p = line.indexOf(":");
		//		if (p == -1) {
		//			headers.put(line, "");
		//		}
		//
		//		String name = line.substring(0, p).trim().toLowerCase();//StringUtils.lowerCase(line.substring(0, p).trim());
		//		String value = line.substring(p + 1).trim();
		//
		//		headers.put(name, value);

	}

	/**
	 * parse request line(the first line)
	 * @param line GET /tio?name=tanyaowu HTTP/1.1
	 * @param channelContext
	 * @return
	 *
	 * @author tanyaowu
	 * 2017年2月23日 下午1:37:51
	 *
	 */
	public static RequestLine parseRequestLine(String line, ChannelContext channelContext) throws AioDecodeException {
		try {
			int len = line.length();
			if (len < 10) {
				throw new AioDecodeException("request line is invalid [" + line + "]");
			}

			// parse method start
//			char[] cs = new char[len];
			int i = 0;
			for (; i < 8; i++) {
				if (line.charAt(i) == ' ') {
					break;
				}
//				cs[i] = c;
			}
			String methodStr = line.substring(0, 0 + i);//String.copyValueOf(cs, 0, i);
			Method method = Method.from(methodStr);
			if (method == null) {
				throw new AioDecodeException("http request method [" + methodStr + "] is invalid");
			}
			// parse method end

			// parse path start
			i++; //
			int offset = i;
			boolean hasQuery = false;
			for (; i < len; i++) {
				char c = line.charAt(i);
				if (c == '?') {
					hasQuery = true;
					break;
				} else if (c == ' ') {
					break;
				}
//				cs[i] = c;
			}
			String path = line.substring(offset, i);//String.copyValueOf(cs, offset, i - offset);
			String queryString = null;
			if (hasQuery) {
				i++; //
				offset = i;
				for (; i < len; i++) {
					if (line.charAt(i) == ' ') {
						break;
					}
//					cs[i] = c;
				}
				queryString = line.substring(offset, i);//String.copyValueOf(cs, offset, i - offset);
			}
			// parse path end

			// parse protocol start
			i++; //
			offset = i;
			for (; i < len; i++) {
				if (line.charAt(i) == '/') {
					break;
				}
//				cs[i] = c;
			}
			String protocol = line.substring(offset, i);//String.copyValueOf(cs, offset, i - offset);
			// parse protocol end

			// parse protocol start
			i++; //
			offset = i;
//			for (; i < len; i++) {
//				char c = line.charAt(i);
//				cs[i] = c;
//			}
			String version = line.substring(offset, len);//String.copyValueOf(cs, offset, i - offset);
			// parse protocol end

			RequestLine requestLine = new RequestLine();
			requestLine.setMethod(method);
			requestLine.setPath(path);
			requestLine.setInitPath(path);
			requestLine.setQueryString(queryString);
			requestLine.setVersion(version);
			requestLine.setProtocol(protocol);
			requestLine.setLine(line);

			return requestLine;

			//			int index1 = line.indexOf(' ');
			//			String _method = StringUtils.upperCase(line.substring(0, index1));
			//			Method method = Method.from(_method);
			//			if (method == null) {
			//				throw new AioDecodeException("http request method [" + _method + "] is invalid");
			//			}
			//			int index2 = line.indexOf(' ', index1 + 1);
			//			String pathAndQuerystr = line.substring(index1 + 1, index2); // "/tio?name=tanyaowu"
			//			String path = null; //"/user/get"
			//			String queryStr = null;
			//			int indexOfQuestionmark = pathAndQuerystr.indexOf("?");
			//			if (indexOfQuestionmark != -1) {
			//				queryStr = StringUtils.substring(pathAndQuerystr, indexOfQuestionmark + 1);
			//				path = StringUtils.substring(pathAndQuerystr, 0, indexOfQuestionmark);
			//			} else {
			//				path = pathAndQuerystr;
			//				queryStr = "";
			//			}
			//
			//			String protocolVersion = line.substring(index2 + 1);
			//			String[] pv = StringUtils.split(protocolVersion, "/");
			//			String protocol = pv[0];
			//			String version = pv[1];
			//
			//			RequestLine requestLine = new RequestLine();
			//			requestLine.setMethod(method);
			//			requestLine.setPath(path);
			//			requestLine.setInitPath(path);
			//			requestLine.setPathAndQuery(pathAndQuerystr);
			//			requestLine.setQuery(queryStr);
			//			requestLine.setVersion(version);
			//			requestLine.setProtocol(protocol);
			//			requestLine.setLine(line);
			//
			//			return requestLine;
		} catch (Throwable e) {
			if (channelContext != null) {
				log.error(channelContext.toString() + " parse http request line error", e);
			} else {
				log.error("parse http request line error", e);
			}

			throw new AioDecodeException(e);
		}
	}

	/**
	 * 解析URLENCODED格式的消息体
	 * 形如： 【Content-Type : application/x-www-form-urlencoded; charset=UTF-8】
	 * @author tanyaowu
	 */
	private static void parseUrlencoded(HttpRequest httpRequest, RequestLine firstLine, byte[] bodyBytes, String bodyString, ChannelContext channelContext) {
		decodeParams(httpRequest.getParams(), bodyString, httpRequest.getCharset(), channelContext);
	}

	/**
	 * 解析查询
	 * @param httpRequest
	 * @param requestLine
	 * @param channelContext
	 */
	private static void parseQueryString(HttpRequest httpRequest, RequestLine requestLine, ChannelContext channelContext) {
		String queryString = requestLine.getQueryString();
		decodeParams(httpRequest.getParams(), queryString, httpRequest.getCharset(), channelContext);
	}

	/**
	 * @author tanyaowu
	 * 2017年2月22日 下午4:06:42
	 *
	 */
	public HttpRequestDecoder() {

	}

}
