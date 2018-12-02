package org.tio.http.client;

import org.tio.client.intf.ClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 * 2018年7月8日 上午11:12:15
 */
public class HttpClientAioListener implements ClientAioListener {

	public HttpClientAioListener() {
	}

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		return;
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) {

	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
		@SuppressWarnings("unused")
		ClientHttpRequest request = (ClientHttpRequest) packet;
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
}
