package org.tio.examples.im.server.handler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.utils.SystemTimer;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.packets.JoinGroupResult;
import org.tio.examples.im.common.packets.JoinReqBody;
import org.tio.examples.im.common.packets.JoinRespBody;

/**
 * 
 * 
 * @author tanyaowu 
 *
 */
public class JoinReqHandler implements ImBsHandlerIntf
{
	private static Logger log = LoggerFactory.getLogger(JoinReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception
	{
		if (packet.getBody() == null)
		{
			throw new Exception("body is null");
		}

		JoinReqBody reqBody = JoinReqBody.parseFrom(packet.getBody());
		
		String group = reqBody.getGroup();
		if (StringUtils.isBlank(group))
		{
			log.error("group is null,{}", channelContext);
			Aio.close(channelContext, "group is null when join group");
			return null;
		}

		Aio.bindGroup(channelContext, group);

		
		JoinGroupResult joinGroupResult = JoinGroupResult.JOIN_GROUP_RESULT_OK;
		JoinRespBody joinRespBody = JoinRespBody.newBuilder().setTime(SystemTimer.currentTimeMillis()).setResult(joinGroupResult).setGroup(group).build();
		byte[] body = joinRespBody.toByteArray();
		
		ImPacket respPacket = new ImPacket();
		respPacket.setCommand(Command.COMMAND_JOIN_GROUP_RESP);
		respPacket.setBody(body);
		
		Aio.send(channelContext, respPacket);
	
		return null;
	}
}
