/**
 * 
 */
package org.tio.examples.im.common.bs;
import org.tio.core.utils.SystemTimer;
/**
 * 
 * @filename:	 org.tio.examples.common.im.bs.BaseReqBody
 * @copyright:   Copyright (c)2010
 * @company:     talent
 * @author:      谭耀武
 * @version:     1.0
 * @create time: 2013年3月24日 下午4:42:52
 * @record
 * <table cellPadding="3" cellSpacing="0" style="width:600px">
 * <thead style="font-weight:bold;background-color:#e3e197">
 * 	<tr>   <td>date</td>	<td>author</td>		<td>version</td>	<td>description</td></tr>
 * </thead>
 * <tbody style="background-color:#ffffeb">
* 	<tr><td>2013年3月24日</td>	<td>谭耀武</td>	<td>1.0</td>	<td>create</td></tr>
 * </tbody>
 * </table>
 */
public class BaseReqBody
{
	
	private Long time;
	/**
	 * 
	 */
	public BaseReqBody()
	{
		time = SystemTimer.currentTimeMillis();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{}

	public Long getTime()
	{
		return time;
	}

	public void setTime(Long time)
	{
		this.time = time;
	}
}


