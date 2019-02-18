package org.tio.http.client;

import org.tio.core.Node;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.Method;
import org.tio.http.common.RequestLine;

/**
 * 临时写的httpclient，用于性能测试
 * @author tanyaowu
 *
 */
public class ClientHttpRequest extends HttpRequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1997414964490639641L;

	public ClientHttpRequest(Node remote) {
		super(remote);
	}

	public static ClientHttpRequest get(String path, String queryString) {
		return new ClientHttpRequest(Method.GET, path, queryString);
	}

	public ClientHttpRequest(Method method, String path, String queryString) {
		super();
		RequestLine requestLine = new RequestLine();
		requestLine.setMethod(method);
		requestLine.setPath(path);
		requestLine.setQueryString(queryString);
		requestLine.setProtocol("HTTP");
		requestLine.setVersion("1.1");
		this.setRequestLine(requestLine);
	}

}
