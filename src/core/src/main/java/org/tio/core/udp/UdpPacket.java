package org.tio.core.udp;

import org.tio.core.Node;
import org.tio.utils.SystemTimer;

/**
 * @author tanyaowu
 * 2017年7月5日 下午4:46:24
 */
public class UdpPacket {

	private byte[]	data;
	/**
	 * 对端Node
	 */
	private Node	remote;
	/**
	 * 收到消息的时间
	 */
	private long	time	= SystemTimer.currTime;

	/**
	 *
	 * @author tanyaowu
	 */
	public UdpPacket() {
	}

	public UdpPacket(byte[] data, Node remote) {
		super();
		this.data = data;
		this.remote = remote;
	}

	public byte[] getData() {
		return data;
	}

	public Node getRemote() {
		return remote;
	}

	public long getTime() {
		return time;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public void setRemote(Node remote) {
		this.remote = remote;
	}

	public void setTime(long time) {
		this.time = time;
	}
}
