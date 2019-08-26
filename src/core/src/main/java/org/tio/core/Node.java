package org.tio.core;

import java.util.Objects;

import org.tio.utils.hutool.StrUtil;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:07
 */
public class Node implements Comparable<Node> {
	private String	ip;
	private int		port;

	public Node(String ip, int port) {
		super();
		if (StrUtil.isBlank(ip)) {
			ip = "0.0.0.0";
		}

		this.setIp(ip);
		this.setPort(port);
	}

	@Override
	public int compareTo(Node other) {
		if (other == null) {
			return -1;
		}
		//		RemoteNode other = (RemoteNode) obj;

		if (Objects.equals(ip, other.getIp()) && Objects.equals(port, other.getPort())) {
			return 0;
		} else {
			return this.toString().compareTo(other.toString());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Node other = (Node) obj;
		return ip.equals(other.getIp()) && port == other.getPort();
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		return (ip + ":" + port).hashCode();
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ip).append(":").append(port);
		return builder.toString();
	}

}
