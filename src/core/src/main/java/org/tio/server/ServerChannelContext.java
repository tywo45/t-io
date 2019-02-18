package org.tio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Node;

/**
 *
 * @author tanyaowu
 *
 */
public class ServerChannelContext extends ChannelContext {

	/**
	 * @param groupContext
	 * @param asynchronousSocketChannel
	 *
	 * @author tanyaowu
	 * 2016年12月6日 下午12:17:59
	 *
	 */
	public ServerChannelContext(GroupContext groupContext, AsynchronousSocketChannel asynchronousSocketChannel) {
		super(groupContext, asynchronousSocketChannel);
	}

	/**
	 * 创建一个虚拟ChannelContext，主要用来模拟一些操作，真实场景中用得少
	 * @param groupContext
	 */
	public ServerChannelContext(GroupContext groupContext) {
		super(groupContext);
	}

	/**
	 * @see org.tio.core.ChannelContext#createClientNode(java.nio.channels.AsynchronousSocketChannel)
	 *
	 * @param asynchronousSocketChannel
	 * @return
	 * @throws IOException
	 * @author tanyaowu
	 * 2016年12月6日 下午12:18:08
	 *
	 */
	@Override
	public Node createClientNode(AsynchronousSocketChannel asynchronousSocketChannel) throws IOException {
		InetSocketAddress inetSocketAddress = (InetSocketAddress) asynchronousSocketChannel.getRemoteAddress();
		Node clientNode = new Node(inetSocketAddress.getHostString(), inetSocketAddress.getPort());
		return clientNode;
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean isServer() {
		return true;
	}

}
