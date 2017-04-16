package org.tio.examples.im.server.handler;

import org.tio.core.ChannelContext;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;

/**
 * 
 * @author tanyaowu 
 *
 */
public interface ImBsHandlerIntf
{
	/**
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 下午1:08:45
	 *
	 */
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext)  throws Exception;
}
