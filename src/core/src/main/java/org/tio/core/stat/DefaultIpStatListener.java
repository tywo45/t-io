/**
 * 
 */
package org.tio.core.stat;

import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 *
 */
public class DefaultIpStatListener implements IpStatListener {

	public static final DefaultIpStatListener me = new DefaultIpStatListener();

	/**
	 * 
	 */
	private DefaultIpStatListener() {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onExpired(org.tio.core.TioConfig, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onExpired(TioConfig tioConfig, IpStat ipStat) {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onAfterConnected(org.tio.core.ChannelContext, boolean, boolean, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onDecodeError(org.tio.core.ChannelContext, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onDecodeError(ChannelContext channelContext, IpStat ipStat) {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onAfterSent(org.tio.core.ChannelContext, org.tio.core.intf.Packet, boolean, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onAfterDecoded(org.tio.core.ChannelContext, org.tio.core.intf.Packet, int, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onAfterReceivedBytes(org.tio.core.ChannelContext, int, org.tio.core.stat.IpStat)
	 */
	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes, IpStat ipStat) throws Exception {
	}

	/* (non-Javadoc)
	 * @see org.tio.core.stat.IpStatListener#onAfterHandled(org.tio.core.ChannelContext, org.tio.core.intf.Packet, org.tio.core.stat.IpStat, long)
	 */
	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, IpStat ipStat, long cost) throws Exception {
	}

}
