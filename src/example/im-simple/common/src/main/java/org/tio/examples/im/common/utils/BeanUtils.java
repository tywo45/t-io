/**
 * 
 */
package org.tio.examples.im.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class BeanUtils.
 *
 * @filename:  com.talent.utils.BeanUtils
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2016年4月26日 上午10:24:18
 * @record <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2016年4月26日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BeanUtils
{
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(BeanUtils.class);

	/**
	 * Instantiates a new bean utils.
	 */
	public BeanUtils()
	{

	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception
	{
		
	}

	/**
	 * Bean to map.
	 *
	 * @param src the src
	 * @return the map
	 * @author: tanyaowu
	 * 2016年5月10日 下午1:41:53
	 */
	public static Map<String, Object> beanToMap(Object src)
	{

		if (src == null)
		{
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try
		{
			BeanInfo beanInfo = Introspector.getBeanInfo(src.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors)
			{
				String key = property.getName();
				// 过滤class属性  
				if (!"class".equals(key))
				{
					// 得到property对应的getter方法  
					Method getter = property.getReadMethod();
					Object value = getter.invoke(src);
					map.put(key, value);
				}
			}
		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
		return map;
	}

	/**
	 * Copy properties.
	 *
	 * @param desc 不允许为空
	 * @param src 不允许为空
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void copyProperties(Object desc, Object src)
	{
		BeanUtilsBean2 beanUtilsBean2 = new BeanUtilsBean2();
		try
		{
			if (desc instanceof Map)
			{
				Map<String, Object> map = beanToMap(src);
				((Map) desc).putAll(map);
			} else
			{
				beanUtilsBean2.copyProperties(desc, src);
			}

		} catch (Exception e)
		{
			log.error(e.getMessage(), e);
		}
	}

}
