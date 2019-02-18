package org.tio.http.common.session.limiter;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * session限流接口
 * @author tanyaowu
 *
 */
public interface SessionRateLimiter {

	/**
	 * 是否允许访问，true：允许访问，false：不允许访问
	 * @param request
	 * @param sessionRateVo
	 * @return
	 * @author tanyaowu
	 */
	public boolean allow(HttpRequest request, SessionRateVo sessionRateVo);

	/**
	 * 当被限流后，返回给用户的HttpResponse
	 * 如果返回null，则会断开连接
	 * @param request
	 * @param sessionRateVo
	 * @return
	 * @author tanyaowu
	 */
	public HttpResponse response(HttpRequest request, SessionRateVo sessionRateVo);

}