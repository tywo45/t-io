package org.tio.examples.showcase.common.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录请求
 * @author tanyaowu 
 * 2017年3月25日 上午8:22:06
 */
public class LoginReqBody extends BaseBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoginReqBody.class);
	
	private String loginname;
	private String password;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public LoginReqBody()
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
	 * @return the loginname
	 */
	public String getLoginname()
	{
		return loginname;
	}

	/**
	 * @param loginname the loginname to set
	 */
	public void setLoginname(String loginname)
	{
		this.loginname = loginname;
	}

	/**
	 * @return the password
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
}
