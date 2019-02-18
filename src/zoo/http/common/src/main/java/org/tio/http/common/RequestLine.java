package org.tio.http.common;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 * 2017年6月28日 下午2:20:32
 */
public class RequestLine {
	public Method	method;
	public String	path;			//譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是/user/get
	private String	initPath;		//同path，只是path可能会被业务端修改，而这个是记录访问者访问的最原始path的
	public String	queryString;	//譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是name=tan&id=789
	//	private String pathAndQuery;  //形如：/user/get?value=999
	private String	protocol;
	public String	version;
	//	private String line;

	/**
	 * @return the line
	 */
	//	public String getLine() {
	//		return line;
	//	}

	/**
	 * @return the method
	 */
	public Method getMethod() {
		return method;
	}

	/**
	 * 譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是/user/get
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * /user/get?value=999
	 * @return
	 * @author tanyaowu
	 */
	public String getPathAndQuery() {
		if (StrUtil.isNotBlank(queryString)) {
			return path + "?" + queryString;
		}
		return path;
	}

	/**
	 * 譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是name=tan&id=789
	 * @return the queryString
	 */
	public String getQueryString() {
		return queryString;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param line the line to set
	 */
	//	public void setLine(String line) {
	//		this.line = line;
	//	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * 譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是/user/get
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	//	/**
	//	 * 形如：/user/get?value=999
	//	 * @param pathAndQuery
	//	 * @author tanyaowu
	//	 */
	//	public void setPathAndQuery(String pathAndQuery) {
	//		this.pathAndQuery = pathAndQuery;
	//	}

	/**
	 * 譬如http://www.163.com/user/get?value=tan&id=789，那些此值就是name=tan&id=789
	 * @param queryString the queryString to set
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getInitPath() {
		return initPath;
	}

	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(method.value).append(" ").append(path);
		if (StrUtil.isNotBlank(queryString)) {
			sb.append("?").append(queryString);
		}
		sb.append(" ");
		sb.append(protocol).append("/").append(version);

		return sb.toString();
	}

	/**
	 * 用来编码用的
	 * "GET /json?tan=谭耀武 HTTP/1.1" -->"GET /json?tan=%E8%B0%AD%E8%80%80%E6%AD%A6 HTTP/1.1"
	 * @return
	 * @author tanyaowu
	 */
	@SuppressWarnings("deprecation")
	public String toUrlEncodedString(String charset) {
		StringBuilder sb = new StringBuilder();
		sb.append(method.value).append(" ").append(path);
		if (StrUtil.isNotBlank(queryString)) {
			sb.append("?");//.append(queryString);
			String[] keyValues = queryString.split("&");
			for (String keyValue : keyValues) {
				String[] keyValueArray = keyValue.split("=");
				if (keyValueArray.length == 2) {
					String name = keyValueArray[0];
					String value = keyValueArray[1];
					if (StrUtil.isNotBlank(charset)) {
						try {
							sb.append(name).append("=").append(URLEncoder.encode(value, charset));
						} catch (UnsupportedEncodingException e) {
							sb.append(name).append("=").append(URLEncoder.encode(value));
						}
					} else {
						sb.append(name).append("=").append(URLEncoder.encode(value));
					}

				}
			}
		}
		sb.append(" ");
		sb.append(protocol).append("/").append(version);

		return sb.toString();
	}

}
