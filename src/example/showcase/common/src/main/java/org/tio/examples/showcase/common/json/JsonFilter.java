package org.tio.examples.showcase.common.json;

/**
 * java bean转化成json string的时候，需要过滤一些属性
 * @author tanyaowu 
 * 2017年3月26日 下午8:28:26
 */
public interface JsonFilter
{
	/**
	 * 
	 * @param name property name
	 * @param value the value of the property
	 * @return true while allow it to json string; otherwise false.
	 */
	boolean accept(String name, Object value);
}
