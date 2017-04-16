package org.tio.examples.im.server.handler;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.utils.SystemTimer;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.ChatReqBody;
import org.tio.examples.im.common.packets.ChatRespBody;
import org.tio.examples.im.common.packets.ChatType;
import org.tio.examples.im.common.packets.Command;

/**
 * 
 * 
 * @author tanyaowu 
 *
 */
public class ChatReqHandler implements ImBsHandlerIntf
{
	private static Logger log = LoggerFactory.getLogger(ChatReqHandler.class);

	@Override
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception
	{

		if (packet.getBody() == null)
		{
			throw new Exception("body is null");
		}

		ChatReqBody chatReqBody = ChatReqBody.parseFrom(packet.getBody());

		Integer fromId = 111;
		String fromNick = "test";

		Integer toId = chatReqBody.getToId();
		String toNick = chatReqBody.getToNick();
		String toGroup = chatReqBody.getGroup();

		if (chatReqBody != null)
		{

			ChatRespBody.Builder builder = ChatRespBody.newBuilder();
			builder.setType(chatReqBody.getType());
			builder.setText(chatReqBody.getText());
			builder.setFromId(fromId);
			builder.setFromNick(fromNick);
			builder.setToId(toId);
			builder.setToNick(toNick);
			builder.setGroup(toGroup);
			builder.setTime(SystemTimer.currentTimeMillis());
			ChatRespBody chatRespBody = builder.build();
			byte[] bodybyte = chatRespBody.toByteArray();

			ImPacket respPacket = new ImPacket();
			respPacket.setCommand(Command.COMMAND_CHAT_RESP);

			respPacket.setBody(bodybyte);

			if (Objects.equals(ChatType.CHAT_TYPE_PUBLIC, chatReqBody.getType()))
			{
				Aio.sendToGroup(channelContext.getGroupContext(), toGroup, respPacket);
			} else if (Objects.equals(ChatType.CHAT_TYPE_PRIVATE, chatReqBody.getType()))
			{
				if (toId != null)
				{
					Aio.sendToUser(channelContext.getGroupContext(), toId + "", respPacket);
				}
			}
		}
		return null;
	}
}
