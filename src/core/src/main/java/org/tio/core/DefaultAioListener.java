package org.tio.core;

import org.tio.client.intf.ClientAioListener;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 */
public class DefaultAioListener<SessionContext, P extends Packet, R> implements ClientAioListener<SessionContext, P, R>, ServerAioListener<SessionContext, P, R>
{
	/** 
	 * @see org.tio.core.intf.AioListener#onAfterConnected(org.tio.core.ChannelContext, boolean, boolean)
	 * 
	 * @param channelContext
	 * @param isConnected
	 * @param isReconnect
	 * @author: tanyaowu
	 * 2017年2月4日 下午9:40:14
	 * 
	 */
	@Override
	public void onAfterConnected(ChannelContext<SessionContext, P, R> channelContext, boolean isConnected, boolean isReconnect){}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, boolean)
	 * 
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @author: tanyaowu
	 * 2017年2月4日 下午9:40:14
	 * 
	 */
	@Override
	public void onAfterSent(ChannelContext<SessionContext, P, R> channelContext, P packet, boolean isSentSuccess){}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterReceived(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int)
	 * 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author: tanyaowu
	 * 2017年2月4日 下午9:40:14
	 * 
	 */
	@Override
	public void onAfterReceived(ChannelContext<SessionContext, P, R> channelContext, P packet, int packetSize){}

	/** 
	 * @see org.tio.core.intf.AioListener#onAfterClose(org.tio.core.ChannelContext, java.lang.Throwable, java.lang.String, boolean)
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isRemove
	 * @author: tanyaowu
	 * 2017年2月4日 下午9:40:14
	 * 
	 */
	@Override
	public void onAfterClose(ChannelContext<SessionContext, P, R> channelContext, Throwable throwable, String remark, boolean isRemove){}
}
