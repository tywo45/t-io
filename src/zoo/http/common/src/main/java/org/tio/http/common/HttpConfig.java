package org.tio.http.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.HttpSession;
import org.tio.http.common.session.id.ISessionIdGenerator;
import org.tio.http.common.session.limiter.SessionRateLimiter;
import org.tio.http.common.view.freemarker.FreemarkerConfig;
import org.tio.utils.cache.ICache;
import org.tio.utils.hutool.FileUtil;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 * 2017年8月15日 下午1:21:14
 */
public class HttpConfig {

	private static Logger log = LoggerFactory.getLogger(HttpConfig.class);

	/**
	 * 用于覆盖session cookie的参数名，客户端可以用这个传递sessionid
	 */
	public static final String TIO_HTTP_SESSIONID = "tio_http_sessionid";

	/**
	 * 存放HttpSession对象的cacheName
	 */
	public static final String SESSION_CACHE_NAME = "tio-h-s";

	/**
	 * 存放sessionId的cookie value
	 */
	public static final String SESSION_COOKIE_NAME = "PHPSESSID";

	/**
	 * session默认的超时时间，单位：秒
	 */
	public static final long DEFAULT_SESSION_TIMEOUT = 30 * 60;

	/**
	 * 默认的静态资源缓存时间，单位：秒
	 */
	public static final int MAX_LIVETIME_OF_STATICRES = 60 * 10;

	/**
	 * 文件上传时，boundary值的最大长度
	 */
	public static final int MAX_LENGTH_OF_BOUNDARY = 512;

	/**
	 * 文件上传时，头部的最大长度
	 */
	public static final int MAX_LENGTH_OF_MULTI_HEADER = 512;

	/**
	 * 文件上传时，体的最大长度(默认值2M)
	 */
	public static final int MAX_LENGTH_OF_MULTI_BODY = 1024 * 1024 * 2;

	/**
	 * 文件上传时，体的最大长度
	 */
	private int maxLengthOfMultiBody = MAX_LENGTH_OF_MULTI_BODY;

	/**
	 *POST体的最大长度默认值（2M）
	 */
	public static final int MAX_LENGTH_OF_POST_BODY = 1024 * 1024 * 2;

	/**
	 * POST体的最大长度
	 */
	private int maxLengthOfPostBody = MAX_LENGTH_OF_POST_BODY;

	public static final int MAX_FORWARD_COUNT = 10;

	/**
	 * 
	 */
	public int maxForwardCount = MAX_FORWARD_COUNT;

	/**
	 * 是否使用session
	 */
	private boolean useSession = true;

	/**
	 * 是否兼容1.0
	 * true：兼容
	 * false：不兼容
	 * 默认兼容
	 */
	public boolean compatible1_0 = true;

	/**
	 * 是否拼接http request header value
	 * 
	 */
	private boolean appendRequestHeaderString = false;

	private String bindIp = null;//"127.0.0.1";

	/**
	 * 监听端口
	 */
	private Integer bindPort = 80;

	private String serverInfo = HttpConst.SERVER_INFO;

	private String charset = HttpConst.CHARSET_NAME;

	private ICache sessionStore = null;

	public SessionRateLimiter sessionRateLimiter;

	/**
	 * 访问路径前缀，譬如"/api"
	 */
	private String contextPath = "";

	/**
	 * 加后缀，譬如".php"
	 */
	private String suffix = "";

	/**
	 * 如果访问路径是以"/"结束，则实际访问路径会自动加上welcomeFile，从而变成形如"/index.html"的路径
	 */
	private String welcomeFile = null;//"index.html";

	/**
	 * 允许访问的域名，如果不限制，则为null
	 */
	private String[] allowDomains = null;

	/**
	 * 存放HttpSession对象的cacheName
	 */
	private String sessionCacheName = SESSION_CACHE_NAME;

	/**
	 * session超时时间，单位：秒
	 */
	private long sessionTimeout = DEFAULT_SESSION_TIMEOUT;

	private String sessionCookieName = SESSION_COOKIE_NAME;

	/**
	 * 静态资源缓存时间，如果小于等于0则不缓存，单位：秒
	 */
	private int maxLiveTimeOfStaticRes = MAX_LIVETIME_OF_STATICRES;

	private String page404 = "/404.html";

	//	private HttpSessionManager httpSessionManager;

	private String page500 = "/500.html";

	private ISessionIdGenerator sessionIdGenerator;

	private HttpRequestHandler httpRequestHandler;

	/**
	 * ip被拉黑时，服务器给的响应，如果是null，服务器会直接断开连接
	 */
	private HttpResponse respForBlackIp = null;

	/**
	 * 是否被代理
	 */
	private boolean isProxied = false;

	/**
	 * 示例：
	 * 1、classpath中：page
	 * 2、绝对路径：/page
	 */
	private String pageRoot = null;//FileUtil.getAbsolutePath("page");//"/page";

	private boolean pageInClasspath = false;

	/**
	 * 临时支持freemarker，主要用于开发环境中的前端开发，暂时不重点作为tio-http-server功能<br>
	 * 请大家暂时不要使用该功能，因为api随时会变
	 */
	private FreemarkerConfig freemarkerConfig = null;

	/**
	 * 域名和页面根目录映射。当客户端通过不同域名访问时，其页面根目录是不一样的<br>
	 * key: www.t-io.org<br>
	 * value: 域名对应的页面根目录<br>
	 */
	private volatile Map<String, String> domainPageMap = null;//new HashMap<>();

	public boolean checkHost = true;

	//	/**
	//	 * @return the httpSessionManager
	//	 */
	//	public HttpSessionManager getHttpSessionManager() {
	//		return httpSessionManager;
	//	}
	//
	//	/**
	//	 * @param httpSessionManager the httpSessionManager to set
	//	 */
	//	public void setHttpSessionManager(HttpSessionManager httpSessionManager) {
	//		this.httpSessionManager = httpSessionManager;
	//	}

	public Map<String, String> getDomainPageMap() {
		return domainPageMap;
	}

	public HttpConfig(Integer bindPort, boolean useSession) {
		this.bindPort = bindPort;
		this.useSession = useSession;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	public HttpConfig(Integer bindPort, Long sessionTimeout, String contextPath, String suffix) {
		this.bindPort = bindPort;
		if (sessionTimeout != null) {
			this.sessionTimeout = sessionTimeout;
		}

		if (contextPath == null) {
			contextPath = "";
		}
		this.contextPath = contextPath;

		if (suffix == null) {
			suffix = "";
		}
		this.suffix = suffix;
	}

	/**
	 * @return the bindIp
	 */
	public String getBindIp() {
		return bindIp;
	}

	/**
	 * @return the bindPort
	 */
	public Integer getBindPort() {
		return bindPort;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @return the maxLiveTimeOfStaticRes
	 */
	public int getMaxLiveTimeOfStaticRes() {
		return maxLiveTimeOfStaticRes;
	}

	public String getPage404() {
		return page404;
	}

	public String getPage500() {
		return page500;
	}

	/**
	 * @return the pageRoot
	 */
	public String getPageRoot() {
		return pageRoot;
	}

	public String getPageRoot(HttpRequest request) {
		if (this.domainPageMap == null || domainPageMap.size() == 0) {
			return pageRoot;
		}

		String domain = request.getDomain();
		String root = domainPageMap.get(domain);
		if (root != null) {
			return root;
		}

		Set<Entry<String, String>> set = domainPageMap.entrySet();

		for (Entry<String, String> entry : set) {
			String d = entry.getKey();
			if (d.startsWith(".") && domain.endsWith(d)) {
				String file = entry.getValue();
				domainPageMap.put(domain, file);
				return file;
			}
		}
		domainPageMap.put(domain, pageRoot);
		return pageRoot;
	}

	/**
	 * 
	 * @param request
	 * @param path 形如 /xx/aa.html
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public HttpResource getResource(HttpRequest request, String path) throws Exception {
		String pageRoot = getPageRoot(request);
		HttpResource httpResource = null;
		//		File file = null;
		if (pageRoot != null) {
			if (StrUtil.endWith(path, "/")) {
				path = path + "index.html";
			}

			String complatePath = pageRoot + path;
			if (isPageInClasspath()) {
				URL url = this.getClass().getClassLoader().getResource(complatePath);
				if (url != null) {
					String protocol = url.getProtocol();
					if (Objects.equals(protocol, "jar")) {
						InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(complatePath);
						httpResource = new HttpResource(path, inputStream, null);
					} else {
						File file = new File(url.toURI());
						if (file.exists()) {
							httpResource = new HttpResource(path, null, file);
						}
					}
				}
			} else {
				File file = new File(complatePath);
				if (file.exists()) {
					httpResource = new HttpResource(path, null, file);
				}
			}
		}

		return httpResource;
	}

	/**
	 * @return the serverInfo
	 */
	public String getServerInfo() {
		return serverInfo;
	}

	/**
	 * @return the sessionCacheName
	 */
	public String getSessionCacheName() {
		return sessionCacheName;
	}

	public String getSessionCookieName() {
		return sessionCookieName;
	}

	//	public void setSessionTimeout(long sessionTimeout) {
	//		this.sessionTimeout = sessionTimeout;
	//	}

	public ISessionIdGenerator getSessionIdGenerator() {
		return sessionIdGenerator;
	}

	public ICache getSessionStore() {
		return sessionStore;
	}

	public long getSessionTimeout() {
		return sessionTimeout;
	}

	/**
	 * @param bindIp the bindIp to set
	 */
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	/**
	 * @param charset the charset to set
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @param maxLiveTimeOfStaticRes the maxLiveTimeOfStaticRes to set
	 */
	public void setMaxLiveTimeOfStaticRes(int maxLiveTimeOfStaticRes) {
		this.maxLiveTimeOfStaticRes = maxLiveTimeOfStaticRes;
	}

	public void setPage404(String page404) {
		this.page404 = page404;
	}

	public void setPage500(String page500) {
		this.page500 = page500;
	}

	/**
	 * 静态文件路径，必须先设置好pageRoot后再用
	 * key:  文件名后缀
	 * path: 访问路径  如 /user/set.html
	 */
	private Map<String, Set<String>> staticPathsMap = null;

	/**
	 * path: 访问路径  如 /user/set.html
	 */
	private Set<String> staticPaths = null;

	public Map<String, Set<String>> getStaticPathsMap() {
		initStaticPaths();
		return staticPathsMap;
	}

	public Set<String> getStaticPaths() {
		initStaticPaths();
		return staticPaths;
	}

	private Map<String, Set<String>> initStaticPaths() {
		if (staticPathsMap != null) {
			return staticPathsMap;
		}

		if (pageInClasspath) {
			throw new RuntimeException("classpath的pageRoot是");
		}

		staticPathsMap = new TreeMap<>();
		staticPaths = new TreeSet<>();

		List<File> files = FileUtil.loopFiles(pageRoot, new FileFilter() {
			@Override
			public boolean accept(File file) {
				String filename = file.getName();
				String ext = FileUtil.extName(filename);//.getExtension(filename);
				if (file.isDirectory()) {
					if ("svn-base".equalsIgnoreCase(ext)) {
						return false;
					}
					return true;
				}
				//				String ext = FileUtil.extName(file);
				return true;
			}
		});

		if (files == null) {
			return staticPathsMap;
		}

		log.info("一共{}个文件", files.size());

		File pageRootFile = new File(pageRoot);
		String pageRootAbs;
		try {
			pageRootAbs = pageRootFile.getCanonicalPath();
		} catch (IOException e1) {
			log.error(e1.toString(), e1);
			return null;
		}
		for (File file : files) {
			try {
				if (file.isDirectory()) {

				} else {
					String absPath = file.getCanonicalPath();
					//					long start = System.currentTimeMillis();
					String path = absPath.substring(pageRootAbs.length());

					path = path.replaceAll("\\\\", "/");

					if (!(path.startsWith("/"))) {
						path = "/" + path;
					}
					log.info("访问路径:{}", path);
					String ext = FileUtil.extName(path);
					Set<String> set = staticPathsMap.get(ext);
					if (set == null) {
						set = new TreeSet<>();
						staticPathsMap.put(ext, set);
					}
					set.add(path);
					staticPaths.add(path);

				}
			} catch (Exception e) {
				log.error(e.toString());
			}
		}
		//		log.error(Json.toFormatedJson(staticPaths));
		//		log.error(Json.toFormatedJson(staticPathsMap));
		return staticPathsMap;
	}

	/**
	 * 
	 * @param pageRoot 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @author tanyaowu
	 * @throws IOException 
	 */
	public void setPageRoot(String pageRoot) throws IOException {
		staticPathsMap = null;
		if (StrUtil.startWith(pageRoot, "classpath:")) {
			this.pageRoot = pageRoot.substring("classpath:".length());//.replaceFirst("classpath:", "classpath:/");
			if (this.pageRoot.startsWith("/")) {
				this.pageRoot = this.pageRoot.substring(1);
			}
			this.pageInClasspath = true;
		} else {
			this.pageRoot = pageRoot;//fromPath(pageRoot);

		}
	}

	/**
	 * 
	 * @param path 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @return
	 */
	//	public static File fromPath(String path) {
	//		if (path == null) {
	//			return null;
	//		}
	//
	//		if (StrUtil.startWithIgnoreCase(path, "classpath:")) {
	//			return new File(ResourceUtil.getAbsolutePath(path));
	//		} else {
	//			return new File(path);
	//		}
	//	}

	/**
	 * 
	 * @param domain 形如www.t-io.org的域名，也可以是形如.t-io.org这样的通配域名
	 * @param pageRoot 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @throws IOException 
	 */
	public void addDomainPage(String domain, String pageRoot) throws IOException {
		//		File pageRootFile = fromPath(pageRoot);
		//		if (!pageRootFile.exists()) {
		//			throw new IOException("文件【" + pageRoot + "】不存在");
		//		}
		//
		//		if (!pageRootFile.isDirectory()) {
		//			throw new IOException("文件【" + pageRoot + "】不是目录");
		//		}

		if (domainPageMap == null) {
			synchronized (this) {
				if (domainPageMap == null) {
					domainPageMap = new HashMap<>();
				}
			}
		}

		domainPageMap.put(domain, pageRoot);

		if (this.freemarkerConfig != null) {
			freemarkerConfig.addDomainConfiguration(domain, pageRoot);
		}
	}

	/**
	 * @param serverInfo the serverInfo to set
	 */
	public void setServerInfo(String serverInfo) {
		this.serverInfo = serverInfo;
	}

	/**
	 * @param sessionCacheName the sessionCacheName to set
	 */
	public void setSessionCacheName(String sessionCacheName) {
		this.sessionCacheName = sessionCacheName;
	}

	public void setSessionCookieName(String sessionCookieName) {
		this.sessionCookieName = sessionCookieName;
	}

	public void setSessionIdGenerator(ISessionIdGenerator sessionIdGenerator) {
		this.sessionIdGenerator = sessionIdGenerator;
	}

	public void setSessionStore(ICache sessionStore) {
		this.sessionStore = sessionStore;
		//		this.httpSessionManager = HttpSessionManager.getInstance(sessionStore);
	}

	/**
	 * @return the httpRequestHandler
	 */
	public HttpRequestHandler getHttpRequestHandler() {
		return httpRequestHandler;
	}

	/**
	 * @param httpRequestHandler the httpRequestHandler to set
	 */
	public void setHttpRequestHandler(HttpRequestHandler httpRequestHandler) {
		this.httpRequestHandler = httpRequestHandler;
	}

	public String getContextPath() {
		return contextPath;
	}

	public String getSuffix() {
		return suffix;
	}

	public String[] getAllowDomains() {
		return allowDomains;
	}

	public void setAllowDomains(String[] allowDomains) {
		this.allowDomains = allowDomains;
	}

	/**
	 * @return the isProxied
	 */
	public boolean isProxied() {
		return isProxied;
	}

	/**
	 * @param isProxied the isProxied to set
	 */
	public void setProxied(boolean isProxied) {
		this.isProxied = isProxied;
	}

	public boolean isUseSession() {
		return useSession;
	}

	public void setUseSession(boolean useSession) {
		this.useSession = useSession;
	}

	/**
	 * 根据sessionId获取HttpSession对象
	 * @param sessionId
	 * @return
	 */
	public HttpSession getHttpSession(String sessionId) {
		if (StrUtil.isBlank(sessionId)) {
			return null;
		}
		HttpSession httpSession = (HttpSession) getSessionStore().get(sessionId);
		return httpSession;
	}

	public String getWelcomeFile() {
		return welcomeFile;
	}

	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}

	public FreemarkerConfig getFreemarkerConfig() {
		return freemarkerConfig;
	}

	public void setFreemarkerConfig(FreemarkerConfig freemarkerConfig) {
		this.freemarkerConfig = freemarkerConfig;
	}

	/**
	 * @return the appendRequestHeaderString
	 */
	public boolean isAppendRequestHeaderString() {
		return appendRequestHeaderString;
	}

	/**
	 * @param appendRequestHeaderString the appendRequestHeaderString to set
	 */
	public void setAppendRequestHeaderString(boolean appendRequestHeaderString) {
		this.appendRequestHeaderString = appendRequestHeaderString;
	}

	/**
	 * @return the maxLengthOfMultiBody
	 */
	public int getMaxLengthOfMultiBody() {
		return maxLengthOfMultiBody;
	}

	/**
	 * @param maxLengthOfMultiBody the maxLengthOfMultiBody to set
	 */
	public void setMaxLengthOfMultiBody(int maxLengthOfMultiBody) {
		this.maxLengthOfMultiBody = maxLengthOfMultiBody;
	}

	public HttpResponse getRespForBlackIp() {
		return respForBlackIp;
	}

	public void setRespForBlackIp(HttpResponse respForBlackIp) {
		this.respForBlackIp = respForBlackIp;
	}

	/**
	 * @param checkHost
	 * @author tanyaowu
	 */
	public void setCheckHost(boolean checkHost) {
		this.checkHost = checkHost;
	}

	public void setCompatible1_0(boolean compatible1_0) {
		this.compatible1_0 = compatible1_0;
	}

	public boolean isPageInClasspath() {
		return pageInClasspath;
	}

	public void setPageInClasspath(boolean pageInClasspath) {
		this.pageInClasspath = pageInClasspath;
	}

	public SessionRateLimiter getSessionRateLimiter() {
		return sessionRateLimiter;
	}

	public void setSessionRateLimiter(SessionRateLimiter sessionRateLimiter) {
		this.sessionRateLimiter = sessionRateLimiter;
	}

	public int getMaxForwardCount() {
		return maxForwardCount;
	}

	public void setMaxForwardCount(int maxForwardCount) {
		this.maxForwardCount = maxForwardCount;
	}

	public int getMaxLengthOfPostBody() {
		return maxLengthOfPostBody;
	}

	public void setMaxLengthOfPostBody(int maxLengthOfPostBody) {
		this.maxLengthOfPostBody = maxLengthOfPostBody;
	}
}
