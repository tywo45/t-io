package org.tio.core.stat;

import java.util.concurrent.atomic.AtomicLong;

public class GroupStat
{
	/**
	 * 关闭了多少连接
	 */
	private AtomicLong closed = new AtomicLong();
	/**
	 * 接收到的消息包
	 */
	private AtomicLong receivedPacket = new AtomicLong();
	/**
	 * 接收到的消息字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();
	/**
	 * 处理了的消息包数
	 */
	private AtomicLong handledPacket = new AtomicLong();
	/**
	 * 发送了的消息包数
	 */
	private AtomicLong sentPacket = new AtomicLong();

	/**
	 * 发送了的字节数
	 */
	private AtomicLong sentBytes = new AtomicLong();

	/**
	 * @return the receivedPacket
	 */
	public AtomicLong getReceivedPacket()
	{
		return receivedPacket;
	}

	/**
	 * @return the handledPacket
	 */
	public AtomicLong getHandledPacket()
	{
		return handledPacket;
	}

	/**
	 * @return the sentPacket
	 */
	public AtomicLong getSentPacket()
	{
		return sentPacket;
	}

	/**
	 * @return the closed
	 */
	public AtomicLong getClosed()
	{
		return closed;
	}

	/**
	 * @param closed the closed to set
	 */
	public void setClosed(AtomicLong closed)
	{
		this.closed = closed;
	}

	/**
	 * @return the sentBytes
	 */
	public AtomicLong getSentBytes()
	{
		return sentBytes;
	}

	/**
	 * @return the receivedBytes
	 */
	public AtomicLong getReceivedBytes()
	{
		return receivedBytes;
	}

	/**
	 * @param receivedBytes the receivedBytes to set
	 */
	public void setReceivedBytes(AtomicLong receivedBytes)
	{
		this.receivedBytes = receivedBytes;
	}

}
