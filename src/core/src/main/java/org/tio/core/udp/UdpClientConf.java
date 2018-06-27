package org.tio.core.udp;

import org.tio.core.Node;

/**
 * @author tanyaowu
 * 2017年7月5日 下午3:53:20
 */
public class UdpClientConf extends UdpConf {
	//	private static Logger log = LoggerFactory.getLogger(UdpClientConf.class);

	/**
	 * @param args
	 * @author tanyaowu
	 */
	public static void main(String[] args) {

	}

	/**
	 *
	 * @author tanyaowu
	 */
	public UdpClientConf(String serverip, int serverport, int timeout) {
		super(timeout);
		Node node = new Node(serverip, serverport);
		this.setServerNode(node);
		this.setTimeout(timeout);
	}

}
