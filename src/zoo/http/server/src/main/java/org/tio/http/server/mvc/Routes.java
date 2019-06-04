package org.tio.http.server.mvc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpRequest;
import org.tio.http.server.annotation.RequestPath;
import org.tio.http.server.mvc.intf.ControllerFactory;
import org.tio.utils.hutool.ArrayUtil;
import org.tio.utils.hutool.ClassScanAnnotationHandler;
import org.tio.utils.hutool.ClassUtil;
import org.tio.utils.hutool.FileUtil;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.json.Json;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/**
 * @author tanyaowu
 * 2017年7月1日 上午9:05:30
 */
public class Routes {
	private static Logger log = LoggerFactory.getLogger(Routes.class);

	/**
	 * 
	 */
	public static final String META_PATH_KEY = "TIO_HTTP_META_PATH";

//	private boolean writeMappingToFile = true;

	/**
	 * 路径和对象映射<br>
	 * key: /user<br>
	 * value: object<br>
	 */
	public final Map<String, Object> PATH_BEAN_MAP = new TreeMap<>();

	/**
	 * class和对象映射<br>
	 * key: XxxController.class<br>
	 * value: XxxController.class对应的实例对象<br>
	 */
	public static final Map<Class<?>, Object> CLASS_BEAN_MAP = new HashMap<>();

	/**
	 * bean和MethodAccess映射<br>
	 * key: XxxController.class对应的实例对象<br>
	 * value: MethodAccess<br>
	 */
	public static final Map<Object, MethodAccess> BEAN_METHODACCESS_MAP = new HashMap<>();

	/**
	 * 路径和class映射<br>
	 * 只是用来打印的<br>
	 * key: /user<br>
	 * value: Class<br>
	 */
	public final Map<String, Class<?>> PATH_CLASS_MAP = new TreeMap<>();

	/**
	 * 路径和class映射<br>
	 * key: class<br>
	 * value: /user<br>
	 */
	public static final Map<Class<?>, String> CLASS_PATH_MAP = new HashMap<>();

	/**
	 * Method路径映射<br>
	 * key: /user/update，包含forward的路径<br>
	 * value: method<br>
	 */
	public final Map<String, Method> PATH_METHOD_MAP = new TreeMap<>();

	/**
	 * 方法参数名映射<br>
	 * key: method<br>
	 * value: ["id", "name", "scanPackages"]<br>
	 */
	public final Map<Method, String[]> METHOD_PARAMNAME_MAP = new HashMap<>();

	/**
	 * path跟forward映射<br>
	 * key: 原访问路径<br>
	 * value: forward后的路径<br>
	 * 譬如：原来的访问路径是/user/123，forward是/user/getById，这个相当于是一个rewrite的功能，对外路径要相对友好，对内路径一般用于业务更便捷地处理
	 */
	public final Map<String, String> PATH_FORWARD_MAP = new HashMap<>();

	/**
	 * 方法和参数类型映射<br>
	 * key: method<br>
	 * value: [String.class, int.class]<br>
	 */
	public final Map<Method, Class<?>[]> METHOD_PARAMTYPE_MAP = new HashMap<>();

	/**
	 * 方法和对象映射<br>
	 * key: method<br>
	 * value: bean<br>
	 */
	public final Map<Method, Object> METHOD_BEAN_MAP = new HashMap<>();

	/**
	 * Method路径映射<br>
	 * 只是用于打印日志<br>
	 * key: /user/update<br>
	 * value: method string<br>
	 */
	public final Map<String, String> PATH_METHODSTR_MAP = new TreeMap<>();

	/**
	 * 含有路径变量的请求<br>
	 * key: 子路径的个数（pathUnitCount），譬如/user/{userid}就是2<br>
	 * value: VariablePathVo<br>
	 */
	public final Map<Integer, VariablePathVo[]> VARIABLE_PATH_MAP = new TreeMap<>();

	/**
	 * 含有路径变量的请求<br>
	 * 只是用于打印日志<br>
	 * key: 配置的路径/user/{userid}<br>
	 * value: method string<br>
	 */
	public final Map<String, String> VARIABLEPATH_METHODSTR_MAP = new TreeMap<>();

	private final StringBuilder errorStr = new StringBuilder();

	/**
	 * 
	 * @param scanPackages
	 */
	public Routes(String[] scanPackages) {
		this(scanPackages, null);
	}

	public Routes(String scanPackage) {
		this(scanPackage, null);
	}

	public Routes(String[] scanPackages, ControllerFactory controllerFactory) {
		addRoutes(scanPackages, controllerFactory);
	}

	public Routes(String scanPackage, ControllerFactory controllerFactory) {
		this(new String[] { scanPackage }, controllerFactory);
	}

	//
	public Routes(Class<?>[] scanRootClasses) {
		this(toPackages(scanRootClasses), null);
	}

	public Routes(Class<?> scanRootClasse) {
		this(scanRootClasse.getPackage().getName(), null);
	}

	public Routes(Class<?>[] scanRootClasses, ControllerFactory controllerFactory) {
		addRoutes(toPackages(scanRootClasses), controllerFactory);
	}

	public Routes(Class<?> scanRootClasse, ControllerFactory controllerFactory) {
		this(new String[] { scanRootClasse.getPackage().getName() }, controllerFactory);
	}

	public static String[] toPackages(Class<?>[] scanRootClasses) {
		String[] scanPackages = new String[scanRootClasses.length];
		int i = 0;
		for (Class<?> clazz : scanRootClasses) {
			scanPackages[i++] = clazz.getPackage().getName();
		}
		return scanPackages;
	}

	/**
	 * 添加路由
	 * @param scanPackages
	 * @author tanyaowu
	 */
	public void addRoutes(String[] scanPackages) {
		addRoutes(scanPackages, null);
	}

	/**
	 * 添加路由
	 * @param scanPackages
	 * @param controllerFactory
	 * @author tanyaowu
	 */
	public void addRoutes(String[] scanPackages, ControllerFactory controllerFactory) {
		if (controllerFactory == null) {
			controllerFactory = DefaultControllerFactory.me;
		}
		ControllerFactory controllerFactory1 = controllerFactory;
		if (scanPackages != null) {
			for (String pkg : scanPackages) {
				try {
					ClassUtil.scanPackage(pkg, new ClassScanAnnotationHandler(RequestPath.class) {
						@Override
						public void handlerAnnotation(Class<?> clazz) {
							try {
								Object bean = controllerFactory1.getInstance(clazz);//classWithAnnotation.newInstance();
								RequestPath classMapping = clazz.getAnnotation(RequestPath.class);
								String beanPath = classMapping.value();
								Object obj = PATH_BEAN_MAP.get(beanPath);
								if (obj != null) {
									log.error("mapping[{}] already exists in class [{}]", beanPath, obj.getClass().getName());
									errorStr.append("mapping[" + beanPath + "] already exists in class [" + obj.getClass().getName() + "]\r\n\r\n");
								} else {
									PATH_BEAN_MAP.put(beanPath, bean);
									CLASS_BEAN_MAP.put(clazz, bean);
									PATH_CLASS_MAP.put(beanPath, clazz);
									CLASS_PATH_MAP.put(clazz, beanPath);

									MethodAccess access = MethodAccess.get(clazz);
									BEAN_METHODACCESS_MAP.put(bean, access);
								}

								Method[] methods = clazz.getDeclaredMethods();//ClassUtil.getPublicMethods(clazz);
								c: for (Method method : methods) {
									int modifiers = method.getModifiers();
									if (!Modifier.isPublic(modifiers)) {
										continue c;
									}

									RequestPath mapping = method.getAnnotation(RequestPath.class);
									if (mapping == null) {
										//										log.error(method.getName());
										continue c;
									}

									//									String methodName = method.getName();
									String methodPath = mapping.value();
									//									if (StrUtil.isBlank(beanPath)) {
									//										log.error("方法有注解，但类没注解, method:{}, class:{}", methodName, clazz);
									//										errorStr.append("方法有注解，但类没注解, method:" + methodName + ", class:" + clazz + "\r\n\r\n");
									//										continue c;
									//									}

									String completePath = beanPath + methodPath;
									Class<?>[] parameterTypes = method.getParameterTypes();
									try {
										Paranamer paranamer = new BytecodeReadingParanamer();
										String[] parameterNames = paranamer.lookupParameterNames(method, false); // will return null if not found

										Method checkMethod = PATH_METHOD_MAP.get(completePath);
										if (checkMethod != null) {
											log.error("mapping[{}] already exists in method [{}]", completePath, checkMethod.getDeclaringClass() + "#" + checkMethod.getName());
											errorStr.append("mapping[" + completePath + "] already exists in method [" + checkMethod.getDeclaringClass() + "#"
											        + checkMethod.getName() + "]\r\n\r\n");
											continue c;
										}

										PATH_METHOD_MAP.put(completePath, method);

										String methodStr = methodToStr(method, parameterNames);
										PATH_METHODSTR_MAP.put(completePath, methodStr);

										METHOD_PARAMNAME_MAP.put(method, parameterNames);
										METHOD_PARAMTYPE_MAP.put(method, parameterTypes);
										if (StrUtil.isNotBlank(mapping.forward())) {
											PATH_FORWARD_MAP.put(completePath, mapping.forward());
											PATH_METHODSTR_MAP.put(mapping.forward(), methodStr);
											PATH_METHOD_MAP.put(mapping.forward(), method);
										}

										METHOD_BEAN_MAP.put(method, bean);
									} catch (Throwable e) {
										log.error(e.toString(), e);
									}
								}
							} catch (Throwable e) {
								log.error(e.toString(), e);
							}

						}
					});
				} catch (Exception e) {
					log.error(e.toString(), e);
				}

			}

			String pathClassMapStr = Json.toFormatedJson(PATH_CLASS_MAP);
			log.info("class  mapping\r\n{}", pathClassMapStr);
			String pathMethodstrMapStr = Json.toFormatedJson(PATH_METHODSTR_MAP);
			log.info("method mapping\r\n{}", pathMethodstrMapStr);

			processVariablePath();

			String variablePathMethodstrMapStr = Json.toFormatedJson(VARIABLEPATH_METHODSTR_MAP);
			log.info("variable path mapping\r\n{}", variablePathMethodstrMapStr);

			String writeMappingToFile = System.getProperty("tio.mvc.route.writeMappingToFile", "true");
			if ("true".equalsIgnoreCase(writeMappingToFile)) {
				try {
					FileUtil.writeString(pathClassMapStr, "/tio_mvc_path_class.json", "utf-8");
					FileUtil.writeString(pathMethodstrMapStr, "/tio_mvc_path_method.json", "utf-8");
					FileUtil.writeString(variablePathMethodstrMapStr, "/tio_mvc_variablepath_method.json", "utf-8");
					if (errorStr.length() > 0) {
						FileUtil.writeString(errorStr.toString(), "/tio_error_mvc.txt", "utf-8");
					}
				} catch (Exception e) {
					//										log.error(e.toString(), e);
				}
			}
		}
	}

	/**
	 * 处理有变量的路径
	 * @param PATH_METHOD_MAP
	 */
	private void processVariablePath() {
		Set<Entry<String, Method>> set = PATH_METHOD_MAP.entrySet();
		//		Set<String> forRemoved = new HashSet<>();
		for (Entry<String, Method> entry : set) {
			String path = entry.getKey();
			Method method = entry.getValue();
			if (StrUtil.contains(path, '{') && StrUtil.contains(path, '}')) {
				String[] pathUnits = StrUtil.split(path, "/");
				PathUnitVo[] pathUnitVos = new PathUnitVo[pathUnits.length];

				boolean isVarPath = false; //是否是带变量的路径
				for (int i = 0; i < pathUnits.length; i++) {
					PathUnitVo pathUnitVo = new PathUnitVo();
					String pathUnit = pathUnits[i];
					if (StrUtil.contains(pathUnit, '{') || StrUtil.contains(pathUnit, '}')) {
						if (StrUtil.startWith(pathUnit, "{") && StrUtil.endWith(pathUnit, "}")) {
							String[] xx = METHOD_PARAMNAME_MAP.get(method);
							String varName = StrUtil.subBetween(pathUnit, "{", "}");
							if (ArrayUtil.contains(xx, varName)) {
								isVarPath = true;
								pathUnitVo.setVar(true);
								pathUnitVo.setPath(varName);
							} else {
								log.error("path:{}, 对应的方法中并没有包含参数名为{}的参数", path, varName);
								errorStr.append("path:{" + path + "}, 对应的方法中并没有包含参数名为" + varName + "的参数\r\n\r\n");
							}
						} else {
							pathUnitVo.setVar(false);
							pathUnitVo.setPath(pathUnit);
						}
					} else {
						pathUnitVo.setVar(false);
						pathUnitVo.setPath(pathUnit);
					}
					pathUnitVos[i] = pathUnitVo;
				}

				if (isVarPath) {
					VariablePathVo variablePathVo = new VariablePathVo(path, method, pathUnitVos);
					addVariablePathVo(pathUnits.length, variablePathVo);
				}
			}
		}

		//		set.removeAll(forRemoved);
	}

	/**
	 * 
	 * @param pathUnitCount
	 * @param forceCreate
	 * @return
	 */
	@SuppressWarnings("unused")
	private VariablePathVo[] getVariablePathVos(Integer pathUnitCount, boolean forceCreate) {
		VariablePathVo[] ret = VARIABLE_PATH_MAP.get(pathUnitCount);
		if (forceCreate && ret == null) {
			ret = new VariablePathVo[0];
			VARIABLE_PATH_MAP.put(pathUnitCount, ret);
		}
		return ret;
	}

	/**
	 * 根据class获取class对应的bean
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getController(Class<T> clazz) {
		return (T) CLASS_BEAN_MAP.get(clazz);
	}

	public static String getRequestPath(Class<?> clazz) {
		return CLASS_PATH_MAP.get(clazz);
	}

	/**
	 * 
	 * @param pathUnitCount
	 * @param variablePathVo
	 */
	private void addVariablePathVo(Integer pathUnitCount, VariablePathVo variablePathVo) {
		VariablePathVo[] existValue = VARIABLE_PATH_MAP.get(pathUnitCount);
		if (existValue == null) {
			existValue = new VariablePathVo[] { variablePathVo };
			VARIABLE_PATH_MAP.put(pathUnitCount, existValue);
		} else {
			VariablePathVo[] newExistValue = new VariablePathVo[existValue.length + 1];
			System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
			newExistValue[newExistValue.length - 1] = variablePathVo;
			VARIABLE_PATH_MAP.put(pathUnitCount, newExistValue);
		}
		VARIABLEPATH_METHODSTR_MAP.put(variablePathVo.getPath(), methodToStr(variablePathVo.getMethod(), METHOD_PARAMNAME_MAP.get(variablePathVo.getMethod())));
		//org.tio.http.server.mvc.Routes.METHOD_PARAMNAME_MAP
	}

	private String methodToStr(Method method, String[] parameterNames) {
		return method.getDeclaringClass().getName() + "." + method.getName() + "(" + ArrayUtil.join(parameterNames, ",") + ")";
		//		matchingClass.getName() + "." + method.getName() + "(" + ArrayUtil.join(parameterNames, ",") + ")"
	}

	@SuppressWarnings("unused")
	public Method getMethodByPath(String path, HttpRequest request) {
		Method method = PATH_METHOD_MAP.get(path);
		if (method == null) {
			String[] pathUnitsOfRequest = StrUtil.split(path, "/"); // "/user/214" -- > ["user", "214"]
			VariablePathVo[] variablePathVos = VARIABLE_PATH_MAP.get(pathUnitsOfRequest.length);
			if (variablePathVos != null) {
				tag1: for (VariablePathVo variablePathVo : variablePathVos) {
					PathUnitVo[] pathUnitVos = variablePathVo.getPathUnits();
					tag2: for (int i = 0; i < pathUnitVos.length; i++) {
						PathUnitVo pathUnitVo = pathUnitVos[i];
						String pathUnitOfRequest = pathUnitsOfRequest[i];

						if (pathUnitVo.isVar()) {
							request.addParam(pathUnitVo.getPath(), pathUnitOfRequest);
						} else {
							if (!StrUtil.equals(pathUnitVo.getPath(), pathUnitOfRequest)) {
								continue tag1;
							}
						}
					}

					String metapath = variablePathVo.getPath();
					String forward = PATH_FORWARD_MAP.get(metapath);
					if (StrUtil.isNotBlank(forward)) {
						request.requestLine.path = forward;
					}
					method = variablePathVo.getMethod();
					return method;
				}
			}
			return null;
		} else {
			return method;
		}
	}
}
