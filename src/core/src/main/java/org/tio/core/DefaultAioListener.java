package org.tio.core;

import org.tio.client.intf.ClientAioListener;
import org.tio.core.intf.Packet;
import org.tio.server.intf.ServerAioListener;

/**
 *
 * @author tanyaowu
 */
public class DefaultAioListener implements ClientAioListener, ServerAioListener {
	/**
	 *
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isRemove
	 * @author tanyaowu
	 */
	//	@Override
	//	public void onAfterClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	//	}

	/**
	 *
	 * @param channelContext
	 * @param isConnected
	 * @param isReconnect
	 * @author tanyaowu
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param packetSize
	 * @author tanyaowu
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) {
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 * @author tanyaowu
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) throws Exception {
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {
	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext channelContext, Long interval, int heartbeatTimeoutCount) {
		return false;
	}
}
