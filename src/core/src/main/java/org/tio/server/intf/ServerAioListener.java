package org.tio.server.intf;

import org.tio.core.ChannelContext;
import org.tio.core.intf.AioListener;

/**
 *
 * @author tanyaowu
 *
 */
public interface ServerAioListener extends AioListener {

	/**
	 * 建立连接后触发的方法
	 * @param asynchronousSocketChannel
	 * @param tioServer
	 * @return false: 表示拒绝这个连接, true: 表示接受这个连接
	 *
	 * @author tanyaowu
	 * 2016年12月20日 上午10:10:56
	 *
	 */
	//	void onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, TioServer tioServer);

	/**
	 * 服务器检查到心跳超时时，会调用这个函数
	 * @param channelContext
	 * @param interval 已经多久没有收发消息了，单位：毫秒
	 * @param heartbeatTimeoutCount 心跳超时次数，第一次超时此值是1，以此类推。此值被保存在：channelContext.stat.heartbeatTimeoutCount
	 * @return 返回true，那么服务器则不关闭此连接；返回false，服务器将按心跳超时关闭该连接
	 */
	public boolean onHeartbeatTimeout(ChannelContext channelContext, Long interva, int heartbeatTimeoutCount);
}
