package org.tio.json;

/**
 * 
 * @author tanyaowu 
 * 2017年4月16日 上午11:37:00
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

	//	/**
	//	 * 
	//	 * @param jsonConfig
	//	 */
	//	void processJsonConfig(JsonConfig jsonConfig);
}
