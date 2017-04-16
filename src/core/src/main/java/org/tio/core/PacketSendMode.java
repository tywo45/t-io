package org.tio.core;

/**
 *  消息发送模式
 * @author tanyaowu 
 *
 */
public enum PacketSendMode
{
	/**
	 * 阻塞发送，直接发送成功，才折回
	 */
	BLOCK(1), 
	
	/**
	 * 把packet丢到一个队列中，让线程池去发送
	 */
	QUEUE(2);

	private final int value;

	public static PacketSendMode forNumber(int value)
	{
		switch (value)
		{
		case 1:
			return BLOCK;
		case 2:
			return QUEUE;
		default:
			return null;
		}
	}

	private PacketSendMode(int value)
	{
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue()
	{
		return value;
	}
}
