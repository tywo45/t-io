package org.tio.examples.showcase.client;

import java.util.HashMap;
import java.util.Map;

import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.examples.showcase.client.handler.GroupMsgRespHandler;
import org.tio.examples.showcase.client.handler.JoinGroupRespHandler;
import org.tio.examples.showcase.client.handler.LoginRespHandler;
import org.tio.examples.showcase.client.handler.P2PRespHandler;
import org.tio.examples.showcase.common.ShowcaseAbsAioHandler;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.examples.showcase.common.Type;
import org.tio.examples.showcase.common.intf.AbsShowcaseBsHandler;

/**
 * 
 * @author tanyaowu 
 * 2017年3月27日 上午12:18:11
 */
public class ShowcaseClientAioHandler extends ShowcaseAbsAioHandler implements ClientAioHandler<ShowcaseSessionContext, ShowcasePacket, Object>
{

	private static Map<Byte, AbsShowcaseBsHandler<?>> handlerMap = new HashMap<>();
	static
	{
		handlerMap.put(Type.GROUP_MSG_RESP, new GroupMsgRespHandler());
		handlerMap.put(Type.JOIN_GROUP_RESP, new JoinGroupRespHandler());
		handlerMap.put(Type.LOGIN_RESP, new LoginRespHandler());
		handlerMap.put(Type.P2P_RESP, new P2PRespHandler());
	}

	/** 
	 * 处理消息
	 */
	@Override
	public Object handler(ShowcasePacket packet, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws Exception
	{
		Byte type = packet.getType();
		AbsShowcaseBsHandler<?> showcaseBsHandler = handlerMap.get(type);
		showcaseBsHandler.handler(packet, channelContext);
		return null;
	}

	private static ShowcasePacket heartbeatPacket = new ShowcasePacket(Type.HEART_BEAT_REQ, null);

	/** 
	 * 此方法如果返回null，框架层面则不会发心跳；如果返回非null，框架层面会定时发本方法返回的消息包
	 */
	@Override
	public ShowcasePacket heartbeatPacket()
	{
		return heartbeatPacket;
	}
}
