/**
 * 
 */
package org.tio.http.server.intf;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;

/**
 * @author tanyaowu
 */
public interface ThrowableHandler {

	/**
	 * 
	 * @param request
	 * @param requestLine
	 * @param throwable
	 * @return
	 * @throws Exception 
	 */
	public HttpResponse handler(HttpRequest request, RequestLine requestLine, Throwable throwable) throws Exception;
}
