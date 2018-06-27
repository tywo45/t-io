package org.tio.http.server;

import java.io.IOException;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.GroupContextKey;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpUuid;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.http.server.util.Threads;
import org.tio.server.TioServer;
import org.tio.server.ServerGroupContext;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 */
public class HttpServerStarter {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(HttpServerStarter.class);

	private HttpConfig httpConfig = null;

	private HttpServerAioHandler httpServerAioHandler = null;

	private HttpServerAioListener httpServerAioListener = null;

	private ServerGroupContext serverGroupContext = null;

	private TioServer tioServer = null;

	private HttpRequestHandler httpRequestHandler;
	
	

	/**
	 * 
	 * @param httpConfig
	 * @param requestHandler
	 * @author tanyaowu
	 */
	public HttpServerStarter(HttpConfig httpConfig, HttpRequestHandler requestHandler) {
		this(httpConfig, requestHandler, null, null);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param requestHandler
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author tanyaowu
	 */
	public HttpServerStarter(HttpConfig httpConfig, HttpRequestHandler requestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		if (tioExecutor == null) {
			tioExecutor = Threads.tioExecutor;
		}
		
		if (groupExecutor == null) {
			groupExecutor = Threads.groupExecutor;
		}
		
		init(httpConfig, requestHandler, tioExecutor, groupExecutor);
	}

//	/**
//	 * @deprecated
//	 * @param pageRoot 如果为null，则不提供静态资源服务
//	 * @param serverPort
//	 * @param contextPath
//	 * @param scanPackages
//	 * @param httpServerInterceptor
//	 * @author tanyaowu
//	 * @throws IOException 
//	 */
//	public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor) throws IOException {
//		this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, null, null, null);
//	}
//
//	/**
//	 * @deprecated
//	 * @param pageRoot 如果为null，则不提供静态资源服务
//	 * @param serverPort
//	 * @param contextPath
//	 * @param scanPackages
//	 * @param httpServerInterceptor
//	 * @param sessionStore
//	 * @author tanyaowu
//	 * @throws IOException 
//	 */
//	public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, ICache sessionStore) throws IOException {
//		this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, sessionStore, null, null);
//	}
//
//	/**
//	 * @deprecated
//	 * pageRoot 如果为null，则不提供静态资源服务
//	 * @param pageRoot
//	 * @param serverPort
//	 * @param contextPath
//	 * @param scanPackages
//	 * @param httpServerInterceptor
//	 * @param sessionStore
//	 * @param tioExecutor
//	 * @param groupExecutor
//	 * @author tanyaowu
//	 * @throws IOException 
//	 */
//	public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, ICache sessionStore,
//			SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
//		this(pageRoot, serverPort, contextPath, scanPackages, httpServerInterceptor, null, sessionStore, tioExecutor, groupExecutor);
//	}
//	
//	/**
//	 * @deprecated
//	 * pageRoot 如果为null，则不提供静态资源服务
//	 * @param pageRoot
//	 * @param serverPort
//	 * @param contextPath
//	 * @param scanPackages
//	 * @param httpServerInterceptor
//	 * @param httpSessionListener
//	 * @param sessionStore
//	 * @param tioExecutor
//	 * @param groupExecutor
//	 * @author tanyaowu
//	 * @throws IOException 
//	 */
//	public HttpServerStarter(String pageRoot, int serverPort, String contextPath, String[] scanPackages, HttpServerInterceptor httpServerInterceptor, HttpSessionListener httpSessionListener, ICache sessionStore,
//			SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) throws IOException {
//		int port = serverPort;
//
//		httpConfig = new HttpConfig(port, null, contextPath, null);
//		httpConfig.setPageRoot(pageRoot);
//		if (sessionStore != null) {
//			httpConfig.setSessionStore(sessionStore);
//		}
//
//		Routes routes = new Routes(scanPackages);
//		DefaultHttpRequestHandler requestHandler = new DefaultHttpRequestHandler(httpConfig, routes);
//		requestHandler.setHttpServerInterceptor(httpServerInterceptor);
//		requestHandler.setHttpSessionListener(httpSessionListener);
//
//		
//		
//		init(httpConfig, requestHandler, tioExecutor, groupExecutor);
//	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public HttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	/**
	 * @return the httpServerAioHandler
	 */
	public HttpServerAioHandler getHttpServerAioHandler() {
		return httpServerAioHandler;
	}

	/**
	 * @return the httpServerAioListener
	 */
	public HttpServerAioListener getHttpServerAioListener() {
		return httpServerAioListener;
	}

	/**
	 * @return the serverGroupContext
	 */
	public ServerGroupContext getServerGroupContext() {
		return serverGroupContext;
	}

	private void init(HttpConfig httpConfig, HttpRequestHandler requestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this.httpConfig = httpConfig;
		this.httpRequestHandler = requestHandler;
		httpConfig.setHttpRequestHandler(this.httpRequestHandler);
		this.httpServerAioHandler = new HttpServerAioHandler(httpConfig, requestHandler);
		httpServerAioListener = new HttpServerAioListener();
		serverGroupContext = new ServerGroupContext("Tio Http Server", httpServerAioHandler, httpServerAioListener, tioExecutor, groupExecutor);
		serverGroupContext.setHeartbeatTimeout(1000 * 20);
		serverGroupContext.setShortConnection(true);
		serverGroupContext.setAttribute(GroupContextKey.HTTP_SERVER_CONFIG, httpConfig);

		tioServer = new TioServer(serverGroupContext);

		HttpUuid imTioUuid = new HttpUuid();
		serverGroupContext.setTioUuid(imTioUuid);
	}

	public void setHttpRequestHandler(HttpRequestHandler requestHandler) {
		this.httpRequestHandler = requestHandler;
	}

	public void start() throws IOException {
		if (httpConfig.getSessionStore() == null) {
			CaffeineCache caffeineCache = CaffeineCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
			httpConfig.setSessionStore(caffeineCache);
		}

//		if (httpConfig.getPageRoot() == null) {
//			httpConfig.setPageRoot("page");
//		}

		if (httpConfig.getSessionIdGenerator() == null) {
			httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
		}

		tioServer.start(this.httpConfig.getBindIp(), this.httpConfig.getBindPort());
	}

	public void stop() throws IOException {
		tioServer.stop();
	}
}
