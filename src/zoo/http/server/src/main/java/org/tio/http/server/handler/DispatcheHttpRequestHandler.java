package org.tio.http.server.handler;

import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;

/**
 * @author tanyaowu
 *
 */
public abstract class DispatcheHttpRequestHandler implements HttpRequestHandler {
	public DispatcheHttpRequestHandler() {
	}

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		HttpRequestHandler httpRequestHandler = _getHttpRequestHandler(request);
		return httpRequestHandler.handler(request);
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) throws Exception {
		HttpRequestHandler httpRequestHandler = _getHttpRequestHandler(request);
		return httpRequestHandler.resp404(request, requestLine);
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) throws Exception {
		HttpRequestHandler httpRequestHandler = _getHttpRequestHandler(request);
		return httpRequestHandler.resp500(request, requestLine, throwable);
	}

	//	@Override
	//	public void clearStaticResCache() {
	//		HttpRequestHandler httpRequestHandler = _getHttpRequestHandler();
	//		httpRequestHandler.clearStaticResCache();
	//	}

	private HttpRequestHandler _getHttpRequestHandler(HttpRequest request) {
		HttpRequestHandler httpRequestHandler = getHttpRequestHandler(request);
		//		request.setHttpConfig(httpRequestHandler.getHttpConfig(request));
		return httpRequestHandler;
	}

	@Override
	public HttpConfig getHttpConfig(HttpRequest request) {
		HttpRequestHandler httpRequestHandler = getHttpRequestHandler(request);
		return httpRequestHandler.getHttpConfig(request);
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	public abstract HttpRequestHandler getHttpRequestHandler(HttpRequest request);

}
