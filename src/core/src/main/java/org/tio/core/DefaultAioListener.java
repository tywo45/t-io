package org.tio.core;

import org.tio.client.intf.ClientAioListener;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 * 
 * @author tanyaowu 
 */
public class DefaultAioListener<SessionContext, P extends Packet, R> implements ClientAioListener<SessionContext, P, R>, ServerAioListener<SessionContext, P, R> {
	/**
	 * 
	 * @param channelContext
	 * @param isConnected
	 * @param isReconnect
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterConnected(ChannelContext<SessionContext, P, R> channelContext, boolean isConnected, boolean isReconnect) {
	}

	/**
	 * 
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterReceived(ChannelContext<SessionContext, P, R> channelContext, P packet, int packetSize) {
	}

	/**
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isRemove
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterClose(ChannelContext<SessionContext, P, R> channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	/**
	 * 
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public void onAfterSent(ChannelContext<SessionContext, P, R> channelContext, P packet, boolean isSentSuccess) throws Exception {
	}
}
