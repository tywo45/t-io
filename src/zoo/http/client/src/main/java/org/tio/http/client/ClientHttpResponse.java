package org.tio.http.client;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.http.common.Cookie;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpPacket;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.ResponseLine;

/**
 *
 * @author tanyaowu
 *
 */
public class ClientHttpResponse extends HttpPacket {
	private static Logger log = LoggerFactory.getLogger(ClientHttpResponse.class);

	private static final long serialVersionUID = 6894945438284158658L;

	public ChannelContext channelContext;
	//	private int contentLength;
	//	private byte[] bodyBytes;
	private String	charset			= HttpConst.CHARSET_NAME;
	public Integer	contentLength	= null;

	private List<Cookie>		cookies	= null;
	private Map<String, String>	headers	= new HashMap<>();

	public HttpConfig httpConfig;

	private HttpRequest request = null;

	/**
	 * 客户端用（客户端是根据对端）
	 */
	public ResponseLine responseLine;

	public String connection;

	public String bodyString;

	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	public ClientHttpResponse() {

	}

	public ClientHttpResponse(HttpRequest request) {
		this.request = request;
	}

	public ClientHttpResponse(Map<String, String> responseHeaders, byte[] body) {
		this.headers.putAll(responseHeaders);
		this.body = body;
	}

	public boolean addCookie(Cookie cookie) {
		if (cookies == null) {
			cookies = new ArrayList<>();
		}
		return cookies.add(cookie);
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	public void addHeaders(Map<String, String> headers) {
		if (headers != null) {
			Set<Entry<String, String>> set = headers.entrySet();
			for (Entry<String, String> entry : set) {
				this.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * 获取"Content-Type"头部内容
	 * @return
	 * @author tanyaowu
	 */
	public String getContentType() {
		return this.headers.get(HttpConst.ResponseHeaderKey.Content_Type);
	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	private String getHeader(String key) {
		return headers.get(key);
	}

	/**
	 * <span style='color:red'>
	 *  <p style='color:red;font-size:12pt;'>警告：通过本方法获得Map<String, String>对象后，请勿调用put(key, value)。<p>
	 *  <p style='color:red;font-size:12pt;'>添加响应头只能通过HttpResponse.addHeader(String, String)或HttpResponse.addHeaders(Map<String, String> headers)方式添加<p>
	 * </span>
	 * @return
	 * @author tanyaowu
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * @return the request
	 */
	public HttpRequest getHttpRequest() {
		return request;
	}

	public String getLastModified() {
		//		if (lastModified != null) {
		//			return lastModified;
		//		}
		return this.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);
	}

	@Override
	public String logstr() {
		String str = null;
		if (request != null) {
			str = "\r\n响应: 请求ID_" + request.getId() + "  " + request.getRequestLine().getPathAndQuery();
			str += "\r\n" + this.getHeaderString();
		} else {
			str = "\r\n响应\r\n" + responseLine.toString();
		}
		return str;
	}

	//	/**
	//	 * @return the encodedBytes
	//	 */
	//	public byte[] getEncodedBytes() {
	//		return encodedBytes;
	//	}

	public void setChannelContext(ChannelContext channelContext) {
		this.channelContext = channelContext;

	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	//	/**
	//	 * @param encodedBytes the encodedBytes to set
	//	 */
	//	public void setEncodedBytes(byte[] encodedBytes) {
	//		this.encodedBytes = encodedBytes;
	//	}

	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}

	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	/**
	 * @param request the request to set
	 */
	public void setHttpRequestPacket(HttpRequest request) {
		this.request = request;
	}

	public void setLastModified(String lastModified) {
		if (lastModified != null) {
			//			this.lastModified = lastModified;
			this.addHeader(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
		}
	}

	public void setResponseLine(ResponseLine responseLine) {
		this.responseLine = responseLine;
	}

	@Override
	public String toString() {
		String ret = this.getHeaderString();
		if (this.getBody() != null) {
			try {
				ret += new String(this.getBody(), this.request.getCharset());
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		return ret;//requestLine.getPathAndQuery() + System.lineSeparator() + Json.toFormatedJson(params);
	}

	public void setConnection(String connection) {
		this.connection = connection;
	}

}
