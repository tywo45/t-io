package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.utils.HttpGzipUtils;
import org.tio.utils.HttpDateTimer;


/**
 *
 * @author tanyaowu
 *
 */
public class HttpResponse extends HttpPacket {
	private static Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private static final long serialVersionUID = -3512681144230291786L;

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public static void main(String[] args) {
	}
	
	public static HttpResponse cloneResponse(HttpRequest request, HttpResponse response) {
		HttpResponse cloneResponse = new HttpResponse(request);
		cloneResponse.setStatus(response.getStatus());
		cloneResponse.setBody(response.getBody());
		cloneResponse.setHasGzipped(response.isHasGzipped());
		cloneResponse.addHeaders(response.getHeaders());
		
		if (cloneResponse.getCookies() != null) {
			cloneResponse.getCookies().clear();
		}
		return cloneResponse;
	}

	private HttpResponseStatus status = HttpResponseStatus.C200;

	/**
	 * 是否是静态资源
	 * true: 静态资源
	 */
	private boolean isStaticRes = false;

	private HttpRequest request = null;
	private List<Cookie> cookies = null;
	
	/**
	 * 是否已经被gzip压缩过了，防止重复压缩
	 */
	private boolean hasGzipped = false;

	//	private int contentLength;
	//	private byte[] bodyBytes;
	private String charset = HttpConst.CHARSET_NAME;

//	/**
//	 * 已经编码好的byte[]
//	 */
//	private byte[] encodedBytes = null;

	/**
	 * 忽略ip访问统计
	 */
	private boolean skipIpStat = false;
	/**
	 * 忽略token访问统计
	 */
	private boolean skipTokenStat = false;
	
//	private String lastModified = null;//HttpConst.ResponseHeaderKey.Last_Modified
	
//	/**
//	 *
//	 * @param request
//	 * @param httpConfig 可以为null
//	 * @author tanyaowu
//	 */
//	public HttpResponse(HttpRequest request, HttpConfig httpConfig) {
//		this.request = request;
//
//		String Connection = StringUtils.lowerCase(request.getHeader(HttpConst.RequestHeaderKey.Connection));
//		RequestLine requestLine = request.getRequestLine();
//		String version = requestLine.getVersion();
//		if ("1.0".equals(version)) {
//			if (StringUtils.equals(Connection, HttpConst.RequestHeaderValue.Connection.keep_alive)) {
//				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
//				addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
//			} else {
//				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
//			}
//		} else {
//			if (StringUtils.equals(Connection, HttpConst.RequestHeaderValue.Connection.close)) {
//				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
//			} else {
//				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
//				addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
//			}
//		}
//		
//
//		if (httpConfig != null) {
//			addHeader(HttpConst.ResponseHeaderKey.Server, httpConfig.getServerInfo());
//		}
//		//		String xx = DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis());
//		//		addHeader(HttpConst.ResponseHeaderKey.Date, DatePattern.HTTP_DATETIME_FORMAT.format(SystemTimer.currentTimeMillis()));
//		//		addHeader(HttpConst.ResponseHeaderKey.Date, new Date().toGMTString());
//	}
	
	/**
	 * 
	 * @param request
	 */
	public HttpResponse(HttpRequest request) {
		this.request = request;

		String connection = request.getConnection();//StringUtils.lowerCase(request.getHeader(HttpConst.RequestHeaderKey.Connection));
		String version = request.getRequestLine().getVersion();

		switch (version) {
		case "1.0":
			if (StringUtils.equals(connection, HttpConst.RequestHeaderValue.Connection.keep_alive)) {
				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
				addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
			} else {
				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
			}
			break;

		default:
			if (StringUtils.equals(connection, HttpConst.RequestHeaderValue.Connection.close)) {
				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.close);
			} else {
				addHeader(HttpConst.ResponseHeaderKey.Connection, HttpConst.ResponseHeaderValue.Connection.keep_alive);
				addHeader(HttpConst.ResponseHeaderKey.Keep_Alive, "timeout=10, max=20");
			}
			break;
		}
		HttpConfig httpConfig = request.getHttpConfig();
		if (httpConfig != null) {
			addHeader(HttpConst.ResponseHeaderKey.Server, httpConfig.getServerInfo());
		}
		addHeader(HttpConst.ResponseHeaderKey.Date, HttpDateTimer.currDateString());
	}
	
	/**
	 * 
	 * @param responseHeaders
	 * @param body
	 */
	public HttpResponse(Map<String, String> responseHeaders, byte[] body) {
		if (responseHeaders != null) {
			this.headers.putAll(responseHeaders);
		}
		this.setBody(body);
		HttpGzipUtils.gzip(this);
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
	 * 获取"Content-Type"头部内容
	 * @return
	 * @author tanyaowu
	 */
	public String getContentType() {
		return this.headers.get(HttpConst.RequestHeaderKey.Content_Type);
	}

	public boolean addCookie(Cookie cookie) {
		if (cookies == null) {
//			synchronized (this) {
//				if (cookies == null) {
//					cookies = new ArrayList<>();
//				}
//			}
			
			cookies = new ArrayList<>();
		}
//		log.error("cookie domain:{}, value:{}", cookie.getDomain(), cookie.getValue());
		return cookies.add(cookie);
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

//	/**
//	 * @return the encodedBytes
//	 */
//	public byte[] getEncodedBytes() {
//		return encodedBytes;
//	}

	/**
	 * @return the request
	 */
	public HttpRequest getHttpRequest() {
		return request;
	}

	/**
	 * @return the status
	 */
	public HttpResponseStatus getStatus() {
		return status;
	}



	/**
	 * @return the isStaticRes
	 */
	public boolean isStaticRes() {
		return isStaticRes;
	}

	@Override
	public String logstr() {
		String str = null;
		if (request != null) {
			str = "\r\n响应: 请求ID_" + request.getId() + "  " + request.getRequestLine().getPathAndQuery();
			str += "\r\n" + this.getHeaderString();
		} else {
			str = "\r\n响应\r\n" + status.getHeaderText();
		}
		return str;
	}


	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
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

	/**
	 * @param request the request to set
	 */
	public void setHttpRequestPacket(HttpRequest request) {
		this.request = request;
	}

	/**
	 * @param isStaticRes the isStaticRes to set
	 */
	public void setStaticRes(boolean isStaticRes) {
		this.isStaticRes = isStaticRes;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(HttpResponseStatus status) {
		this.status = status;
	}

	public boolean isHasGzipped() {
		return hasGzipped;
	}

	public void setHasGzipped(boolean hasGzipped) {
		this.hasGzipped = hasGzipped;
	}

	public boolean isSkipIpStat() {
		return skipIpStat;
	}

	public void setSkipIpStat(boolean skipIpStat) {
		this.skipIpStat = skipIpStat;
	}

	public boolean isSkipTokenStat() {
		return skipTokenStat;
	}

	public void setSkipTokenStat(boolean skipTokenStat) {
		this.skipTokenStat = skipTokenStat;
	}

	public String getLastModified() {
//		if (lastModified != null) {
//			return lastModified;
//		}
		return this.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);
	}

	public void setLastModified(String lastModified) {
		if (StringUtils.isNotBlank(lastModified)) {
//			this.lastModified = lastModified;
			this.headers.put(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
		}
	}
	
	@Override
	public String toString() {
		String ret =  this.getHeaderString();
		if (this.getBody() != null) {
			try {
				ret += new String(this.getBody(), this.request.getCharset());
			} catch (UnsupportedEncodingException e) {
				log.error(e.toString(), e);
			}
		}
		return ret;//requestLine.getPathAndQuery() + System.lineSeparator() + Json.toFormatedJson(params);
	}
}
