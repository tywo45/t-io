package org.tio.server.intf;

import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 *
 */
public interface ServerAioListener<SessionContext, P extends Packet, R> extends AioListener<SessionContext, P, R> {

	/**
	 * 建立连接后触发的方法
	 * @param asynchronousSocketChannel
	 * @param aioServer
	 * @return false: 表示拒绝这个连接, true: 表示接受这个连接
	 *
	 * @author: tanyaowu
	 * 2016年12月20日 上午10:10:56
	 *
	 */
	//	void onAfterAccepted(AsynchronousSocketChannel asynchronousSocketChannel, AioServer<SessionContext, P, R> aioServer);
}
