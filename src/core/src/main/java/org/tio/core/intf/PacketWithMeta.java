package org.tio.core.intf;

import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:34:59
 */
public class PacketWithMeta<P extends Packet> {
	private P packet = null;
	private Boolean isSentSuccess = null;
	private CountDownLatch countDownLatch = null;

	/**
	 * 
	 * @param packet
	 * @param countDownLatch
	 * @author: tanyaowu
	 */
	public PacketWithMeta(P packet, CountDownLatch countDownLatch) {
		super();
		this.packet = packet;
		this.countDownLatch = countDownLatch;
		if (countDownLatch != null) {
			this.packet.setBlockSend(true);
		}
	}

	/**
	 * @return the packet
	 */
	public P getPacket() {
		return packet;
	}

	/**
	 * @return the isSentSuccess
	 */
	public Boolean getIsSentSuccess() {
		return isSentSuccess;
	}

	/**
	 * @param isSentSuccess the isSentSuccess to set
	 */
	public void setIsSentSuccess(Boolean isSentSuccess) {
		this.isSentSuccess = isSentSuccess;
	}

	/**
	 * @return the countDownLatch
	 */
	public java.util.concurrent.CountDownLatch getCountDownLatch() {
		return countDownLatch;
	}

}
