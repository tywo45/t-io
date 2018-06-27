package org.tio.core.udp.intf;

import java.net.DatagramSocket;

import org.tio.core.udp.UdpPacket;

/**
 * @author tanyaowu
 * 2017年7月5日 下午2:46:47
 */
public interface UdpHandler {

	/**
	 *
	 * @param udpPacket
	 * @param datagramSocket
	 * @author tanyaowu
	 */
	public void handler(UdpPacket udpPacket, DatagramSocket datagramSocket);
}
