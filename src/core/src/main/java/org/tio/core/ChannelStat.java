package org.tio.core;

import java.util.concurrent.atomic.AtomicLong;

import org.tio.core.utils.SystemTimer;

/**
 * @author tanyaowu 
 * 2017年4月1日 下午2:17:35
 */
public class ChannelStat {
	/**
	 * 最近一次收到业务消息包的时间(一个完整的业务消息包，一部分消息不算)
	 */
	private long latestTimeOfReceivedPacket = SystemTimer.currentTimeMillis();

	/**
	 * 最近一次发送业务消息包的时间(一个完整的业务消息包，一部分消息不算)
	 */
	private long latestTimeOfSentPacket = SystemTimer.currentTimeMillis();

	/**
	 * ChannelContext对象创建的时间
	 */
	private long timeCreated = SystemTimer.currentTimeMillis();

	/**
	 * 第一次连接成功的时间
	 */
	private Long timeFirstConnected = null;

	/**
	 * 连接关闭的时间
	 */
	private long timeClosed = SystemTimer.currentTimeMillis();

	/**
	 * 进入重连队列时间
	 */
	private long timeInReconnQueue = SystemTimer.currentTimeMillis();

	/**
	 * 本连接已发送的字节数
	 */
	private AtomicLong sentBytes = new AtomicLong();

	/**
	 * 本连接已发送的packet数
	 */
	private AtomicLong sentPackets = new AtomicLong();

	/**
	 * 本连接已处理的字节数
	 */
	private AtomicLong handledBytes = new AtomicLong();

	/**
	 * 本连接已处理的packet数
	 */
	private AtomicLong handledPackets = new AtomicLong();

	/**
	 * 本连接已接收的字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();

	/**
	 * 本连接已接收的packet数
	 */
	private AtomicLong receivedPackets = new AtomicLong();

	/**
	 * @return the timeLatestReceivedMsg
	 */
	public long getLatestTimeOfReceivedPacket() {
		return latestTimeOfReceivedPacket;
	}

	/**
	 * @param timeLatestReceivedMsg the timeLatestReceivedMsg to set
	 */
	public void setLatestTimeOfReceivedPacket(long latestTimeOfReceivedPacket) {
		this.latestTimeOfReceivedPacket = latestTimeOfReceivedPacket;
	}

	/**
	 * @return the timeLatestSentMsg
	 */
	public long getLatestTimeOfSentPacket() {
		return latestTimeOfSentPacket;
	}

	/**
	 * @param timeLatestSentMsg the timeLatestSentMsg to set
	 */
	public void setLatestTimeOfSentPacket(long latestTimeOfSentPacket) {
		this.latestTimeOfSentPacket = latestTimeOfSentPacket;
	}

	/**
	 * @return the countSentByte
	 */
	public AtomicLong getSentBytes() {
		return sentBytes;
	}

	/**
	 * @param countSentByte the countSentByte to set
	 */
	public void setSentBytes(AtomicLong sentBytes) {
		this.sentBytes = sentBytes;
	}

	/**
	 * @return the countSentPacket
	 */
	public AtomicLong getSentPackets() {
		return sentPackets;
	}

	/**
	 * @param countSentPacket the countSentPacket to set
	 */
	public void setSentPackets(AtomicLong sentPackets) {
		this.sentPackets = sentPackets;
	}

	/**
	 * @return the countHandledByte
	 */
	public AtomicLong getHandledBytes() {
		return handledBytes;
	}

	/**
	 * @param countHandledByte the countHandledByte to set
	 */
	public void setHandledBytes(AtomicLong countHandledByte) {
		this.handledBytes = countHandledByte;
	}

	/**
	 * @return the countHandledPacket
	 */
	public AtomicLong getHandledPackets() {
		return handledPackets;
	}

	/**
	 * @param countHandledPacket the countHandledPacket to set
	 */
	public void setHandledPackets(AtomicLong handledPackets) {
		this.handledPackets = handledPackets;
	}

	/**
	 * @return the countReceivedByte
	 */
	public AtomicLong getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @param countReceivedByte the countReceivedByte to set
	 */
	public void setReceivedBytes(AtomicLong receivedBytes) {
		this.receivedBytes = receivedBytes;
	}

	/**
	 * @return the countReceivedPacket
	 */
	public AtomicLong getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @param countReceivedPacket the countReceivedPacket to set
	 */
	public void setReceivedPackets(AtomicLong receivedPackets) {
		this.receivedPackets = receivedPackets;
	}

	/**
	 * @return the timeClosed
	 */
	public long getTimeClosed() {
		return timeClosed;
	}

	/**
	 * @param timeClosed the timeClosed to set
	 */
	public void setTimeClosed(long timeClosed) {
		this.timeClosed = timeClosed;
	}

	/**
	 * @return the timeInReconnQueue
	 */
	public long getTimeInReconnQueue() {
		return timeInReconnQueue;
	}

	/**
	 * @param timeInReconnQueue the timeInReconnQueue to set
	 */
	public void setTimeInReconnQueue(long timeInReconnQueue) {
		this.timeInReconnQueue = timeInReconnQueue;
	}

	/**
	 * @return the timeCreated
	 */
	public long getTimeCreated() {
		return timeCreated;
	}

	/**
	 * @return the timeFirstConnected
	 */
	public Long getTimeFirstConnected() {
		return timeFirstConnected;
	}

	/**
	 * @param timeFirstConnected the timeFirstConnected to set
	 */
	public void setTimeFirstConnected(Long timeFirstConnected) {
		this.timeFirstConnected = timeFirstConnected;
	}

}
