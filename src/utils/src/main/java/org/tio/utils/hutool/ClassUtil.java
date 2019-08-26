package org.tio.utils.hutool;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2018年8月5日 下午6:34:56
 */
public class ClassUtil {
	private static Logger log = LoggerFactory.getLogger(ClassUtil.class);

	/**
	 * {@code null}安全的获取对象类型
	 * 
	 * @param <T> 对象类型
	 * @param obj 对象，如果为{@code null} 返回{@code null}
	 * @return 对象类型，提供对象如果为{@code null} 返回{@code null}
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getClass(T obj) {
		return ((null == obj) ? null : (Class<T>) obj.getClass());
	}

	/**
	 * 获取类名
	 * 
	 * @param obj 获取类名对象
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 3.0.7
	 */
	public static String getClassName(Object obj, boolean isSimple) {
		if (null == obj) {
			return null;
		}
		final Class<?> clazz = obj.getClass();
		return getClassName(clazz, isSimple);
	}

	/**
	 * 获取类名<br>
	 * 类名并不包含“.class”这个扩展名<br>
	 * 例如：ClassUtil这个类<br>
	 * 
	 * <pre>
	 * isSimple为false: "com.xiaoleilu.hutool.util.ClassUtil"
	 * isSimple为true: "ClassUtil"
	 * </pre>
	 * 
	 * @param clazz 类
	 * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
	 * @return 类名
	 * @since 3.0.7
	 */
	public static String getClassName(Class<?> clazz, boolean isSimple) {
		if (null == clazz) {
			return null;
		}
		return isSimple ? clazz.getSimpleName() : clazz.getName();
	}

	/**
	 * 获得对象数组的类数组
	 * 
	 * @param objects 对象数组，如果数组中存在{@code null}元素，则此元素被认为是Object类型
	 * @return 类数组
	 */
	public static Class<?>[] getClasses(Object... objects) {
		Class<?>[] classes = new Class<?>[objects.length];
		Object obj;
		for (int i = 0; i < objects.length; i++) {
			obj = objects[i];
			classes[i] = (null == obj) ? Object.class : obj.getClass();
		}
		return classes;
	}

	/**
	 * 指定类是否与给定的类名相同
	 * 
	 * @param clazz 类
	 * @param className 类名，可以是全类名（包含包名），也可以是简单类名（不包含包名）
	 * @param ignoreCase 是否忽略大小写
	 * @return 指定类是否与给定的类名相同
	 * @since 3.0.7
	 */
	public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
		if (null == clazz || StrUtil.isBlank(className)) {
			return false;
		}
		if (ignoreCase) {
			return className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName());
		} else {
			return className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
		}
	}

	// ----------------------------------------------------------------------------------------- Method
	/**
	 * 获得指定类中的Public方法名<br>
	 * 去重重载的方法
	 * 
	 * @param clazz 类
	 * @return 方法名Set
	 */
	public static Set<String> getPublicMethodNames(Class<?> clazz) {
		HashSet<String> methodSet = new HashSet<String>();
		Method[] methodArray = getPublicMethods(clazz);
		for (Method method : methodArray) {
			String methodName = method.getName();
			methodSet.add(methodName);
		}
		return methodSet;
	}

	/**
	 * 获得本类及其父类所有Public方法
	 * 
	 * @param clazz 查找方法的类
	 * @return 过滤后的方法列表
	 */
	public static Method[] getPublicMethods(Class<?> clazz) {
		return clazz.getMethods();
	}

	/**
	 * 查找指定Public方法 如果找不到对应的方法或方法不为public的则返回<code>null</code>
	 * 
	 * @param clazz 类
	 * @param methodName 方法名
	 * @param paramTypes 参数类型
	 * @return 方法
	 * @throws SecurityException 无权访问抛出异常
	 */
	public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
		try {
			return clazz.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException ex) {
			return null;
		}
	}

	// ----------------------------------------------------------------------------------------- Field
	/**
	 * 查找指定类中的所有字段（包括非public字段）， 字段不存在则返回<code>null</code>
	 * 
	 * @param clazz 被查找字段的类
	 * @param fieldName 字段名
	 * @return 字段
	 * @throws SecurityException 安全异常
	 */
	public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
		if (null == clazz || StrUtil.isBlank(fieldName)) {
			return null;
		}
		try {
			return clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			// e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查找指定类中的所有字段（包括非public字段)
	 * 
	 * @param clazz 被查找字段的类
	 * @return 字段
	 * @throws SecurityException 安全异常
	 */
	public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
		if (null == clazz) {
			return null;
		}
		return clazz.getDeclaredFields();
	}

	/**
	 * 指定类是否为Public
	 * 
	 * @param clazz 类
	 * @return 是否为public
	 */
	public static boolean isPublic(Class<?> clazz) {
		if (null == clazz) {
			throw new NullPointerException("Class to provided is null.");
		}
		return Modifier.isPublic(clazz.getModifiers());
	}

	/**
	 * 指定方法是否为Public
	 * 
	 * @param method 方法
	 * @return 是否为public
	 */
	public static boolean isPublic(Method method) {
		if (null == method) {
			throw new NullPointerException("Method to provided is null.");
		}
		return isPublic(method.getDeclaringClass());
	}

	/**
	 * 指定类是否为非public
	 * 
	 * @param clazz 类
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Class<?> clazz) {
		return false == isPublic(clazz);
	}

	/**
	 * 指定方法是否为非public
	 * 
	 * @param method 方法
	 * @return 是否为非public
	 */
	public static boolean isNotPublic(Method method) {
		return false == isPublic(method);
	}

	/**
	 * 是否为静态方法
	 * 
	 * @param method 方法
	 * @return 是否为静态方法
	 */
	public static boolean isStatic(Method method) {
		return Modifier.isStatic(method.getModifiers());
	}

	/**
	 * 设置方法为可访问
	 * 
	 * @param method 方法
	 * @return 方法
	 */
	public static Method setAccessible(Method method) {
		if (null != method && false == method.isAccessible()) {
			method.setAccessible(true);
		}
		return method;
	}

	/**
	 * 是否为抽象类
	 * 
	 * @param clazz 类
	 * @return 是否为抽象类
	 */
	public static boolean isAbstract(Class<?> clazz) {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	/**
	 * 是否为标准的类<br>
	 * 这个类必须：
	 * <pre>
	 * 1、非接口 
	 * 2、非抽象类 
	 * 3、非Enum枚举 
	 * 4、非数组 
	 * 5、非注解 
	 * 6、非原始类型（int, long等）
	 * </pre>
	 * 
	 * @param clazz 类
	 * @return 是否为标准类
	 */
	public static boolean isNormalClass(Class<?> clazz) {
		return null != clazz //
		        && false == clazz.isInterface() //
		        && false == isAbstract(clazz) //
		        && false == clazz.isEnum() //
		        && false == clazz.isArray() //
		        && false == clazz.isAnnotation() //
		        && false == clazz.isSynthetic() //
		        && false == clazz.isPrimitive();//
	}

	/**
	 * 判断类是否为枚举类型
	 * @param clazz 类
	 * @return 是否为枚举类型
	 * @since 3.2.0
	 */
	public static boolean isEnum(Class<?> clazz) {
		return null == clazz ? false : clazz.isEnum();
	}

	/**
	 * 获取指定类型分的默认值<br>
	 * 默认值规则为：
	 * <pre>
	 * 1、如果为原始类型，返回0
	 * 2、非原始类型返回{@code null}
	 * </pre>
	 * 
	 * @param clazz 类
	 * @return 默认值
	 * @since 3.0.8
	 */
	public static Object getDefaultValue(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			if (long.class == clazz) {
				return 0L;
			} else if (int.class == clazz) {
				return 0;
			} else if (short.class == clazz) {
				return (short) 0;
			} else if (char.class == clazz) {
				return (char) 0;
			} else if (byte.class == clazz) {
				return (byte) 0;
			} else if (double.class == clazz) {
				return 0D;
			} else if (float.class == clazz) {
				return 0f;
			} else if (boolean.class == clazz) {
				return false;
			}
		}

		return null;
	}

	/**
	 * 获得默认值列表
	 * @param classes 值类型
	 * @return 默认值列表
	 * @since 3.0.9
	 */
	public static Object[] getDefaultValues(Class<?>... classes) {
		final Object[] values = new Object[classes.length];
		for (int i = 0; i < classes.length; i++) {
			values[i] = getDefaultValue(classes[i]);
		}
		return values;
	}

	/**
	 * 扫描包路径下所有的class文件
	 * 代码摘自网上（https://gitee.com/liuyueyi/quicksilver/blob/master/silver-file/src/main/java/com.hust.hui.quicksilver.file/PkgUtil.java），但作了不少改造
	 * @param pkg 形如：org.tio.core
	 * @param classScanHandler 
	 * @return
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 */
	public static void scanPackage(String pkg, ClassScanHandler classScanHandler) throws ClassNotFoundException, IOException {
		String pkgDirName = pkg.replace('.', '/');

		Enumeration<URL> urls = ClassUtil.class.getClassLoader().getResources(pkgDirName);
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				findClassesByFile(pkg, filePath, classScanHandler);
			} else if ("jar".equals(protocol)) {
				JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
				findClassesByJar(pkg, jar, classScanHandler);
			}
		}
	}

	/**
	 * 扫描包路径下的所有class文件
	 *
	 * @param pkg 包名
	 * @param pkgDir 包对应的绝对地址
	 * @throws ClassNotFoundException 
	 */
	private static void findClassesByFile(String pkg, String pkgDir, ClassScanHandler classScanHandler) throws ClassNotFoundException {
		File dir = new File(pkgDir);
		if (!dir.exists() || !dir.isDirectory()) {
			return;
		}

		File[] dirfiles = dir.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith("class"));

		if (dirfiles == null || dirfiles.length == 0) {
			return;
		}

		for (File f : dirfiles) {
			if (f.isDirectory()) {
				findClassesByFile(pkg + "." + f.getName(), pkgDir + "/" + f.getName(), classScanHandler);
				continue;
			}

			// 获取类名，干掉 ".class" 后缀
			String className = f.getName();
			className = className.substring(0, className.length() - 6);

			Class<?> clazz = loadClass(pkg + "." + className);
			if (clazz != null) {
				if (classScanHandler != null) {
					try {
						classScanHandler.handler(clazz);
					} catch (Exception e) {
						log.error(clazz.getName(), e);
					}
				}
			}
		}
	}

	/**
	 * 扫描包路径下的所有class文件
	 *
	 * @param pkgName 包名
	 * @param jar     jar文件
	 * @throws ClassNotFoundException 
	 */
	private static void findClassesByJar(String pkgName, JarFile jar, ClassScanHandler classScanHandler) throws ClassNotFoundException {
		String pkgDir = pkgName.replace(".", "/");

		Enumeration<JarEntry> entry = jar.entries();

		JarEntry jarEntry;
		String name, className;
		while (entry.hasMoreElements()) {
			jarEntry = entry.nextElement();

			name = jarEntry.getName();
			if (name.charAt(0) == '/') {
				name = name.substring(1);
			}

			if (jarEntry.isDirectory() || !name.startsWith(pkgDir) || !name.endsWith(".class")) {
				// 非指定包路径， 非class文件
				continue;
			}

			// 去掉后面的".class", 将路径转为package格式
			className = name.substring(0, name.length() - 6);
			Class<?> clazz = loadClass(className.replace("/", "."));
			if (clazz != null) {
				if (classScanHandler != null) {
					classScanHandler.handler(clazz);
				}
			}
		}
	}

	private static Class<?> loadClass(String fullClzName) throws ClassNotFoundException {
		return Thread.currentThread().getContextClassLoader().loadClass(fullClzName);
	}

	/**
	 * 是否为包装类型
	 * 
	 * @param clazz 类
	 * @return 是否为包装类型
	 */
	public static boolean isPrimitiveWrapper(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return BasicType.wrapperPrimitiveMap.containsKey(clazz);
	}

	/**
	 * 是否为基本类型（包括包装类和原始类）
	 * 
	 * @param clazz 类
	 * @return 是否为基本类型
	 */
	public static boolean isBasicType(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
	}

	/**
	 * 是否简单值类型或简单值类型的数组<br>
	 * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class及其数组
	 * 
	 * @param clazz 属性类
	 * @return 是否简单值类型或简单值类型的数组
	 */
	public static boolean isSimpleTypeOrArray(Class<?> clazz) {
		if (null == clazz) {
			return false;
		}
		return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
	}

	/**
	 * 是否为简单值类型<br>
	 * 包括：原始类型,、String、other CharSequence, a Number, a Date, a URI, a URL, a Locale or a Class.
	 * 
	 * @param clazz 类
	 * @return 是否为简单值类型
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return isBasicType(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz)
		        || clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || clazz.equals(Class.class);
	}

}
