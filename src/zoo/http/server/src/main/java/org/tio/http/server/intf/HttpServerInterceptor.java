package org.tio.http.server.intf;

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;

/**
 * @author tanyaowu
 * 2017年7月25日 下午2:16:06
 */
public interface HttpServerInterceptor {

	/**
	 * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler()前会先调用这个方法<br>
	 * 如果返回了HttpResponse对象，则后续都不再执行，表示调用栈就此结束<br>
	 * @param request
	 * @param requestLine
	 * @param channelContext
	 * @param responseFromCache 从缓存中获取到的HttpResponse对象
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResponse doBeforeHandler(HttpRequest request, RequestLine requestLine, HttpResponse responseFromCache) throws Exception;

	/**
	 * 在执行org.tio.http.server.handler.IHttpRequestHandler.handler()后会调用此方法，业务层可以统一在这里给HttpResponse作一些修饰
	 * @param request
	 * @param requestLine
	 * @param response
	 * @param cost 本次请求耗时，单位：毫秒
	 * @throws Exception
	 */
	public void doAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse response, long cost) throws Exception;
}
