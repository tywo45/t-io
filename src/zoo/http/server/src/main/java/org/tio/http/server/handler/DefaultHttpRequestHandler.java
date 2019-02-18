package org.tio.http.server.handler;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Tio;
import org.tio.http.common.Cookie;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResource;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;
import org.tio.http.common.session.HttpSession;
import org.tio.http.common.session.limiter.SessionRateLimiter;
import org.tio.http.common.session.limiter.SessionRateVo;
import org.tio.http.common.utils.HttpGzipUtils;
import org.tio.http.common.view.freemarker.FreemarkerConfig;
import org.tio.http.server.intf.CurrUseridGetter;
import org.tio.http.server.intf.HttpServerInterceptor;
import org.tio.http.server.intf.ThrowableHandler;
import org.tio.http.server.mvc.Routes;
import org.tio.http.server.mvc.intf.ControllerFactory;
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
import org.tio.utils.IoUtils;
import org.tio.utils.SystemTimer;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.freemarker.FreemarkerUtils;
import org.tio.utils.hutool.ArrayUtil;
import org.tio.utils.hutool.BeanUtil;
import org.tio.utils.hutool.ClassUtil;
import org.tio.utils.hutool.FileUtil;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.hutool.Validator;

import com.esotericsoftware.reflectasm.MethodAccess;

import freemarker.template.Configuration;

/**
 *
 * @author tanyaowu
 *
 */
public class DefaultHttpRequestHandler implements HttpRequestHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHttpRequestHandler.class);

	/**
	 * 静态资源的CacheName
	 * key:   path 譬如"/index.html"
	 * value: FileCache
	 */
	private static final String STATIC_RES_CONTENT_CACHENAME = "TIO_HTTP_STATIC_RES_CONTENT";

	private static final String SESSIONRATELIMITER_CACHENAME = "TIO_HTTP_SESSIONRATELIMITER_CACHENAME";

	/**
	 * 把cookie对象存到ChannelContext对象中
	 * request.channelContext.setAttribute(SESSION_COOKIE_KEY, sessionCookie);
	 */
	private static final String SESSION_COOKIE_KEY = "TIO_HTTP_SESSION_COOKIE";

	private static final Map<Class<?>, MethodAccess> CLASS_METHODACCESS_MAP = new HashMap<>();

	private static MethodAccess getMethodAccess(Class<?> clazz) {
		MethodAccess ret = CLASS_METHODACCESS_MAP.get(clazz);
		if (ret == null) {
			synchronized (CLASS_METHODACCESS_MAP) {
				ret = CLASS_METHODACCESS_MAP.get(clazz);
				if (ret == null) {
					ret = MethodAccess.get(clazz);
					CLASS_METHODACCESS_MAP.put(clazz, ret);
				}
			}
		}
		return ret;
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

	private CaffeineCache sessionRateLimiterCache;

	private static final String SESSIONRATELIMITER_KEY_SPLIT = "?";

	private String	contextPath;
	private int		contextPathLength	= 0;
	private String	suffix;
	private int		suffixLength		= 0;

	/**
	 * 
	 * @param httpConfig
	 * @param scanRootClasse
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Class<?> scanRootClasse) {
		this(httpConfig, new Class<?>[] { scanRootClasse });
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanRootClasse
	 * @param controllerFactory
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Class<?> scanRootClasse, ControllerFactory controllerFactory) {
		this(httpConfig, new Class<?>[] { scanRootClasse }, controllerFactory);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanRootClasses
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Class<?>[] scanRootClasses) {
		this(httpConfig, scanRootClasses, null);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanRootClasses
	 * @param controllerFactory
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Class<?>[] scanRootClasses, ControllerFactory controllerFactory) {
		Routes routes = new Routes(scanRootClasses, controllerFactory);
		init(httpConfig, routes);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanPackage
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, String scanPackage) {
		this(httpConfig, scanPackage, null);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanPackage
	 * @param controllerFactory
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, String scanPackage, ControllerFactory controllerFactory) {
		this(httpConfig, new String[] { scanPackage }, controllerFactory);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanPackages
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, String[] scanPackages) {
		this(httpConfig, scanPackages, null);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param scanPackages
	 * @param controllerFactory
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, String[] scanPackages, ControllerFactory controllerFactory) {
		Routes routes = new Routes(scanPackages, controllerFactory);
		init(httpConfig, routes);
	}

	/**
	 * 
	 * @param httpConfig
	 * @param routes
	 */
	public DefaultHttpRequestHandler(HttpConfig httpConfig, Routes routes) {
		init(httpConfig, routes);
	}

	private void init(HttpConfig httpConfig, Routes routes) {
		if (httpConfig == null) {
			throw new RuntimeException("httpConfig can not be null");
		}
		this.contextPath = httpConfig.getContextPath();
		this.suffix = httpConfig.getSuffix();

		if (StrUtil.isNotBlank(contextPath)) {
			contextPathLength = contextPath.length();
		}
		if (StrUtil.isNotBlank(suffix)) {
			suffixLength = suffix.length();
		}

		this.httpConfig = httpConfig;

		if (httpConfig.getMaxLiveTimeOfStaticRes() > 0) {
			staticResCache = CaffeineCache.register(STATIC_RES_CONTENT_CACHENAME, (long) httpConfig.getMaxLiveTimeOfStaticRes(), null);
		}

		sessionRateLimiterCache = CaffeineCache.register(SESSIONRATELIMITER_CACHENAME, 60 * 1L, null);

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
	public HttpConfig getHttpConfig(HttpRequest request) {
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

	private Method getMethod(HttpRequest request, RequestLine requestLine) {
		Method method = null;
		String path = requestLine.path;
		if (routes != null) {
			method = routes.getMethodByPath(path, request);
			path = requestLine.path;
		}
		if (method == null) {
			if (StrUtil.isNotBlank(httpConfig.getWelcomeFile())) {
				if (StrUtil.endWith(path, "/")) {
					path = path + httpConfig.getWelcomeFile();
					requestLine.setPath(path);

					if (routes != null) {
						method = routes.getMethodByPath(path, request);
						path = requestLine.path;
					}
				}
			}
		}

		return method;
	}

	@Override
	public HttpResponse handler(HttpRequest request) throws Exception {
		request.setNeedForward(false);

		if (!checkDomain(request)) {
			Tio.remove(request.channelContext, "过来的域名[" + request.getDomain() + "]不对");
			return null;
		}

		long start = SystemTimer.currTime;

		HttpResponse response = null;
		RequestLine requestLine = request.getRequestLine();
		String path = requestLine.path;

		if (StrUtil.isNotBlank(contextPath)) {
			if (StrUtil.startWith(path, contextPath)) {
				path = StrUtil.subSuf(path, contextPathLength);
			} else {

			}
		}

		if (StrUtil.isNotBlank(suffix)) {
			if (StrUtil.endWith(path, suffix)) {
				path = StrUtil.sub(path, 0, path.length() - suffixLength);
			} else {

			}
		}
		requestLine.setPath(path);

		try {
			processCookieBeforeHandler(request, requestLine);

			requestLine = request.getRequestLine();

			Method method = getMethod(request, requestLine);
			path = requestLine.path;

			if (httpServerInterceptor != null) {
				response = httpServerInterceptor.doBeforeHandler(request, requestLine, response);
				if (response != null) {
					return response;
				}
			}
			path = requestLine.path;
			if (method == null) {
				method = getMethod(request, requestLine);
				path = requestLine.path;
			}

			//流控
			if (httpConfig.isUseSession()) {
				SessionRateLimiter sessionRateLimiter = httpConfig.sessionRateLimiter;
				if (sessionRateLimiter != null) {
					boolean pass = false;

					HttpSession httpSession = request.getHttpSession();
					String key = path + SESSIONRATELIMITER_KEY_SPLIT + httpSession.getId();
					SessionRateVo sessionRateVo = sessionRateLimiterCache.get(key, SessionRateVo.class);
					if (sessionRateVo == null) {
						synchronized (httpSession) {
							sessionRateVo = sessionRateLimiterCache.get(key, SessionRateVo.class);
							if (sessionRateVo == null) {
								sessionRateVo = SessionRateVo.create(path);
								sessionRateLimiterCache.put(key, sessionRateVo);
								pass = true;
							}
						}
					}

					if (!pass) {
						if (sessionRateLimiter.allow(request, sessionRateVo)) {
							pass = true;
						}
					}

					if (!pass) {
						response = sessionRateLimiter.response(request, sessionRateVo);
						return response;
					}

					//更新上次访问时间（放在这个位置：被拒绝访问的就不更新lastAccessTime）
					sessionRateVo.setLastAccessTime(SystemTimer.currTime);
					sessionRateVo.getAccessCount().incrementAndGet();
				}
			}

			if (method != null) {
				String[] paramnames = routes.METHOD_PARAMNAME_MAP.get(method);
				Class<?>[] parameterTypes = routes.METHOD_PARAMTYPE_MAP.get(method);//method.getParameterTypes();
				Object bean = routes.METHOD_BEAN_MAP.get(method);
				Object obj = null;
				if (parameterTypes == null || parameterTypes.length == 0) {
					obj = Routes.BEAN_METHODACCESS_MAP.get(bean).invoke(bean, method.getName(), parameterTypes, (Object) null);
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
							} else if (paramType == ServerChannelContext.class) { //paramType.isAssignableFrom(ServerChannelContext.class)
								paramValues[i] = request.channelContext;
							} else {
								Map<String, Object[]> params = request.getParams();
								if (params != null) {
									if (ClassUtils.isSimpleTypeOrArray(paramType)) {
										//										paramValues[i] = Ognl.getValue(paramnames[i], (Object) params, paramType);
										Object[] value = params.get(paramnames[i]);
										if (value != null && value.length > 0) {
											if (paramType.isArray()) {
												//												paramValues[i] = Convert.convert(paramType, value);
												if (value.getClass() == String[].class) {
													paramValues[i] = StrUtil.convert(paramType, (String[]) value);
												} else {
													paramValues[i] = value;
												}
											} else {
												//												paramValues[i] = Convert.convert(paramType, value[0]);
												if (value[0].getClass() == String.class) {
													paramValues[i] = StrUtil.convert(paramType, (String) value[0]);
												} else {
													paramValues[i] = value[0];
												}
											}
										}
									} else {
										paramValues[i] = paramType.newInstance();//BeanUtil.mapToBean(params, paramType, true);
										Set<Entry<String, Object[]>> set = params.entrySet();
										label2: for (Entry<String, Object[]> entry : set) {
											String fieldName = entry.getKey();
											Object[] fieldValue = entry.getValue();

											PropertyDescriptor propertyDescriptor = BeanUtil.getPropertyDescriptor(paramType, fieldName, false);
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
															Object theValue = null;//Convert.convert(clazz, fieldValue);
															if (fieldValue.getClass() == String[].class) {
																theValue = StrUtil.convert(clazz, (String[]) fieldValue);
															} else {
																theValue = fieldValue;
															}

															getMethodAccess(paramType).invoke(paramValues[i], writeMethod.getName(), theValue);
														} else {
															Object theValue = null;//Convert.convert(clazz, fieldValue[0]);
															if (fieldValue[0].getClass() == String.class) {
																theValue = StrUtil.convert(clazz, (String) fieldValue[0]);
															} else {
																theValue = fieldValue[0];
															}

															getMethodAccess(paramType).invoke(paramValues[i], writeMethod.getName(), theValue);
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

					obj = Routes.BEAN_METHODACCESS_MAP.get(bean).invoke(bean, method.getName(), parameterTypes, paramValues);
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
						response.addHeader(HeaderName.tio_from_cache, HeaderValue.Tio_From_Cache.TRUE);
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
					String pageRoot = httpConfig.getPageRoot(request);
					if (pageRoot != null) {
						//						if (StrUtil.endWith(path, "/")) {
						//							path = path + "index.html";
						//						}
						////						else {
						////							path = path + "/index.html";
						////						}
						//						
						//						String complatePath = pageRoot + path;
						//						if (httpConfig.isPageInClasspath()) {
						//							URL url = this.getClass().getClassLoader().getResource(complatePath);
						//							if (url != null) {
						//								file = new File(url.toURI());
						//							}
						//						} else {
						////							String root = FileUtil.getAbsolutePath(pageRoot);
						//							file = new File(complatePath);
						////							if (!file.exists() || file.isDirectory()) {
						////								
						////								file = new File(pageRoot, path);
						////							}
						//						}

						HttpResource httpResource = httpConfig.getResource(request, path);//.getFile(request, path);

						if (httpResource != null) {
							file = httpResource.getFile();
							String template = httpResource.getPath(); // "/res/css/header-all.css"
							InputStream inputStream = httpResource.getInputStream();

							String extension = FileUtil.extName(template);

							//项目中需要，时间支持一下freemarker模板，后面要做模板支持抽象设计
							FreemarkerConfig freemarkerConfig = httpConfig.getFreemarkerConfig();
							if (freemarkerConfig != null) {

								if (ArrayUtil.contains(freemarkerConfig.getSuffixes(), extension)) {
									Configuration configuration = freemarkerConfig.getConfiguration(request);
									if (configuration != null) {
										Object model = freemarkerConfig.getModelGenerator().generate(request);
										if (request.isClosed()) {
											return null;
										} else {
											if (model instanceof HttpResponse) {
												response = (HttpResponse) model;
												return response;
											} else {
												try {
													String retStr = FreemarkerUtils.generateStringByPath(template, configuration, model);
													response = Resps.bytes(request, retStr.getBytes(configuration.getDefaultEncoding()), extension);
													return response;
												} catch (Exception e) {
													log.error("freemarker错误，当成普通文本处理：" + file.getCanonicalPath() + ", " + e.toString());
												}
											}
										}
									}
								}
							}

							if (file != null) {
								response = Resps.file(request, file);
							} else {
								response = Resps.bytes(request, IoUtils.toByteArray(inputStream), extension);//.file(request, file);
							}

							response.setStaticRes(true);

							//把静态资源放入缓存
							if (response.isStaticRes() && staticResCache != null/** && request.getIsSupportGzip()*/
							) {
								if (response.getBody() != null && response.getStatus() == HttpResponseStatus.C200) {
									HeaderValue contentType = response.getHeader(HeaderName.Content_Type);
									HeaderValue contentEncoding = response.getHeader(HeaderName.Content_Encoding);
									HeaderValue lastModified = response.getLastModified();//.getHeader(HttpConst.ResponseHeaderKey.Last_Modified);

									Map<HeaderName, HeaderValue> headers = new HashMap<>();
									if (contentType != null) {
										headers.put(HeaderName.Content_Type, contentType);
									}
									if (contentEncoding != null) {
										headers.put(HeaderName.Content_Encoding, contentEncoding);
									}
									//									if (StrUtil.isNotBlank(lastModified)) {
									//										headers.put(HttpConst.ResponseHeaderKey.Last_Modified, lastModified);
									//									}
									//									headers.put(HttpConst.ResponseHeaderKey.tio_from_cache, "true");

									HttpResponse responseInCache = new HttpResponse(request);
									responseInCache.addHeaders(headers);
									if (lastModified != null) {
										responseInCache.setLastModified(lastModified);
									}
									responseInCache.setBody(response.getBody());
									responseInCache.setHasGzipped(response.isHasGzipped());

									if (file != null) {
										fileCache = new FileCache(responseInCache, file.lastModified());
									} else {
										fileCache = new FileCache(responseInCache, ManagementFactory.getRuntimeMXBean().getStartTime());
									}

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
			try {
				long time = SystemTimer.currTime;
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
							log.error(requestLine.toString(), e);
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
			} catch (Exception e) {
				log.error(request.requestLine.toString(), e);
			} finally {
				if (request.isNeedForward()) {
					request.setForward(true);
					return handler(request);
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

		//		Cookie cookie = getSessionCookie(request, httpConfig);
		String sessionId = getSessionId(request);

		StatPathFilter statPathFilter = ipPathAccessStats.getStatPathFilter();

		//添加统计
		for (Long duration : ipPathAccessStats.durationList) {
			IpAccessStat ipAccessStat = ipPathAccessStats.get(duration, ip);//.get(duration, ip, path);//.get(v, channelContext.getClientNode().getIp());

			ipAccessStat.count.incrementAndGet();
			ipAccessStat.timeCost.addAndGet(iv);
			ipAccessStat.setLastAccessTime(SystemTimer.currTime);
			if (StrUtil.isBlank(sessionId)) {
				ipAccessStat.noSessionCount.incrementAndGet();
			} else {
				ipAccessStat.sessionIds.add(sessionId);
			}

			if (statPathFilter.filter(path, request, response)) {
				IpPathAccessStat ipPathAccessStat = ipAccessStat.get(path);
				ipPathAccessStat.count.incrementAndGet();
				ipPathAccessStat.timeCost.addAndGet(iv);
				ipPathAccessStat.setLastAccessTime(SystemTimer.currTime);

				if (StrUtil.isBlank(sessionId)) {
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
		if (StrUtil.isNotBlank(token)) {
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
				tokenAccessStat.setLastAccessTime(SystemTimer.currTime);

				if (statPathFilter.filter(path, request, response)) {
					TokenPathAccessStat tokenPathAccessStat = tokenAccessStat.get(path);
					tokenPathAccessStat.count.incrementAndGet();
					tokenPathAccessStat.timeCost.addAndGet(iv);
					tokenPathAccessStat.setLastAccessTime(SystemTimer.currTime);

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
		sb.append("\r\n").append("request :").append(requestLine.toString());
		log.error(sb.toString(), e);

	}

	private void processCookieAfterHandler(HttpRequest request, RequestLine requestLine, HttpResponse httpResponse) throws ExecutionException {
		if (httpResponse == null) {
			return;
		}

		if (!httpConfig.isUseSession()) {
			return;
		}

		HttpSession httpSession = request.getHttpSession();//(HttpSession) channelContext.getAttribute();//.getHttpSession();//not null
		//		Cookie cookie = getSessionCookie(request, httpConfig);
		String sessionId = getSessionId(request);

		if (StrUtil.isBlank(sessionId)) {
			createSessionCookie(request, httpSession, httpResponse);
			//			log.info("{} 创建会话Cookie, {}", request.getChannelContext(), cookie);
		} else {
			HttpSession httpSession1 = (HttpSession) httpConfig.getSessionStore().get(sessionId);

			if (httpSession1 == null) {//有cookie但是超时了
				createSessionCookie(request, httpSession, httpResponse);
			}
		}
	}

	/**
	 * 根据session创建session对应的cookie
	 * 注意：先有session，后有session对应的cookie
	 * @param request
	 * @param httpSession
	 * @param httpResponse
	 * @return
	 * @author tanyaowu
	 */
	private void createSessionCookie(HttpRequest request, HttpSession httpSession, HttpResponse httpResponse) {
		if (httpResponse == null) {
			return;
		}

		Object test = request.channelContext.getAttribute(SESSION_COOKIE_KEY);
		if (test != null) {
			return;
		}

		String sessionId = httpSession.getId();
		//		String host = request.getHost();
		String domain = request.getDomain();

		boolean isip = Validator.isIpv4(domain);
		if (!isip) {
			String[] dms = StrUtil.split(domain, ".");
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

		request.channelContext.setAttribute(SESSION_COOKIE_KEY, sessionCookie);
		return;
	}

	private void processCookieBeforeHandler(HttpRequest request, RequestLine requestLine) throws ExecutionException {
		if (!httpConfig.isUseSession()) {
			return;
		}

		String sessionId = getSessionId(request);
		//		Cookie cookie = getSessionCookie(request, httpConfig);
		HttpSession httpSession = null;
		if (StrUtil.isBlank(sessionId)) {
			httpSession = createSession(request);
		} else {
			//			if (StrUtil.isBlank(sessionId)) {
			//				sessionId = cookie.getValue();
			//			}

			httpSession = (HttpSession) httpConfig.getSessionStore().get(sessionId);
			if (httpSession == null) {
				log.info("{} session【{}】超时", request.channelContext, sessionId);
				httpSession = createSession(request);
			}
		}
		request.setHttpSession(httpSession);
	}

	public static String getSessionId(HttpRequest request) {
		String sessionId = request.getString(org.tio.http.common.HttpConfig.TIO_HTTP_SESSIONID);
		if (StrUtil.isNotBlank(sessionId)) {
			return sessionId;
		}

		Cookie cookie = getSessionCookie(request, request.httpConfig);
		if (cookie != null) {
			return cookie.getValue();
		}

		return null;
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) throws Exception {
		if (routes != null) {
			String page404 = httpConfig.getPage404();
			Method method = routes.PATH_METHOD_MAP.get(page404);
			if (method != null) {
				return Resps.forward(request, page404);
			}
		}

		return Resps.resp404(request, requestLine, httpConfig);
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) throws Exception {
		if (throwableHandler != null) {
			return throwableHandler.handler(request, requestLine, throwable);
		}

		if (routes != null) {
			String page500 = httpConfig.getPage500();
			Method method = routes.PATH_METHOD_MAP.get(page500);
			if (method != null) {
				return Resps.forward(request, page500);
			}
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
	public void clearStaticResCache() {
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
