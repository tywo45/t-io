package org.tio.examples.showcase.common.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 进群请求
 * @author tanyaowu 
 * 2017年3月25日 上午8:22:06
 */
public class JoinGroupReqBody extends BaseBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JoinGroupReqBody.class);
	
	private String group;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public JoinGroupReqBody()
	{
		
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 */
	public static void main(String[] args)
	{

	}
	
	/**
	 * @return the group
	 */
	public String getGroup()
	{
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(String group)
	{
		this.group = group;
	}
}
