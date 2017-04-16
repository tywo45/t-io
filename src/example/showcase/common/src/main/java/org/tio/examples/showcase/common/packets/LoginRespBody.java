package org.tio.examples.showcase.common.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登录响应
 * @author tanyaowu 
 * 2017年3月25日 上午8:39:02
 */
public class LoginRespBody extends BaseBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(LoginRespBody.class);
	
	public static interface Code
	{
		Integer SUCCESS = 1;
		Integer FAIL = 2;
	}
	
	private String token;
	
	private Integer code;
	private String msg;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public LoginRespBody()
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
	 * @return the code
	 */
	public Integer getCode()
	{
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code)
	{
		this.code = code;
	}

	/**
	 * @return the msg
	 */
	public String getMsg()
	{
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg)
	{
		this.msg = msg;
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
}
