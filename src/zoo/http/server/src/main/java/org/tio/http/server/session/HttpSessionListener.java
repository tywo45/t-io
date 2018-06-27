package org.tio.http.server.session;

import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.session.HttpSession;

/**
 * @author tanyaowu 
 * 2017年9月27日 下午1:46:20
 */
public interface HttpSessionListener {
	/**
	 * 
	 * @param request
	 * @param session
	 * @param httpConfig
	 */
	public void doAfterCreated(HttpRequest request, HttpSession session, HttpConfig httpConfig);

}
