package org.tio.examples.im.common.json;

import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @filename:	 com.talent.utils.json.JsonFilterUtils
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2010-4-11 上午08:26:07
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2010-4-11</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
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
