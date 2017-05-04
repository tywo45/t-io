package org.tio.core;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

public class Node implements Comparable<Node> {
	private String ip;
	private int port;

	public Node(String ip, int port) {
		super();
		if (StringUtils.isBlank(ip) || "0:0:0:0:0:0:0:0".equals(ip)) {
			ip = "0.0.0.0";
		}

		this.setIp(ip);
		this.setPort(port);
	}

	@Override
	public int hashCode() {
		return (ip + ":" + port).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		Node other = (Node) obj;
		return ip.equals(other.getIp()) && port == other.getPort();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(ip).append(":").append(port);
		return builder.toString();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

}
