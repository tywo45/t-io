package org.tio.core.intf;

import org.tio.core.ChannelContext;

/**
 * @author tanyaowu 
 * 2017年5月8日 下午1:14:08
 */
public interface PacketListener {
	/**
	 * 
	 * @param channelContext
	 * @param packet
	 * @param isSentSuccess
	 * @throws Exception
	 * @author: tanyaowu
	 */
	void onAfterSent(ChannelContext<?, ?, ?> channelContext, Packet packet, boolean isSentSuccess) throws Exception;

}
