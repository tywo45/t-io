package org.tio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Node;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:31:16
 */
public class ClientChannelContext<SessionContext, P extends Packet, R> extends ChannelContext<SessionContext, P, R> {

	private String bindIp;

	private Integer bindPort;

	/**
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 *
	 * @author: tanyaowu
	 * 
	 */
	public ClientChannelContext(GroupContext<SessionContext, P, R> groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		super(groupContext, asynchronousSocketChannel);
	}

	/** 
	 * @see org.tio.core.ChannelContext#createClientNode(java.nio.channels.AsynchronousSocketChannel)
	 * 
	 * @param asynchronousSocketChannel
	 * @return
	 * @throws IOException 
	 * @author: tanyaowu
	 * 2016年12月6日 下午12:18:08
	 * 
	 */
	@Override
	public Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getLocalAddress();
		Node clientNode = new Node(inetSocketAddress.getHostString(), inetSocketAddress.getPort());
		return clientNode;
	}

	/**
	 * @return the bindIp
	 */
	public String getBindIp() {
		return bindIp;
	}

	/**
	 * @param bindIp the bindIp to set
	 */
	public void setBindIp(String bindIp) {
		this.bindIp = bindIp;
	}

	/**
	 * @return the bindPort
	 */
	public Integer getBindPort() {
		return bindPort;
	}

	/**
	 * @param bindPort the bindPort to set
	 */
	public void setBindPort(Integer bindPort) {
		this.bindPort = bindPort;
	}

}
