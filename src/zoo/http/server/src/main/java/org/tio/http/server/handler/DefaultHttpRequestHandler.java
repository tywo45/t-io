package org.tio.http.server.handler;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.Cookie;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.HttpSession;
import org.tio.http.common.utils.HttpGzipUtils;
import org.tio.http.common.view.freemarker.FreemarkerConfig;
import org.tio.http.server.intf.CurrUseridGetter;
import org.tio.http.server.intf.HttpServerInterceptor;
import org.tio.http.server.intf.ThrowableHandler;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.session.HttpSessionListener;
import org.tio.http.server.session.SessionCookieDecorator;
import org.tio.http.server.stat.StatPathFilter;
import org.tio.http.server.stat.ip.path.IpAccessStat;
import org.tio.http.server.stat.ip.path.IpPathAccessStat;
import org.tio.http.server.stat.ip.path.IpPathAccessStatListener;
import org.tio.http.server.stat.ip.path.IpPathAccessStats;
import org.tio.http.server.stat.token.TokenAccessStat;
import org.tio.http.server.stat.token.TokenPathAccessStat;
import org.tio.http.server.stat.token.TokenPathAccessStatListener;
import org.tio.http.server.stat.token.TokenPathAccessStats;
import org.tio.http.server.util.ClassUtils;
import org.tio.http.server.util.Resps;
import org.tio.server.ServerChannelContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.freemarker.FreemarkerUtils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import jodd.io.FileNameUtil;

/**
 *
 * @author tanyaowu
 *
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

	//	/**
	//	 * 静态资源的CacheName
	//	 * key:   path 譬如"/index.html"
	//	 * value: HttpResponse
	//	 */
	//	private static final String STATIC_RES_CACHENAME = "TIO_HTTP_STATIC_RES";

	/**
	 * 静态资源的CacheName
	 * key:   path 譬如"/index.html"
	 * value: FileCache
	 */
	private static final String STATIC_RES_CONTENT_CACHENAME = "TIO_HTTP_STATIC_RES_CONTENT";

	/**
	 * @param args
	 *
	 * @author tanyaowu
	 * 2016年11月18日 上午9:13:15
	 *
	 */
	public static void main(String[] args) {
	}

	protected HttpConfig httpConfig;

	protected Routes routes = null;

	//	private LoadingCache<String, HttpSession> loadingCache = null;

	private HttpServerInterceptor httpServerInterceptor;

	private HttpSessionListener httpSessionListener;

	private ThrowableHandler throwableHandler;

	private SessionCookieDecorator sessionCookieDecorator;

	private IpPathAccessStats ipPathAccessStats;

	private TokenPathAccessStats tokenPathAccessStats;

	private CaffeineCache staticResCache;

	private String contextPath;
	private int contextPathLength = 0;
	private String suffix;
	private int suffixLength = 0;

	

	//	private static String randomCookieValue() {
	//		return RandomUtil.randomUUID();
	//	}

	/**
	 *
	 * @param httpConfig
	 * @param routes
	 * @author tanyaowu
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Routes routes) {
		if (httpConfig == null) {
			throw new RuntimeException("httpConfig can not be null");
		}
		this.contextPath = httpConfig.getContextPath();
		this.suffix = httpConfig.getSuffix();

		if (StringUtils.isNotBlank(contextPath)) {
			contextPathLength = contextPath.length();
		}
		if (StringUtils.isNotBlank(suffix)) {
			suffixLength = suffix.length();
		}

		this.httpConfig = httpConfig;

		if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
			staticResCache = CaffeineCache.register(STATIC_RES_CONTENT_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
		}

		this.routes = routes;
	}

	/**
	 * 创建httpsession
	 * @return
	 * @author tanyaowu
	 */
	private HttpSession createSession(HttpRequest request) {
		String sessionId = httpConfig.getSessionIdGenerator().sessionId(httpConfig, request);
		HttpSession httpSession = new HttpSession(sessionId);
		if (httpSessionListener != null) {
			httpSessionListener.doAfterCreated(request, httpSession, httpConfig);
		}
		return httpSession;
	}

	/**
	 * @return the httpConfig
	 */
	public HttpConfig getHttpConfig() {
		return httpConfig;
	}

	public HttpServerInterceptor getHttpServerInterceptor() {
		return httpServerInterceptor;
	}

	public static Cookie getSessionCookie(HttpRequest request, HttpConfig httpConfig) {
		Cookie sessionCookie = request.getCookie(httpConfig.getSessionCookieName());
		return sessionCookie;
	}

	/**
	 * @return the staticResCache
	 */
	public CaffeineCache getStaticResCache() {
		return staticResCache;
	}

	/**
	 * 检查域名是否可以访问本站
	 * @param request
	 * @return
	 * @author tanyaowu
	 */
	private boolean checkDomain(HttpRequest request) {
		String[] allowDomains = httpConfig.getAllowDomains();
		if (allowDomains == null || allowDomains.length == 0) {
			return true;
		}
		String host = request.getHost();
		if (ArrayUtil.contains(allowDomains, host)) {
			return true;
		}
		return false;
	}

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		if (!checkDomain(request)) {
			Tio.remove(request.getChannelContext(), "过来的域名[" + request.getDomain() + "]不对");
			return null;
		}
		
		long start = SystemTimer.currentTimeMillis();
		
		HttpResponse response = null;
		RequestLine requestLine = request.getRequestLine();
		String path = requestLine.getPath();

		if (StringUtils.isNotBlank(contextPath)) {
			if (StringUtils.startsWith(path, contextPath)) {
				path = StringUtils.substring(path, contextPathLength);
			} else {
				//				Tio.remove(request.getChannelContext(), "请求路径不合法，必须以" + contextPath + "开头：" + requestLine.getLine());
				//				return null;
			}
		}

		if (StringUtils.isNotBlank(suffix)) {
			if (StringUtils.endsWith(path, suffix)) {
				path = StringUtils.substring(path, 0, path.length() - suffixLength);
			} else {
				//				Tio.remove(request.getChannelContext(), "请求路径不合法，必须以" + suffix + "结尾：" + requestLine.getLine());
				//				return null;
			}
		}
		
		if (StringUtils.isNotBlank(httpConfig.getWelcomeFile())) {
			if (StringUtils.endsWith(path, "/")) {
				path = path + httpConfig.getWelcomeFile();
			}
		}
		requestLine.setPath(path);

		try {
			processCookieBeforeHandler(request, requestLine);
			if (httpServerInterceptor != null) {
				response = httpServerInterceptor.doBeforeHandler(request, requestLine, response);
				if (response != null) {
					return response;
				}
			}
			requestLine = request.getRequestLine();
			path = requestLine.getPath();

			Method method = null;
			if (routes != null) {
				method = routes.getMethodByPath(path, request);
			}

			if (method != null) {
				String[] paramnames = routes.methodParamnameMap.get(method);
				Class<?>[] parameterTypes = routes.methodParamtypeMap.get(method);//method.getParameterTypes();
				Object bean = routes.methodBeanMap.get(method);
				Object obj = null;
				if (parameterTypes == null || parameterTypes.length == 0) {
					//obj = method.invoke(bean);
					
					obj = Routes.beanMethodaccessMap.get(bean).invoke(bean, method.getName(), parameterTypes, (Object)null);
				} else {
					//赋值这段代码待重构，先用上
					Object[] paramValues = new Object[parameterTypes.length];
					int i = 0;
					for (Class<?> paramType : parameterTypes) {
						try {
							if (paramType == HttpRequest.class) {
								paramValues[i] = request;
							} else if (paramType == HttpSession.class) {
								paramValues[i] = request.getHttpSession();
							} else if (paramType == HttpConfig.class) {
								paramValues[i] = httpConfig;
							} else if (paramType == ServerChannelContext.class) {  //paramType.isAssignableFrom(ServerChannelContext.class)
								paramValues[i] = request.getChannelContext();
							} else {
								Map<String, Object[]> params = request.getParams();
								if (params != null) {
									if (ClassUtils.isSimpleTypeOrArray(paramType)) {
										//										paramValues[i] = Ognl.getValue(paramnames[i], (Object) params, paramType);
										Object[] value = params.get(paramnames[i]);
										if (value != null && value.length > 0) {
											if (paramType.isArray()) {
												paramValues[i] = Convert.convert(paramType, value);
											} else {
												paramValues[i] = Convert.convert(paramType, value[0]);
											}
										}
									} else {
										paramValues[i] = paramType.newInstance();//BeanUtil.mapToBean(params, paramType, true);
										Set<Entry<String, Object[]>> set = params.entrySet();
										label2: for (Entry<String, Object[]> entry : set) {
											String fieldName = entry.getKey();
											Object[] fieldValue = entry.getValue();

											PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(paramType, fieldName, true);
											if (propertyDescriptor == null) {
												continue label2;
											} else {
												Method writeMethod = propertyDescriptor.getWriteMethod();
												if (writeMethod == null) {
													continue label2;
												}
												writeMethod = ClassUtil.setAccessible(writeMethod);
												Class<?>[] clazzes = writeMethod.getParameterTypes();
												if (clazzes == null || clazzes.length != 1) {
													log.info("方法的参数长度不为1，{}.{}", paramType.getName(), writeMethod.getName());
													continue label2;
												}
												Class<?> clazz = clazzes[0];

												if (ClassUtils.isSimpleTypeOrArray(clazz)) {
													if (fieldValue != null && fieldValue.length > 0) {
														if (clazz.isArray()) {
															Object theValue = Convert.convert(clazz, fieldValue);
															writeMethod.invoke(paramValues[i], theValue);
														} else {
															Object theValue = Convert.convert(clazz, fieldValue[0]);
															writeMethod.invoke(paramValues[i], theValue);
														}
													}
												}
											}
										}
									}
								}
							}
						} catch (Throwable e) {
							log.error(request.toString(), e);
						} finally {
							i++;
						}
					}
//					obj = method.invoke(bean, paramValues);
					obj = Routes.beanMethodaccessMap.get(bean).invoke(bean, method.getName(), parameterTypes, paramValues);
				}

				if (obj instanceof HttpResponse) {
					response = (HttpResponse) obj;
					return response;
				} else {
					if (obj == null) {
						if (method.getReturnType() == HttpResponse.class) {
							return null;
						} else {
							response = Resps.json(request, obj);
						}
					} else {
						response = Resps.json(request, obj);
					}
					return response;
				}
			} else {
				FileCache fileCache = null;
				File file = null;
				if (staticResCache != null) {
					//					contentCache = CaffeineCache.getCache(STATIC_RES_CONTENT_CACHENAME);
					fileCache = (FileCache) staticResCache.get(path);
				}
				if (fileCache != null) {
					//					byte[] bodyBytes = fileCache.getData();
					//					Map<String, String> headers = fileCache.getHeaders();

					//					HttpResponse responseInCache = fileCache.getResponse();

					long lastModified = fileCache.getLastModified();

					response = Resps.try304(request, lastModified);
					if (response != null) {
						response.addHeader(HttpConst.ResponseHeaderKey.tio_from_cache, "true");
						return response;
					}

//					response = fileCache.cloneResponse(request);
					response = fileCache.getResponse();
					response = HttpResponse.cloneResponse(request, response);
					
					//					log.info("{}, 从缓存获取, 大小: {}", path, response.getBody().length);

					//					response = new HttpResponse(request, httpConfig);
					//					response.setBody(bodyBytes, request);
					//					response.addHeaders(headers);
					return response;
				} else {
					File pageRoot = httpConfig.getPageRoot(request);
					if (pageRoot != null) {
						//						String root = FileUtil.getAbsolutePath(pageRoot);
						file = new File(pageRoot + path);
						if (!file.exists() || file.isDirectory()) {
							if (StringUtils.endsWith(path, "/")) {
								path = path + "index.html";
							} else {
								path = path + "/index.html";
							}
							file = new File(pageRoot, path);
						}

						if (file.exists()) {
							//项目中需要，时间支持一下freemarker模板，后面要做模板支持抽象设计
							FreemarkerConfig freemarkerConfig = httpConfig.getFreemarkerConfig();
							if (freemarkerConfig != null) {
								String extension = FileNameUtil.getExtension(file.getName());
								if (ArrayUtil.contains(freemarkerConfig.getSuffixes(), extension)) {
									Configuration configuration = freemarkerConfig.getConfiguration(request);
									if (configuration != null) {
										Object model = freemarkerConfig.getModelGenerator().generate(request);
										if (request.isClosed()) {
											return null;
										} else {
											TemplateLoader templateLoader = configuration.getTemplateLoader();//FileTemplateLoader
											if (templateLoader instanceof FileTemplateLoader) {
												try {
													String filePath = file.getCanonicalPath();
													String pageRootPath = httpConfig.getPageRoot(request).getCanonicalPath();
													String template = StringUtils.substring(filePath, pageRootPath.length());
													String retStr = FreemarkerUtils.generateStringByFile(template, configuration, model);
													response = Resps.bytes(request, retStr.getBytes(configuration.getDefaultEncoding()), extension);
													return response;
												} catch (java.lang.Throwable e) {
													//freemarker编译异常的全部走普通view
													log.error(file.getCanonicalPath() + ", " + e.toString(), e);
												}
											}
										}
									}
								}
							}

							response = Resps.file(request, file);
							response.setStaticRes(true);

							//把静态资源放入缓存
							if (response.isStaticRes() && staticResCache != null/** && request.getIsSupportGzip()*/
							) {
								if (response.getBody() != null && response.getStatus() == HttpResponseStatus.C200) {
									String contentType = response.getHeader(HttpConst.ResponseHeaderKey.Content_Type);
									String contentEncoding = response.getHeader(HttpConst.ResponseHeaderKey.Content_Encoding);
									String lastModified = response.getLastModified();//.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);

									Map<String, String> headers = new HashMap<>();
									if (StringUtils.isNotBlank(contentType)) {
										headers.put(HttpConst.ResponseHeaderKey.Content_Type, contentType);
									}
									if (StringUtils.isNotBlank(contentEncoding)) {
										headers.put(HttpConst.ResponseHeaderKey.Content_Encoding, contentEncoding);
									}
//									if (StringUtils.isNotBlank(lastModified)) {
//										headers.put(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
//									}
									//									headers.put(HttpConst.ResponseHeaderKey.tio_from_cache, "true");

									HttpResponse responseInCache = new HttpResponse(request);
									responseInCache.addHeaders(headers);
									if (StringUtils.isNotBlank(lastModified)) {
										responseInCache.setLastModified(lastModified);
									}
									responseInCache.setBody(response.getBody());
									responseInCache.setHasGzipped(response.isHasGzipped());

									fileCache = new FileCache(responseInCache, file.lastModified());
									staticResCache.put(path, fileCache);
									log.info("放入缓存:[{}], {}", path, response.getBody().length);
								}
							}

							return response;
						}
					}
				}
			}

			response = resp404(request, requestLine);//Resps.html(request, "404--并没有找到你想要的内容", httpConfig.getCharset());
			return response;
		} catch (Throwable e) {
			logError(request, requestLine, e);
			response = resp500(request, requestLine, e);//Resps.html(request, "500--服务器出了点故障", httpConfig.getCharset());
			return response;
		} finally {
			long time = SystemTimer.currentTimeMillis();
			long iv = time - start; //本次请求消耗的时间，单位：毫秒
			try {
				processCookieAfterHandler(request, requestLine, response);
			} catch (Throwable e) {
				logError(request, requestLine, e);
			} finally {
				if (httpServerInterceptor != null) {
					try {
						httpServerInterceptor.doAfterHandler(request, requestLine, response, iv);
					} catch (Exception e) {
						log.error(e.toString(), e);
					}
				}
				try {
					HttpGzipUtils.gzip(request, response);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
				boolean f = statIpPath(request, response, path, iv);
				if (!f) {
					return null;
				}

				f = statTokenPath(request, response, path, iv);
				if (!f) {
					return null;
				}
			}
		}
	}

	/**
	 * ipPathAccessStat and ipAccessStat
	 * @param request
	 * @param response
	 * @param path
	 * @param iv
	 * @return
	 */
	private boolean statIpPath(HttpRequest request, HttpResponse response, String path, long iv) {
		if (ipPathAccessStats == null) {
			return true;
		}
		
		if (response == null) {
			return false;
		}
		
		if (response.isSkipIpStat() || request.isClosed()) {
			return true;
		}
		
		//统计一下IP访问数据
		String ip = request.getClientIp();//IpUtils.getRealIp(request);

		Cookie cookie = getSessionCookie(request, httpConfig);

		StatPathFilter statPathFilter = ipPathAccessStats.getStatPathFilter();

		//添加统计
		for (Long duration : ipPathAccessStats.durationList) {
			IpAccessStat ipAccessStat = ipPathAccessStats.get(duration, ip);//.get(duration, ip, path);//.get(v, channelContext.getClientNode().getIp());

			ipAccessStat.count.incrementAndGet();
			ipAccessStat.timeCost.addAndGet(iv);
			ipAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());
			if (cookie == null) {
				ipAccessStat.noSessionCount.incrementAndGet();
			} else {
				ipAccessStat.sessionIds.add(cookie.getValue());
			}

			if (statPathFilter.filter(path, request, response)) {
				IpPathAccessStat ipPathAccessStat = ipAccessStat.get(path);
				ipPathAccessStat.count.incrementAndGet();
				ipPathAccessStat.timeCost.addAndGet(iv);
				ipPathAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

				if (cookie == null) {
					ipPathAccessStat.noSessionCount.incrementAndGet();
				}
//				else {
//					ipAccessStat.sessionIds.add(cookie.getValue());
//				}

				IpPathAccessStatListener ipPathAccessStatListener = ipPathAccessStats.getListener(duration);
				if (ipPathAccessStatListener != null) {
					boolean isContinue = ipPathAccessStatListener.onChanged(request, ip, path, ipAccessStat, ipPathAccessStat);
					if (!isContinue) {
						return false;
					}
				}
			}
		}
	
		
		return true;
	}

	/**
	 * tokenPathAccessStat
	 * @param request
	 * @param response
	 * @param path
	 * @param iv
	 * @return
	 */
	private boolean statTokenPath(HttpRequest request, HttpResponse response, String path, long iv) {
		if (tokenPathAccessStats == null) {
			return true;
		}
		
		if (response == null) {
			return false;
		}
		
		if (response.isSkipTokenStat() || request.isClosed()) {
			return true;
		}
		
		//统计一下Token访问数据
		String token = tokenPathAccessStats.getTokenGetter().getToken(request);
		if (StringUtils.isNotBlank(token)) {
			List<Long> list = tokenPathAccessStats.durationList;

			CurrUseridGetter currUseridGetter = tokenPathAccessStats.getCurrUseridGetter();
			String uid = null;
			if (currUseridGetter != null) {
				uid = currUseridGetter.getUserid(request);
			}

			StatPathFilter statPathFilter = tokenPathAccessStats.getStatPathFilter();

			//添加统计
			for (Long duration : list) {
				TokenAccessStat tokenAccessStat = tokenPathAccessStats.get(duration, token, request.getClientIp(), uid);//.get(duration, ip, path);//.get(v, channelContext.getClientNode().getIp());

				tokenAccessStat.count.incrementAndGet();
				tokenAccessStat.timeCost.addAndGet(iv);
				tokenAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

				if (statPathFilter.filter(path, request, response)) {
					TokenPathAccessStat tokenPathAccessStat = tokenAccessStat.get(path);
					tokenPathAccessStat.count.incrementAndGet();
					tokenPathAccessStat.timeCost.addAndGet(iv);
					tokenPathAccessStat.setLastAccessTime(SystemTimer.currentTimeMillis());

					TokenPathAccessStatListener tokenPathAccessStatListener = tokenPathAccessStats.getListener(duration);
					if (tokenPathAccessStatListener != null) {
						boolean isContinue = tokenPathAccessStatListener.onChanged(request, token, path, tokenAccessStat, tokenPathAccessStat);
						if (!isContinue) {
							return false;
						}
					}
				}
			}
		}
	

		return true;
	}

	private void logError(HttpRequest request, RequestLine requestLine, Throwable e) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n").append("remote  :").append(request.getClientIp());
		sb.append("\r\n").append("request :").append(requestLine.getLine());
		log.error(sb.toString(), e);

	}

	private void processCookieAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse httpResponse) throws ExecutionException {
		if (!httpConfig.isUseSession()) {
			return;
		}

		HttpSession httpSession = request.getHttpSession();//(HttpSession) channelContext.getAttribute();//.getHttpSession();//not null
		Cookie cookie = getSessionCookie(request, httpConfig);
		String sessionId = null;

		if (cookie == null) {
			createSessionCookie(request, httpSession, httpResponse);
//			log.info("{} 创建会话Cookie, {}", request.getChannelContext(), cookie);
		} else {
			sessionId = cookie.getValue();
			HttpSession httpSession1 = (HttpSession) httpConfig.getSessionStore().get(sessionId);

			if (httpSession1 == null) {//有cookie但是超时了
				createSessionCookie(request, httpSession, httpResponse);
			}
		}
	}

	/**
	 * 
	 * @param request
	 * @param httpSession
	 * @param httpResponse
	 * @return
	 * @author tanyaowu
	 */
	private void createSessionCookie(HttpRequest request, HttpSession httpSession, HttpResponse httpResponse) {
		String session_cookie_key = "tio_http_session_cookie";
		ChannelContext channelContext = request.getChannelContext();
		Object test = channelContext.getAttribute(session_cookie_key);
		if (test != null) {
			return;
		}
		
		String sessionId = httpSession.getId();
		//		String host = request.getHost();
		String domain = request.getDomain();
		
		boolean isip = Validator.isIpv4(domain);
		if (!isip) {
			String[] dms = StringUtils.split(domain, ".");
			if (dms.length > 2) {
				domain = "." + dms[dms.length - 2] + "." + dms[dms.length - 1];
			}
		}
		

		String name = httpConfig.getSessionCookieName();
		long maxAge = 3600 * 24 * 365 * 10;//Math.max(httpConfig.getSessionTimeout() * 30, 3600 * 24 * 365 * 10);
		//				maxAge = Long.MAX_VALUE; //把过期时间掌握在服务器端

		Cookie sessionCookie = new Cookie(domain, name, sessionId, maxAge);

		if (sessionCookieDecorator != null) {
			sessionCookieDecorator.decorate(sessionCookie, request, request.getDomain());
		}
		httpResponse.addCookie(sessionCookie);

		httpConfig.getSessionStore().put(sessionId, httpSession);

		channelContext.setAttribute(session_cookie_key, sessionCookie);
		return;
	}

	private void processCookieBeforeHandler(HttpRequest request, RequestLine requestLine) throws ExecutionException {
		if (!httpConfig.isUseSession()) {
			return;
		}

		Cookie cookie = getSessionCookie(request, httpConfig);
		HttpSession httpSession = null;
		if (cookie == null) {
			httpSession = createSession(request);
		} else {
			//			httpSession = (HttpSession)httpSession.getAttribute(SESSIONID_KEY);//loadingCache.getIfPresent(sessionCookie.getValue());
			String sessionId = cookie.getValue();
			httpSession = (HttpSession) httpConfig.getSessionStore().get(sessionId);
			if (httpSession == null) {
				log.info("{} session【{}】超时", request.getChannelContext(), sessionId);
				httpSession = createSession(request);
			}
		}
		request.setHttpSession(httpSession);
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
		return Resps.resp404(request, requestLine, httpConfig);
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
		if (throwableHandler != null) {
			return throwableHandler.handler(request, requestLine, throwable);
		}
		return Resps.resp500(request, requestLine, httpConfig, throwable);
	}

	/**
	 * @param httpConfig the httpConfig to set
	 */
	public void setHttpConfig(HttpConfig httpConfig) {
		this.httpConfig = httpConfig;
	}

	public void setHttpServerInterceptor(HttpServerInterceptor httpServerInterceptor) {
		this.httpServerInterceptor = httpServerInterceptor;
	}

	/**
	 * @param staticResCache the staticResCache to set
	 */
	public void setStaticResCache(CaffeineCache staticResCache) {
		this.staticResCache = staticResCache;
	}

	@Override
	public void clearStaticResCache(HttpRequest request) {
		if (staticResCache != null) {
			staticResCache.clear();
		}
	}

	public HttpSessionListener getHttpSessionListener() {
		return httpSessionListener;
	}

	public void setHttpSessionListener(HttpSessionListener httpSessionListener) {
		this.httpSessionListener = httpSessionListener;
	}

	public SessionCookieDecorator getSessionCookieDecorator() {
		return sessionCookieDecorator;
	}

	public void setSessionCookieDecorator(SessionCookieDecorator sessionCookieDecorator) {
		this.sessionCookieDecorator = sessionCookieDecorator;
	}

	public IpPathAccessStats getIpPathAccessStats() {
		return ipPathAccessStats;
	}

	public void setIpPathAccessStats(IpPathAccessStats ipPathAccessStats) {
		this.ipPathAccessStats = ipPathAccessStats;
	}



	public ThrowableHandler getThrowableHandler() {
		return throwableHandler;
	}

	public void setThrowableHandler(ThrowableHandler throwableHandler) {
		this.throwableHandler = throwableHandler;
	}

	public TokenPathAccessStats getTokenPathAccessStats() {
		return tokenPathAccessStats;
	}

	public void setTokenPathAccessStats(TokenPathAccessStats tokenPathAccessStats) {
		this.tokenPathAccessStats = tokenPathAccessStats;
	}
}
