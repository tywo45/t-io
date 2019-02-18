package org.tio.core.stat;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.BetweenFormater;
import org.tio.utils.hutool.BetweenFormater.Level;

/**
 * 这个是给服务器用的，主要用于监控IP情况，随时拉黑恶意攻击IP
 * @author tanyaowu
 * 2017年8月20日 下午8:02:41
 */
public class IpStat implements java.io.Serializable {

	private static final long serialVersionUID = -6942731710053482089L;

	private Date start = new Date();

	/**
	 * 当前统计了多久，单位：毫秒
	 */
	private long duration;

	/**
	 * 时长类型，单位：秒，譬如60，3600等
	 */
	private Long durationType;

	/**
	 * 客户端ip
	 */
	private String ip;

	/**
	 * 解码异常的次数
	 */
	private AtomicInteger decodeErrorCount = new AtomicInteger();

	/**
	 * 收到该IP连接请求的次数
	 */
	private AtomicInteger requestCount = new AtomicInteger();

	/**
	 * 本IP已发送的字节数
	 */
	private AtomicLong sentBytes = new AtomicLong();

	/**
	 * 本IP已发送的packet数
	 */
	private AtomicLong sentPackets = new AtomicLong();

	/**
	 * 本IP已处理的字节数
	 */
	private AtomicLong handledBytes = new AtomicLong();

	/**
	 * 本IP已处理的packet数
	 */
	private AtomicLong handledPackets = new AtomicLong();

	/**
	 * 处理消息包耗时，单位：毫秒
	 */
	private AtomicLong handledPacketCosts = new AtomicLong();

	/**
	 * 本IP已接收的字节数
	 */
	private AtomicLong receivedBytes = new AtomicLong();

	/**
	 * 本IP已接收了多少次TCP数据包
	 */
	private AtomicLong receivedTcps = new AtomicLong();

	/**
	 * 本IP已接收的packet数
	 */
	private AtomicLong receivedPackets = new AtomicLong();

	public IpStat(String ip, Long durationType) {
		this.ip = ip;
		this.durationType = durationType;
	}

	/**
	 * 平均每次TCP接收到的字节数，这个可以用来监控慢攻击，配置PacketsPerTcpReceive定位慢攻击
	 */
	public double getBytesPerTcpReceive() {
		if (receivedTcps.get() == 0) {
			return 0;
		}
		double ret = (double) receivedBytes.get() / (double) receivedTcps.get();
		return ret;
	}

	/**
	 * @return the decodeErrorCount
	 */
	public AtomicInteger getDecodeErrorCount() {
		return decodeErrorCount;
	}

	public long getDuration() {
		duration = SystemTimer.currTime - this.start.getTime();
		return duration;
	}

	/**
	 * @return the durationType
	 */
	public Long getDurationType() {
		return durationType;
	}

	/**
	 * @return the duration
	 */
	public String getFormatedDuration() {
		duration = SystemTimer.currTime - this.start.getTime();
		BetweenFormater betweenFormater = new BetweenFormater(duration, Level.MILLSECOND);
		return betweenFormater.format();
	}

	/**
	 * @return the countHandledByte
	 */
	public AtomicLong getHandledBytes() {
		return handledBytes;
	}

	/**
	 * @return the countHandledPacket
	 */
	public AtomicLong getHandledPackets() {
		return handledPackets;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * 平均每次TCP接收到的业务包数，这个可以用来监控慢攻击，此值越小越有攻击嫌疑
	 */
	public double getPacketsPerTcpReceive() {
		if (receivedTcps.get() == 0) {
			return 0;
		}
		double ret = (double) receivedPackets.get() / (double) receivedTcps.get();
		return ret;
	}

	/**
	 * @return the countReceivedByte
	 */
	public AtomicLong getReceivedBytes() {
		return receivedBytes;
	}

	/**
	 * @return the countReceivedPacket
	 */
	public AtomicLong getReceivedPackets() {
		return receivedPackets;
	}

	/**
	 * @return the receivedTcps
	 */
	public AtomicLong getReceivedTcps() {
		return receivedTcps;
	}

	/**
	 * @return the requestCount
	 */
	public AtomicInteger getRequestCount() {
		return requestCount;
	}

	/**
	 * @return the countSentByte
	 */
	public AtomicLong getSentBytes() {
		return sentBytes;
	}

	/**
	 * @return the countSentPacket
	 */
	public AtomicLong getSentPackets() {
		return sentPackets;
	}

	/**
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * @param durationType the durationType to set
	 */
	public void setDurationType(Long durationType) {
		this.durationType = durationType;
	}

	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	public AtomicLong getHandledPacketCosts() {
		return handledPacketCosts;
	}

	/**
	 * 处理packet平均耗时，单位：毫秒
	 * @return
	 */
	public double getHandledCostsPerPacket() {
		if (handledPackets.get() > 0) {
			return handledPacketCosts.get() / handledPackets.get();
		}
		return 0;
	}
}
