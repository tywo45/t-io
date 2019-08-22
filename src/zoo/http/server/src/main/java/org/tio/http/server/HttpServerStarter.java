package org.tio.http.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.TcpConst;
import org.tio.http.common.TioConfigKey;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpUuid;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.id.impl.UUIDSessionIdGenerator;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.utils.Threads;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.http.HttpUtils;
import org.tio.utils.hutool.FileUtil;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.json.Json;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

import okhttp3.Response;

/**
 *
 * @author tanyaowu
 */
public class HttpServerStarter {
	private static Logger			log						= LoggerFactory.getLogger(HttpServerStarter.class);
	private HttpConfig				httpConfig				= null;
	private HttpServerAioHandler	httpServerAioHandler	= null;
	private HttpServerAioListener	httpServerAioListener	= null;
	private ServerTioConfig		serverTioConfig		= null;
	private TioServer				tioServer				= null;
	private HttpRequestHandler		httpRequestHandler		= null;
	/**
	 * 预访问路径的后缀
	 */
	private List<String>			preAccessFileType		= new ArrayList<>();

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
		//		preAccessFileType.add("css");
		//		preAccessFileType.add("js");
		//		preAccessFileType.add("jsp");
		preAccessFileType.add("html");
		preAccessFileType.add("ftl");
		//		preAccessFileType.add("xml");
		//		preAccessFileType.add("htm");

		if (tioExecutor == null) {
			tioExecutor = Threads.getTioExecutor();
		}

		if (groupExecutor == null) {
			groupExecutor = Threads.getGroupExecutor();
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
	 * @return the serverTioConfig
	 */
	public ServerTioConfig getServerTioConfig() {
		return serverTioConfig;
	}

	private void init(HttpConfig httpConfig, HttpRequestHandler requestHandler, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		String system_timer_period = System.getProperty("tio.system.timer.period");
		if (StrUtil.isBlank(system_timer_period)) {
			System.setProperty("tio.system.timer.period", "50");
		}

		this.httpConfig = httpConfig;
		this.httpRequestHandler = requestHandler;
		httpConfig.setHttpRequestHandler(this.httpRequestHandler);
		this.httpServerAioHandler = new HttpServerAioHandler(httpConfig, requestHandler);
		httpServerAioListener = new HttpServerAioListener();
		String name = httpConfig.getName();
		if (StrUtil.isBlank(name)) {
			name = "Tio Http Server";
		}
		serverTioConfig = new ServerTioConfig(name, httpServerAioHandler, httpServerAioListener, tioExecutor, groupExecutor);
		serverTioConfig.setHeartbeatTimeout(1000 * 20);
		serverTioConfig.setShortConnection(true);
		serverTioConfig.setReadBufferSize(TcpConst.MAX_DATA_LENGTH);
		//		serverTioConfig.setAttribute(TioConfigKey.HTTP_SERVER_CONFIG, httpConfig);
		serverTioConfig.setAttribute(TioConfigKey.HTTP_REQ_HANDLER, this.httpRequestHandler);

		tioServer = new TioServer(serverTioConfig);

		HttpUuid imTioUuid = new HttpUuid();
		serverTioConfig.setTioUuid(imTioUuid);
	}

	public void setHttpRequestHandler(HttpRequestHandler requestHandler) {
		this.httpRequestHandler = requestHandler;
	}

	public void start() throws IOException {
		start(false);
	}

	/**
	 * @param preAccess
	 * @throws IOException
	 * @author tanyaowu
	 */
	public void start(boolean preAccess) throws IOException {
		if (httpConfig.isUseSession()) {
			if (httpConfig.getSessionStore() == null) {
				CaffeineCache caffeineCache = CaffeineCache.register(httpConfig.getSessionCacheName(), null, httpConfig.getSessionTimeout());
				httpConfig.setSessionStore(caffeineCache);
			}

			if (httpConfig.getSessionIdGenerator() == null) {
				httpConfig.setSessionIdGenerator(UUIDSessionIdGenerator.instance);
			}
		}

		tioServer.start(this.httpConfig.getBindIp(), this.httpConfig.getBindPort());

		if (preAccess) {
			preAccess();
		}
	}

	/**
	 * 预访问第一版功能先上，后面再优化
	 * 
	 * @author tanyaowu
	 */
	public void preAccess() {
		if (httpConfig.isPageInClasspath()) {
			log.info("暂时只支持目录形式的预访问");
			return;
		}

		String pageRoot = httpConfig.getPageRoot();
		if (pageRoot == null) {
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String, Long> pathCostMap = new TreeMap<>();

				long start = System.currentTimeMillis();
				preAccess(pageRoot, pathCostMap);
				long end = System.currentTimeMillis();
				long iv = end - start;

				Map<Long, Set<String>> costPathsMap = new TreeMap<>(new Comparator<Long>() {
					@Override
					public int compare(Long o1, Long o2) {
						//倒序排序
						return Long.compare(o2, o1);
					}
				});
				Set<Entry<String, Long>> entrySet = pathCostMap.entrySet();
				for (Entry<String, Long> entry : entrySet) {
					try {
						Long cost = entry.getValue();
						String path = entry.getKey();
						Set<String> pathSet = costPathsMap.get(cost);
						if (pathSet == null) {
							pathSet = new TreeSet<>();
							costPathsMap.put(cost, pathSet);
						}
						boolean added = pathSet.add(path);
						if (!added) {
							log.error("可能重复访问了:{}", path);
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}

				log.info("预访问了{}个path，耗时:{}ms，访问详情:\r\n{}\r\n耗时排序:\r\n{}", pathCostMap.size(), iv, Json.toFormatedJson(pathCostMap), Json.toFormatedJson(costPathsMap));
			}
		}).start();

	}

	/**
	 * 预访问第一版功能先上，后面再优化
	 * 
	 * @author tanyaowu
	 */
	private void preAccess(String rootpath, Map<String, Long> pathCostMap) {
		try {
			Map<String, String> headerMap = new HashMap<>();
			headerMap.put(HttpConst.RequestHeaderKey.Host, "127.0.0.1");

			String protocol = null;

			if (serverTioConfig.isSsl()) {
				protocol = "https";
			} else {
				protocol = "http";
			}
			String completePathPrefix = protocol + "://127.0.0.1:" + httpConfig.getBindPort();

			File rootDir = new File(rootpath);
			File[] files = rootDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File file) {
					//					String absolutePath = file.getAbsolutePath();
					String filename = file.getName();
					String extension = FileUtil.extName(filename);//.getExtension(filename);
					if (file.isDirectory()) {
						if ("svn-base".equalsIgnoreCase(extension)) {
							return false;
						}
						return true;
					}

					String ext = FileUtil.extName(file);
					if (preAccessFileType.contains(ext)) {
						return true;
					}
					return false;
				}
			});

			File pageRootFile = new File(httpConfig.getPageRoot());
			String pageRootAbs = pageRootFile.getCanonicalPath();
			for (File file : files) {
				try {
					if (file.isDirectory()) {
						preAccess(file.getCanonicalPath(), pathCostMap);
					} else {
						String absPath = file.getCanonicalPath();
						log.info("pageRoot:{}, 预访问路径getAbsolutePath:{}", httpConfig.getPageRoot(), absPath);
						long start = System.currentTimeMillis();
						String path = absPath.substring(pageRootAbs.length());

						if (!(path.startsWith("/") || path.startsWith("\\"))) {
							path = "/" + path;
						}
						log.info("预访问路径:{}", path);
						String url = completePathPrefix + path;
						Response response = HttpUtils.get(url, headerMap);
						long end = System.currentTimeMillis();
						long iv = end - start;
						pathCostMap.put(path, iv);
						log.info("预访问完成，耗时{}ms, [{}], {}", iv, path, response);
						response.close();
					}
				} catch (Exception e) {
					log.error(e.toString());
				}
			}
		} catch (Exception e) {
			log.error("预访问报错", e);
		}
	}

	public void stop() throws IOException {
		tioServer.stop();
	}

	public TioServer getTioServer() {
		return tioServer;
	}

}
