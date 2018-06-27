package org.tio.http.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.http.common.HttpConst.RequestBodyFormat;
import org.tio.http.common.session.HttpSession;
import org.tio.http.common.utils.IpUtils;
import org.tio.utils.SystemTimer;

import cn.hutool.core.util.StrUtil;

/**
 *
 * @author tanyaowu
 *
 */
public class HttpRequest extends HttpPacket {

	//	private static Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private static final long serialVersionUID = -3849253977016967211L;

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public static void main(String[] args) {
	}

	private RequestLine requestLine = null;
	/**
	 * 请求参数
	 */
	private Map<String, Object[]> params = new HashMap<>();
	private List<Cookie> cookies = null;
	private Map<String, Cookie> cookieMap = null;
	private int contentLength;
	private String connection;
	//	private byte[] bodyBytes;
	private String bodyString;
	//	private UserAgent userAgent;
	private RequestBodyFormat bodyFormat;
	private String charset = HttpConst.CHARSET_NAME;
	private Boolean isAjax = null;
	@SuppressWarnings("unused")
	private Boolean isSupportGzip = null;
	private HttpSession httpSession;
	private Node remote = null;
	//	private HttpSession httpSession = null;
	private ChannelContext channelContext;

	private HttpConfig httpConfig;

	private String domain = null;
	private String host = null;
	private String clientIp = null;
	// 该HttpRequest对象的创建时间
	private long createTime = SystemTimer.currentTimeMillis();
	
	private boolean closed = false;

	/**
	 *
	 *
	 * @author tanyaowu
	 * 2017年2月22日 下午4:14:40
	 *
	 */
	public HttpRequest(Node remote) {
		this.remote = remote;
	}
	
	/**
	 * 关闭连接
	 */
	public void close() {
		close(null);
	}
	
	/**
	 * 关闭连接
	 * @param remark
	 */
	public void close(String remark) {
		closed = true;
		Tio.remove(channelContext, remark);
	}

	public void addParam(String key, Object value) {
		if (params == null) {
			params = new HashMap<>();
		}

		Object[] existValue = params.get(key);
		if (existValue != null) {
			Object[] newExistValue = new Object[existValue.length + 1];
			System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
			newExistValue[newExistValue.length - 1] = value;
			params.put(key, newExistValue);
		} else {
			Object[] newExistValue = new Object[] { value };
			params.put(key, newExistValue);
		}
	}

	/**
	 * @return the bodyFormat
	 */
	public RequestBodyFormat getBodyFormat() {
		return bodyFormat;
	}

	/**
	 * 获取请求头中的User-Agent字段
	 * @return
	 * @author: tanyaowu
	 */
	public String getUserAgent() {
		return this.headers.get(HttpConst.RequestHeaderKey.User_Agent);
	}

	/**
	 * 获取请求头中的host字段，形如：www.t-io.org:8080, www.t-io.org等值
	 * @return
	 * @author: tanyaowu
	 */
	public String getHost() {
		if (host != null) {
			return host;
		}
		
		host = this.headers.get(HttpConst.RequestHeaderKey.Host);
		return host;
	}
	
	/**
	 * 获取真实的客户端ip
	 * @return
	 * @author tanyaowu
	 */
	public String getClientIp() {
		if (clientIp == null) {
			clientIp = IpUtils.getRealIp(this);
		}		
		return clientIp;
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
	 * 根据host字段，获取去除端口的纯域名部分的值，形如：www.t-io.org, t-io.org等值
	 * @return
	 * @author tanyaowu
	 */
	public String getDomain() {
		if (domain != null) {
			return domain;
		}
		if (StrUtil.isBlank(getHost())) {
			return null;
		}
		domain = StrUtil.subBefore(getHost(), ":", false);
		return domain;
	}

	/**
	 * @return the bodyString
	 */
	public String getBodyString() {
		return bodyString;
	}

	/**
	 * @return the channelContext
	 */
	public ChannelContext getChannelContext() {
		return channelContext;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the bodyLength
	 */
	public int getContentLength() {
		return contentLength;
	}

	public Cookie getCookie(String cooiename) {
		if (cookieMap == null) {
			return null;
		}
		return cookieMap.get(cooiename);
	}

	/**
	 * @return the cookieMap
	 */
	public Map<String, Cookie> getCookieMap() {
		return cookieMap;
	}

	//	/**
	//	 * @return the bodyBytes
	//	 */
	//	public byte[] getBodyBytes() {
	//		return bodyBytes;
	//	}
	//
	//	/**
	//	 * @param bodyBytes the bodyBytes to set
	//	 */
	//	public void setBodyBytes(byte[] bodyBytes) {
	//		this.bodyBytes = bodyBytes;
	//	}

	//	/**
	//	 * @return the userAgent
	//	 */
	//	public UserAgent getUserAgent() {
	//		return userAgent;
	//	}
	//
	//	/**
	//	 * @param userAgent the userAgent to set
	//	 */
	//	public void setUserAgent(UserAgent userAgent) {
	//		this.userAgent = userAgent;
	//	}

	/**
	 * @return the cookies
	 */
	public List<Cookie> getCookies() {
		return cookies;
	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	/**
	 * @return the httpSession
	 */
	public HttpSession getHttpSession() {
		return httpSession;
	}

	/**
	 * @return the isAjax
	 */
	public Boolean getIsAjax() {
		if (isAjax == null) {
			String X_Requested_With = this.getHeader(HttpConst.RequestHeaderKey.X_Requested_With);
			if (X_Requested_With != null && "XMLHttpRequest".equalsIgnoreCase(X_Requested_With)) {
				isAjax = true;
			} else {
				isAjax = false;
			}
		}

		return isAjax;
	}

	/**
	 * @return the isSupportGzip
	 */
	public Boolean getIsSupportGzip() {
		return true;
//		if (isSupportGzip == null) {
//			String Accept_Encoding = getHeader(HttpConst.RequestHeaderKey.Accept_Encoding);
//			if (StringUtils.isNotBlank(Accept_Encoding)) {
//				String[] ss = StringUtils.split(Accept_Encoding, ",");
//				if (ArrayUtil.contains(ss, "gzip")) {
//					isSupportGzip = true;
//				} else {
//					isSupportGzip = false;
//				}
//			} else {
//				isSupportGzip = true;
//			}
//		}
//		return isSupportGzip;
	}

	/**
	 * @return the params
	 */
	public Map<String, Object[]> getParams() {
		return params;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @author: tanyaowu
	 */
	public String getParam(String name) {
		if (params == null) {
			return null;
		}
		Object[] values = params.get(name);
		if (values != null && values.length > 0) {
			Object obj = values[0];
			return (String) obj;
		}
		return null;
	}

	public Node getRemote() {
		return remote;
	}

	/**
	 * @return the firstLine
	 */
	public RequestLine getRequestLine() {
		return requestLine;
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String logstr() {
		String str = "\r\n请求ID_" + getId() + "\r\n" + getHeaderString();
		if (null != getBodyString()) {
			str += getBodyString();
		}
		return str;
	}

	public void parseCookie() {
		String cookieline = headers.get(HttpConst.RequestHeaderKey.Cookie);
		if (StringUtils.isNotBlank(cookieline)) {
			cookies = new ArrayList<>();
			cookieMap = new HashMap<>();
			Map<String, String> _cookiemap = Cookie.getEqualMap(cookieline);
			List<Map<String, String>> cookieListMap = new ArrayList<>();
			for (Entry<String, String> cookieMapEntry : _cookiemap.entrySet()) {
				HashMap<String, String> cookieOneMap = new HashMap<>();
				cookieOneMap.put(cookieMapEntry.getKey(), cookieMapEntry.getValue());
				cookieListMap.add(cookieOneMap);

				Cookie cookie = Cookie.buildCookie(cookieOneMap);
				cookies.add(cookie);
				cookieMap.put(cookie.getName(), cookie);
				//				log.error("{}, 收到cookie:{}", channelContext, cookie.toString());
			}
		}
	}

	/**
	 * @param bodyFormat the bodyFormat to set
	 */
	public void setBodyFormat(RequestBodyFormat bodyFormat) {
		this.bodyFormat = bodyFormat;
	}

	/**
	 * @param bodyString the bodyString to set
	 */
	public void setBodyString(String bodyString) {
		this.bodyString = bodyString;
	}

	/**
	 * @param channelContext the channelContext to set
	 */
	public void setChannelContext(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param bodyLength the bodyLength to set
	 */
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	/**
	 * @param cookieMap the cookieMap to set
	 */
	public void setCookieMap(Map<String, Cookie> cookieMap) {
		this.cookieMap = cookieMap;
	}

	/**
	 * @param cookies the cookies to set
	 */
	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	/**
	 * 设置好header后，会把cookie等头部信息也设置好
	 * @param headers the headers to set
	 * @param channelContext
	 */
	@Override
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
		if (headers != null) {
			parseCookie();
		}

		//		String Sec_WebSocket_Key = headers.get(HttpConst.RequestHeaderKey.Sec_WebSocket_Key);
		//		if (StringUtils.isNotBlank(Sec_WebSocket_Key)) {
		//			ImSessionContext httpSession = channelContext.getAttribute();
		//			httpSession.setWebsocket(true);
		//		}
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	/**
	 * @param httpSession the httpSession to set
	 */
	public void setHttpSession(HttpSession httpSession) {
		this.httpSession = httpSession;
	}

	/**
	 * @param isAjax the isAjax to set
	 */
	public void setIsAjax(Boolean isAjax) {
		this.isAjax = isAjax;
	}

	/**
	 * @param isSupportGzip the isSupportGzip to set
	 */
	public void setIsSupportGzip(Boolean isSupportGzip) {
		this.isSupportGzip = isSupportGzip;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, Object[]> params) {
		this.params = params;
	}

	public void setRemote(Node remote) {
		this.remote = remote;
	}

	/**
	 * @param requestLine the requestLine to set
	 */
	public void setRequestLine(RequestLine requestLine) {
		this.requestLine = requestLine;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		String ret =  this.getHeaderString();
		if (this.getBodyString() != null) {
			ret += this.getBodyString();
		}
		return ret;//requestLine.getPathAndQuery() + System.lineSeparator() + Json.toFormatedJson(params);
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	/**
	 * @return the connection
	 */
	public String getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(String connection) {
		this.connection = connection;
	}
	

	//	/**
	//	 * @return the httpSession
	//	 */
	//	public HttpSession getHttpSession() {
	//		return httpSession;
	//	}
	//
	//	/**
	//	 * @param httpSession the httpSession to set
	//	 */
	//	public void setHttpSession(HttpSession httpSession) {
	//		this.httpSession = httpSession;
	//	}

}
