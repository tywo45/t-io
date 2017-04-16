package org.tio.json;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author tanyaowu 
 * 2017年4月16日 上午11:37:06
 */
public class JsonFilterUtils
{

	/**
	 * 不追溯父类
	 * @param clazz
	 * @param classes
	 * @return
	 */
	public static boolean containClasses(Class<?> clazz, Class<?>[] classes)
	{
		if (classes == null || classes.length == 0)
		{
			return false;
		}

		boolean isContain = ArrayUtils.contains(classes, clazz);
		if (isContain)
		{
			return true;
		}

		Class<?>[] interfaces = clazz.getInterfaces();
		for (int i = 0; i < interfaces.length; i++)
		{
			isContain = ArrayUtils.contains(classes, interfaces[i]);
			if (isContain)
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * 会追溯父类
	 * @param name property name
	 * @param value the value of the property
	 * @param classes
	 * @return true if classes contain value'class
	 */
	public static boolean containClasses(String name, Object value, Class<?>[] classes)
	{
		if (value == null || (classes == null || classes.length == 0))
		{
			return false;
		}
		return containClassesWithAscend(value.getClass(), classes);
	}

	/**
	 * 追溯所有父类
	 * @param clazz
	 * @param classes
	 * @return
	 */
	public static boolean containClassesWithAscend(Class<?> clazz, Class<?>[] classes)
	{
		if (clazz == null || (classes == null || classes.length == 0))
		{
			return false;
		}

		boolean isContain = containClasses(clazz, classes);
		if (isContain)
		{
			return true;
		}

		//追溯superclass
		Class<?> superClass = clazz.getSuperclass();
		while (superClass != null)
		{
			isContain = containClasses(superClass, classes);
			if (isContain)
			{
				return true;
			}
			superClass = superClass.getSuperclass();
		}
		return false;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

	}
}
