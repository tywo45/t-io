package org.tio.examples.im.common.json;

/**
 * java bean转化成json string的时候，需要过滤一些属性
 * @filename:	 com.talent.utils.json.JsonFilter
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2010-4-10 下午07:38:18
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
 * 	<tr><td>2010-4-10</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
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
