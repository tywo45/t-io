/**
 * 
 */
package org.tio.examples.im.common.bs;

/**
 * 
 * @author tanyaowu 
 */

public class JoinNotifyRespBody extends org.tio.examples.im.common.bs.BaseRespBody
{
	private String group;
	private Integer userid;
	private String nick;
	
	private Integer allcount;   //所有客户端数(包括注册和没注册的)
	private Integer usercount;   //注册用户数
	
	
	public JoinNotifyRespBody()
	{
	}

}
