package org.tio.http.common.handler;

import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;

/**
 * http请求处理者
 * @author tanyaowu 
 * 2017年8月30日 上午9:22:50
 */
public interface HttpRequestHandler {
	/**
	 * 处理请求
	 * @param packet
	 * @return 可以为null
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse handler(HttpRequest packet) throws Exception;

	/**
	 * 响应404
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 * @throws Exception 
	 */
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) throws Exception;

	/**
	 * 响应500
	 * @param request
	 * @param requestLine
	 * @param throwable
	 * @return
	 * @author tanyaowu
	 * @throws Exception
	 */
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, java.lang.Throwable throwable) throws Exception;

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public HttpConfig getHttpConfig(HttpRequest request);

	/**
	 * 清空静态资源缓存，如果没有缓存，可以不处理
	 * @param request
	 * @author: tanyaowu
	 */
	public void clearStaticResCache();
}
