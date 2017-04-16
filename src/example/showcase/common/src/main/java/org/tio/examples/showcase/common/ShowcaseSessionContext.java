package org.tio.examples.showcase.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 一般生产项目中，都需要定义一个这样的SessionContext，用于保存连接的会话数据
 * @author tanyaowu 
 * 2017年3月25日 下午12:07:25
 */
public class ShowcaseSessionContext
{
	private static Logger log = LoggerFactory.getLogger(ShowcaseSessionContext.class);
	
	private String token = null;
	
	private String userid = null;
	
	/**
	 * 
	 * @author: tanyaowu
	 */
	public ShowcaseSessionContext()
	{
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args)
	{

	}

	/**
	 * @return the token
	 */
	public String getToken()
	{
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token)
	{
		this.token = token;
	}

	/**
	 * @return the userid
	 */
	public String getUserid()
	{
		return userid;
	}

	/**
	 * @param userid the userid to set
	 */
	public void setUserid(String userid)
	{
		this.userid = userid;
	}
}
