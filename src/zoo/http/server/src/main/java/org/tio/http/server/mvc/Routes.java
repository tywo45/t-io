package org.tio.http.server.mvc;

import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.HttpRequest;
import org.tio.http.server.annotation.RequestPath;
import org.tio.http.server.mvc.intf.ControllerFactory;
import org.tio.utils.json.Json;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import cn.hutool.core.util.ArrayUtil;
import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.ClassAnnotationMatchProcessor;
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor;
import jodd.io.FileUtil;

/**
 * @author tanyaowu
 * 2017年7月1日 上午9:05:30
 */
public class Routes {
	private static Logger log = LoggerFactory.getLogger(Routes.class);

	private boolean writeMappingToFile = false;

	/**
	 * 路径和对象映射<br>
	 * key: /user<br>
	 * value: object<br>
	 */
	public final Map<String, Object> pathBeanMap = new TreeMap<>();

	/**
	 * class和对象映射<br>
	 * key: XxxController.class<br>
	 * value: XxxController.class对应的实例对象<br>
	 */
	public static final Map<Class<?>, Object> classBeanMap = new HashMap<>();

	/**
	 * bean和MethodAccess映射<br>
	 * key: XxxController.class对应的实例对象<br>
	 * value: MethodAccess<br>
	 */
	public static final Map<Object, MethodAccess> beanMethodaccessMap = new HashMap<>();

	/**
	 * 路径和class映射<br>
	 * 只是用来打印的<br>
	 * key: /user<br>
	 * value: Class<br>
	 */
	public final Map<String, Class<?>> pathClassMap = new TreeMap<>();

	/**
	 * 路径和class映射<br>
	 * key: class<br>
	 * value: /user<br>
	 */
	public static final Map<Class<?>, String> classPathMap = new HashMap<>();

	/**
	 * Method路径映射<br>
	 * key: /user/update<br>
	 * value: method<br>
	 */
	public final Map<String, Method> pathMethodMap = new TreeMap<>();

	/**
	 * 方法参数名映射<br>
	 * key: method<br>
	 * value: ["id", "name", "scanPackages"]<br>
	 */
	public final Map<Method, String[]> methodParamnameMap = new HashMap<>();

	/**
	 * 方法和参数类型映射<br>
	 * key: method<br>
	 * value: [String.class, int.class]<br>
	 */
	public final Map<Method, Class<?>[]> methodParamtypeMap = new HashMap<>();

	/**
	 * 方法和对象映射<br>
	 * key: method<br>
	 * value: bean<br>
	 */
	public final Map<Method, Object> methodBeanMap = new HashMap<>();

	/**
	 * Method路径映射<br>
	 * 只是用于打印日志<br>
	 * key: /user/update<br>
	 * value: method string<br>
	 */
	public final Map<String, String> pathMethodstrMap = new TreeMap<>();

	/**
	 * 含有路径变量的请求<br>
	 * key: 子路径的个数（pathUnitCount），譬如/user/{userid}就是2<br>
	 * value: VariablePathVo<br>
	 */
	public final Map<Integer, VariablePathVo[]> variablePathMap = new TreeMap<>();

	/**
	 * 含有路径变量的请求<br>
	 * 只是用于打印日志<br>
	 * key: 配置的路径/user/{userid}<br>
	 * value: method string<br>
	 */
	public Map<String, String> variablePathMethodstrMap = new TreeMap<>();

	private final StringBuilder errorStr = new StringBuilder();

	/**
	 * 
	 * @param scanPackages
	 */
	public Routes(String[] scanPackages) {
		this(scanPackages, null);
	}

	/**
	 * 
	 * @param scanPackages
	 * @param controllerFactory
	 */
	public Routes(String[] scanPackages, ControllerFactory controllerFactory) {
		addRoutes(scanPackages, controllerFactory);
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
			final FastClasspathScanner fastClasspathScanner = new FastClasspathScanner(scanPackages);
			//			fastClasspathScanner.verbose();
			fastClasspathScanner.matchClassesWithAnnotation(RequestPath.class, new ClassAnnotationMatchProcessor() {
				@Override
				public void processMatch(Class<?> classWithAnnotation) {
					try {
						Object bean = controllerFactory1.getInstance(classWithAnnotation);//classWithAnnotation.newInstance();
						RequestPath mapping = classWithAnnotation.getAnnotation(RequestPath.class);
						//						String beanPath = Routes.this.contextPath + mapping.value();
						String beanPath = mapping.value();
						//						if (!StringUtils.endsWith(beanUrl, "/")) {
						//							beanUrl = beanUrl + "/";
						//						}

						beanPath = formatBeanPath(beanPath);

						Object obj = pathBeanMap.get(beanPath);
						if (obj != null) {
							log.error("mapping[{}] already exists in class [{}]", beanPath, obj.getClass().getName());
							errorStr.append("mapping[" + beanPath + "] already exists in class [" + obj.getClass().getName() + "]\r\n\r\n");
						} else {
							pathBeanMap.put(beanPath, bean);
							classBeanMap.put(classWithAnnotation, bean);
							pathClassMap.put(beanPath, classWithAnnotation);
							classPathMap.put(classWithAnnotation, beanPath);

							MethodAccess access = MethodAccess.get(classWithAnnotation);
							beanMethodaccessMap.put(bean, access);
						}
					} catch (Throwable e) {

						log.error(e.toString(), e);
					}
				}
			});

			fastClasspathScanner.matchClassesWithMethodAnnotation(RequestPath.class, new MethodAnnotationMatchProcessor() {
				@Override
				public void processMatch(Class<?> matchingClass, Executable matchingMethodOrConstructor) {
					//					log.error(matchingMethodOrConstructor + "");
					RequestPath mapping = matchingMethodOrConstructor.getAnnotation(RequestPath.class);

					String methodName = matchingMethodOrConstructor.getName();

					//					String methodPath = mapping.value() + Routes.this.suffix;
					String methodPath = mapping.value();

					methodPath = formatMethodPath(methodPath);
					String beanPath = classPathMap.get(matchingClass);

					if (StringUtils.isBlank(beanPath)) {
						log.error("方法有注解，但类没注解, method:{}, class:{}", methodName, matchingClass);
						errorStr.append("方法有注解，但类没注解, method:" + methodName + ", class:" + matchingClass + "\r\n\r\n");
						return;
					}

					Object bean = pathBeanMap.get(beanPath);
					String completeMethodPath = methodPath;
					if (beanPath != null) {
						completeMethodPath = beanPath + methodPath;
					}

					Class<?>[] parameterTypes = matchingMethodOrConstructor.getParameterTypes();
					Method method;
					try {
						method = matchingClass.getMethod(methodName, parameterTypes);

						Paranamer paranamer = new BytecodeReadingParanamer();
						String[] parameterNames = paranamer.lookupParameterNames(method, false); // will return null if not found

						Method checkMethod = pathMethodMap.get(completeMethodPath);
						if (checkMethod != null) {
							log.error("mapping[{}] already exists in method [{}]", completeMethodPath, checkMethod.getDeclaringClass() + "#" + checkMethod.getName());
							errorStr.append(
									"mapping[" + completeMethodPath + "] already exists in method [" + checkMethod.getDeclaringClass() + "#" + checkMethod.getName() + "]\r\n\r\n");

							return;
						}

						pathMethodMap.put(completeMethodPath, method);

						pathMethodstrMap.put(completeMethodPath, methodToStr(method, parameterNames));

						methodParamnameMap.put(method, parameterNames);
						methodParamtypeMap.put(method, parameterTypes);
						methodBeanMap.put(method, bean);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}
				}
			});

			fastClasspathScanner.scan();

			String pathClassMapStr = Json.toFormatedJson(pathClassMap);
			log.info("class  mapping\r\n{}", pathClassMapStr);
			//			log.info("classPathMap scan result :\r\n {}\r\n", Json.toFormatedJson(classPathMap));
			String pathMethodstrMapStr = Json.toFormatedJson(pathMethodstrMap);
			log.info("method mapping\r\n{}", pathMethodstrMapStr);
			//			log.info("methodParamnameMap scan result :\r\n {}\r\n", Json.toFormatedJson(methodParamnameMap));

			//
			processVariablePath();

			String variablePathMethodstrMapStr = Json.toFormatedJson(variablePathMethodstrMap);
			log.info("variable path mapping\r\n{}", variablePathMethodstrMapStr);

			if (writeMappingToFile) {
				try {
					FileUtil.writeString("/tio_mvc_path_class.json", pathClassMapStr, "utf-8");
					FileUtil.writeString("/tio_mvc_path_method.json", pathMethodstrMapStr, "utf-8");
					FileUtil.writeString("/tio_mvc_variablepath_method.json", variablePathMethodstrMapStr, "utf-8");

					if (errorStr.length() > 0) {
						FileUtil.writeString("/tio_error_mvc.txt", errorStr.toString(), "utf-8");
					}
				} catch (IOException e) {
					log.error(e.toString(), e);
				}
			}
		}
	}

	/**
	 * 处理有变量的路径
	 * @param pathMethodMap
	 */
	private void processVariablePath() {
		Set<Entry<String, Method>> set = pathMethodMap.entrySet();
		//		Set<String> forRemoved = new HashSet<>();
		for (Entry<String, Method> entry : set) {
			String path = entry.getKey();
			Method method = entry.getValue();
			if (StringUtils.contains(path, "{") && StringUtils.contains(path, "}")) {
				String[] pathUnits = StringUtils.split(path, "/");
				PathUnitVo[] pathUnitVos = new PathUnitVo[pathUnits.length];

				boolean isVarPath = false; //是否是带变量的路径
				for (int i = 0; i < pathUnits.length; i++) {
					PathUnitVo pathUnitVo = new PathUnitVo();
					String pathUnit = pathUnits[i];
					if (StringUtils.contains(pathUnit, "{") || StringUtils.contains(pathUnit, "}")) {
						if (StringUtils.startsWith(pathUnit, "{") && StringUtils.endsWith(pathUnit, "}")) {
							String[] xx = methodParamnameMap.get(method);
							String varName = StringUtils.substringBetween(pathUnit, "{", "}");
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
		VariablePathVo[] ret = variablePathMap.get(pathUnitCount);
		if (forceCreate && ret == null) {
			ret = new VariablePathVo[0];
			variablePathMap.put(pathUnitCount, ret);
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
		return (T) classBeanMap.get(clazz);
	}

	public static String getRequestPath(Class<?> clazz) {
		return classPathMap.get(clazz);
	}

	/**
	 * 
	 * @param pathUnitCount
	 * @param variablePathVo
	 */
	private void addVariablePathVo(Integer pathUnitCount, VariablePathVo variablePathVo) {
		VariablePathVo[] existValue = variablePathMap.get(pathUnitCount);
		if (existValue == null) {
			existValue = new VariablePathVo[] { variablePathVo };
			variablePathMap.put(pathUnitCount, existValue);
		} else {
			VariablePathVo[] newExistValue = new VariablePathVo[existValue.length + 1];
			System.arraycopy(existValue, 0, newExistValue, 0, existValue.length);
			newExistValue[newExistValue.length - 1] = variablePathVo;
			variablePathMap.put(pathUnitCount, newExistValue);
		}
		variablePathMethodstrMap.put(variablePathVo.getPath(), methodToStr(variablePathVo.getMethod(), methodParamnameMap.get(variablePathVo.getMethod())));
		//org.tio.http.server.mvc.Routes.methodParamnameMap
	}

	private String methodToStr(Method method, String[] parameterNames) {
		return method.getDeclaringClass().getName() + "." + method.getName() + "(" + ArrayUtil.join(parameterNames, ",") + ")";
		//		matchingClass.getName() + "." + method.getName() + "(" + ArrayUtil.join(parameterNames, ",") + ")"
	}

	@SuppressWarnings("unused")
	public Method getMethodByPath(String path, HttpRequest request) {
		Method method = pathMethodMap.get(path);
		if (method == null) {
			String[] pathUnitsOfRequest = StringUtils.split(path, "/"); // "/user/214" -- > ["user", "214"]
			VariablePathVo[] variablePathVos = variablePathMap.get(pathUnitsOfRequest.length);
			if (variablePathVos != null) {
				tag1: for (VariablePathVo variablePathVo : variablePathVos) {
					PathUnitVo[] pathUnitVos = variablePathVo.getPathUnits();
					tag2: for (int i = 0; i < pathUnitVos.length; i++) {
						PathUnitVo pathUnitVo = pathUnitVos[i];
						String pathUnitOfRequest = pathUnitsOfRequest[i];

						if (pathUnitVo.isVar()) {
							request.addParam(pathUnitVo.getPath(), pathUnitOfRequest);
						} else {
							if (!StringUtils.equals(pathUnitVo.getPath(), pathUnitOfRequest)) {
								continue tag1;
							}
						}
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

	/**
	 * 
	 * @param initPath
	 * @return
	 */
	private static String formatBeanPath(String initPath) {
		return initPath;
	}

	/**
	 * 
	 * @param initPath
	 * @return
	 */
	private static String formatMethodPath(String initPath) {
		return initPath;
	}

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 * @return the writeMappingToFile
	 */
	public boolean isWriteMappingToFile() {
		return writeMappingToFile;
	}

	/**
	 * @param writeMappingToFile the writeMappingToFile to set
	 */
	public void setWriteMappingToFile(boolean writeMappingToFile) {
		this.writeMappingToFile = writeMappingToFile;
	}
}
