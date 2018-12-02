package org.tio.core.udp.task;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.udp.UdpConf;

/**
 * @author tanyaowu
 * 2017年7月5日 下午5:54:13
 */
public class UdpSendRunnable implements Runnable {
	private static Logger log = LoggerFactory.getLogger(UdpSendRunnable.class);

	private LinkedBlockingQueue<DatagramPacket> queue;

	private UdpConf udpConf;

	private boolean isStopped = false;

	private DatagramSocket datagramSocket;

	/**
	 *
	 * @author tanyaowu
	 */
	public UdpSendRunnable(LinkedBlockingQueue<DatagramPacket> queue, UdpConf udpConf, DatagramSocket datagramSocket) {
		this.queue = queue;
		this.udpConf = udpConf;
		this.datagramSocket = datagramSocket;
	}

	@Override
	public void run() {
		DatagramSocket datagramSocket = this.datagramSocket;
		while (!isStopped) {
			try {
				DatagramPacket datagramPacket = queue.take();
				if (datagramSocket == null) {
					datagramSocket = new DatagramSocket();
					datagramSocket.setSoTimeout(udpConf.getTimeout());
				}
				datagramSocket.send(datagramPacket);

			} catch (Throwable e) {
				log.error(e.toString(), e);
			} finally {
				if (queue.size() == 0) {
					if (this.datagramSocket == null && datagramSocket != null) {
						datagramSocket.close();
						datagramSocket = null;
					}
				}
			}
		}
	}

	public void stop() {
		isStopped = true;
	}
}
