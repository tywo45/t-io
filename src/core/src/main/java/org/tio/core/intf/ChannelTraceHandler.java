package org.tio.core.intf;

import java.util.Map;

import org.tio.core.ChannelContext;
import org.tio.core.ChannelAction;

/**
 * @author tanyaowu 
 * 2017年4月16日 下午6:41:03
 */
public interface ChannelTraceHandler<SessionContext, P extends Packet, R> {
	/**
	 * 
	 * @param channelContext
	 * @param channelAction
	 * @param packet
	 * @param extmsg
	 * @author: tanyaowu
	 */
	public void traceChannel(ChannelContext<SessionContext, P, R> channelContext, ChannelAction channelAction, Packet packet, Map<String, Object> extmsg);
}
