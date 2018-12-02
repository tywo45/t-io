/**
 * 
 */
package org.tio.core;

import org.tio.core.intf.Packet;

/**
 * @author tanyaowu
 *
 */
public interface PacketConverter {
	/**
	 * 
	 * @param packet
	 * @param channelContext 要发往的channelContext
	 * @return
	 * @author tanyaowu
	 */
	public Packet convert(Packet packet, ChannelContext channelContext);
}
