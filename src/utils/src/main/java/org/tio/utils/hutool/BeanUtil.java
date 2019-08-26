package org.tio.utils.hutool;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2018年8月5日 下午8:46:56
 */
public class BeanUtil {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(BeanUtil.class);

	/**
	 * 
	 * @author tanyaowu
	 */
	public BeanUtil() {
	}

	// --------------------------------------------------------------------------------------------------------- PropertyDescriptor
	/**
	 * 获得Bean字段描述数组
	 * 
	 * @param clazz Bean类
	 * @return 字段描述数组
	 * @throws IntrospectionException 获取属性异常
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws IntrospectionException {
		BeanInfo beanInfo;
		beanInfo = Introspector.getBeanInfo(clazz);
		return ArrayUtil.filter(beanInfo.getPropertyDescriptors(), new Filter<PropertyDescriptor>() {
			@Override
			public boolean accept(PropertyDescriptor t) {
				//过滤掉getClass方法
				return false == "class".equals(t.getName());
			}
		});
	}

	/**
	 * 获得字段名和字段描述Map，获得的结果会缓存在 {@link BeanInfoCache}中
	 * 
	 * @param clazz Bean类
	 * @param ignoreCase 是否忽略大小写
	 * @return 字段名和字段描述Map
	 * @throws IntrospectionException 获取属性异常
	 */
	public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws IntrospectionException {
		Map<String, PropertyDescriptor> map = BeanInfoCache.INSTANCE.getPropertyDescriptorMap(clazz, ignoreCase);
		if (null == map) {
			map = internalGetPropertyDescriptorMap(clazz, ignoreCase);
			BeanInfoCache.INSTANCE.putPropertyDescriptorMap(clazz, map, ignoreCase);
		}
		return map;
	}

	/**
	 * 获得字段名和字段描述Map。内部使用，直接获取Bean类的PropertyDescriptor
	 * 
	 * @param clazz Bean类
	 * @param ignoreCase 是否忽略大小写
	 * @return 字段名和字段描述Map
	 * @throws IntrospectionException 获取属性异常
	 */
	private static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws IntrospectionException {
		final PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
		final Map<String, PropertyDescriptor> map = ignoreCase ? new CaseInsensitiveMap<String, PropertyDescriptor>(propertyDescriptors.length, 1)
		        : new HashMap<String, PropertyDescriptor>((int) (propertyDescriptors.length), 1);

		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			map.put(propertyDescriptor.getName(), propertyDescriptor);
		}
		return map;
	}

	/**
	 * 获得Bean类属性描述，大小写敏感
	 * 
	 * @param clazz Bean类
	 * @param fieldName 字段名
	 * @return PropertyDescriptor
	 * @throws IntrospectionException 获取属性异常
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, final String fieldName) throws IntrospectionException {
		return getPropertyDescriptor(clazz, fieldName, false);
	}

	/**
	 * 获得Bean类属性描述
	 * 
	 * @param clazz Bean类
	 * @param fieldName 字段名
	 * @param ignoreCase 是否忽略大小写
	 * @return PropertyDescriptor
	 * @throws IntrospectionException 获取属性异常
	 */
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, final String fieldName, boolean ignoreCase) throws IntrospectionException {
		final Map<String, PropertyDescriptor> map = getPropertyDescriptorMap(clazz, ignoreCase);
		return (null == map) ? null : map.get(fieldName);
	}

}
