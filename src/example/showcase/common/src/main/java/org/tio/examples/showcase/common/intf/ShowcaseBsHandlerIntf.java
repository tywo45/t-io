package org.tio.examples.showcase.common.intf;

import org.tio.core.ChannelContext;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;

/**
 * 业务处理器接口
 * @author tanyaowu 
 * 2017年3月27日 下午9:52:42
 */
public interface ShowcaseBsHandlerIntf
{
	
	/**
	 * 
	 * @param packet
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public Object handler(ShowcasePacket packet, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext)  throws Exception;

}
