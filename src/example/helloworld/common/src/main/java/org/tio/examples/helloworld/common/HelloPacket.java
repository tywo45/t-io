package org.tio.examples.helloworld.common;

import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 *
 */
public class HelloPacket extends Packet
{
	public static final int HEADER_LENGHT = 4;//消息头的长度
	public static final String CHARSET = "utf-8";
	private byte[] body;

	/**
	 * @return the body
	 */
	public byte[] getBody()
	{
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(byte[] body)
	{
		this.body = body;
	}
}
