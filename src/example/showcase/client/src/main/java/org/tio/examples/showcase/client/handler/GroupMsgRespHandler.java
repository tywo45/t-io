package org.tio.examples.showcase.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.examples.showcase.common.ShowcasePacket;
import org.tio.examples.showcase.common.ShowcaseSessionContext;
import org.tio.examples.showcase.common.intf.AbsShowcaseBsHandler;
import org.tio.examples.showcase.common.packets.GroupMsgRespBody;
import org.tio.json.Json;

/**
 * @author tanyaowu 
 * 2017年3月27日 下午9:51:28
 */
public class GroupMsgRespHandler extends AbsShowcaseBsHandler<GroupMsgRespBody>
{
	private static Logger log = LoggerFactory.getLogger(GroupMsgRespHandler.class);

	/**
	 * 
	 * @author: tanyaowu
	 */
	public GroupMsgRespHandler()
	{
	}

	/**
	 * @param args
	 * @author: tanyaowu
	 */
	public static void main(String[] args)
	{

	}
	/** 
	 * @return
	 * @author: tanyaowu
	 */
	@Override
	public Class<GroupMsgRespBody> bodyClass()
	{
		return GroupMsgRespBody.class;
	}

	/** 
	 * @param packet
	 * @param bsBody
	 * @param channelContext
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	@Override
	public Object handler(ShowcasePacket packet, GroupMsgRespBody bsBody, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws Exception
	{
		System.out.println("收到群组消息:" + Json.toJson(bsBody));
		return null;
	}
}
