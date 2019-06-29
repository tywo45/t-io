package org.tio.core.udp;

import org.tio.core.Node;

/**
 * @author tanyaowu
 * 2017年7月5日 下午2:53:38
 */
public class UdpConf {

	private int		timeout		= 5000;
	private Node	serverNode	= null;
	private String	charset		= "utf-8";

	/**
	 *
	 * @author tanyaowu
	 */
	public UdpConf(int timeout) {
		this.setTimeout(timeout);
	}

	public String getCharset() {
		return charset;
	}

	public Node getServerNode() {
		return serverNode;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setServerNode(Node serverNode) {
		this.serverNode = serverNode;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
}
