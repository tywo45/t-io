package org.tio.core.intf;

import java.util.Map;

import org.tio.core.ChannelContext;
import org.tio.core.ClientAction;

/**
 * @author tanyaowu 
 * 2017年4月16日 下午6:41:03
 */
public interface ClientTraceHandler<SessionContext, P extends Packet, R>
{
	/**
	 * 
	 * @param channelContext
	 * @param clientAction
	 * @param packet
	 * @param msg
	 * @author: tanyaowu
	 */
	public void traceClient(ChannelContext<SessionContext, P, R> channelContext, ClientAction clientAction, Packet packet, Map<String, Object> msg);
}
