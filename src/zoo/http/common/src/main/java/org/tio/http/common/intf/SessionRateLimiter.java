package org.tio.http.common.intf;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * session限流接口
 * @author tanyaowu
 *
 */
public interface SessionRateLimiter {

	/**
	 * 当前这样的请求，每隔多久允许访问一次，单位：毫秒
	 * @param request
	 * @return 如果返回null则表示不限流
	 * @author tanyaowu
	 */
	public Integer interval(HttpRequest request);

	/**
	 * 当被限流后，返回给用户的HttpResponse，可以返回null
	 * @param request
	 * @return
	 * @author tanyaowu
	 * @param lastAccessTime 
	 */
	public HttpResponse response(HttpRequest request, Long lastAccessTime);

}