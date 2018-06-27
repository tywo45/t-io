package org.tio.core.intf;

import java.util.Map;

import org.tio.core.ChannelAction;
import org.tio.core.ChannelContext;

/**
 * @author tanyaowu
 * 2017年4月16日 下午6:41:03
 */
public interface ChannelTraceHandler {
	/**
	 *
	 * @param channelContext
	 * @param channelAction
	 * @param packet
	 * @param extmsg
	 * @author tanyaowu
	 */
	public void traceChannel(ChannelContext channelContext, ChannelAction channelAction, Packet packet, Map<String, Object> extmsg);
}
