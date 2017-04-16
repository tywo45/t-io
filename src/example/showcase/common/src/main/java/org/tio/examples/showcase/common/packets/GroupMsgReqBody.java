package org.tio.examples.showcase.common.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 群消息请求
 * @author tanyaowu 
 * 2017年3月25日 上午8:22:06
 */
public class GroupMsgReqBody extends BaseBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(GroupMsgReqBody.class);
	
	//消息内容，必填
	private String text;
	
	//发消息到哪个组，可以为空
	private String toGroup;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public GroupMsgReqBody()
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
	 * @return the text
	 */
	public String getText()
	{
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text)
	{
		this.text = text;
	}

	/**
	 * @return the toGroup
	 */
	public String getToGroup()
	{
		return toGroup;
	}

	/**
	 * @param toGroup the toGroup to set
	 */
	public void setToGroup(String toGroup)
	{
		this.toGroup = toGroup;
	}
}
