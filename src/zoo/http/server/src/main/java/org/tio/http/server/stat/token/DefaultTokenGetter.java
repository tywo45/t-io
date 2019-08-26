/**
 * 
 */
package org.tio.http.server.stat.token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpRequest;
import org.tio.http.server.handler.DefaultHttpRequestHandler;

/**
 * @author tanyaowu
 */
public class DefaultTokenGetter implements TokenGetter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(DefaultTokenGetter.class);

	public static DefaultTokenGetter me = new DefaultTokenGetter();

	/**
	 * 
	 */
	protected DefaultTokenGetter() {
	}

	@Override
	public String getToken(HttpRequest request) {
		//		HttpSession httpSession = request.getHttpSession();
		//		if (httpSession != null) {
		//			return httpSession.getId();
		//		}
		//		Cookie cookie = DefaultHttpRequestHandler.getSessionCookie(request, request.httpConfig);
		//		if (cookie != null) {
		//			log.error("token from cookie: {}", cookie.getValue());
		//			return cookie.getValue();
		//		}
		return DefaultHttpRequestHandler.getSessionId(request);
	}

}
