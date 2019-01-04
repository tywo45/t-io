package org.tio.client.intf;

import org.tio.core.ChannelContext;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:14:24
 */
public interface ClientAioHandler extends AioHandler {
	/**
	 * 创建心跳包
	 * @return
	 * @author tanyaowu
	 */
	Packet heartbeatPacket(ChannelContext channelContext);
}
