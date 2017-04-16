package org.tio.examples.showcase.common.packets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 进群响应
 * @author tanyaowu 
 * 2017年3月25日 上午8:22:06
 */
public class JoinGroupRespBody extends BaseBody
{
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JoinGroupRespBody.class);
	

	public static interface Code
	{
		Integer SUCCESS = 1;
		Integer FAIL = 2;
	}
	
	//进群结果，见Code接口，showcase为了简单易懂，都会返回成功
	private Integer code;
	
	//如果进群失败，需要提供一下msg
	private String msg;
	
	private String group;

	/**
	 * 
	 * @author: tanyaowu
	 */
	public JoinGroupRespBody()
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
