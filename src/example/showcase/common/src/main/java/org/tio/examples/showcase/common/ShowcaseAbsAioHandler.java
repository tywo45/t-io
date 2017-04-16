package org.tio.examples.showcase.common;

import java.nio.ByteBuffer;

import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioHandler;

/**
 * 
 * @author tanyaowu 
 * 2017年3月27日 上午12:14:12
 */
public abstract class ShowcaseAbsAioHandler implements AioHandler<ShowcaseSessionContext, ShowcasePacket, Object>
{
	/**
	 * 编码：把业务消息包编码为可以发送的ByteBuffer
	 * 消息头：type + bodyLength
	 * 消息体：byte[]
	 */
	@Override
	public ByteBuffer encode(ShowcasePacket packet, GroupContext<ShowcaseSessionContext, ShowcasePacket, Object> groupContext, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext)
	{
		byte[] body = packet.getBody();
		int bodyLen = 0;
		if (body != null)
		{
			bodyLen = body.length;
		}

		//总长度是消息头的长度+消息体的长度
		int allLen = ShowcasePacket.HEADER_LENGHT + bodyLen;
		
		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		buffer.order(groupContext.getByteOrder());

		//写入消息类型
		buffer.put(packet.getType());
		//写入消息体长度
		buffer.putInt(bodyLen);

		//写入消息体
		if (body != null)
		{
			buffer.put(body);
		}
		return buffer;
	}

	/**
	 * 解码：把接收到的ByteBuffer，解码成应用可以识别的业务消息包
	 * 消息头：type + bodyLength
	 * 消息体：byte[]
	 */
	@Override
	public ShowcasePacket decode(ByteBuffer buffer, ChannelContext<ShowcaseSessionContext, ShowcasePacket, Object> channelContext) throws AioDecodeException
	{
		int readableLength = buffer.limit() - buffer.position();
		if (readableLength < ShowcasePacket.HEADER_LENGHT)
		{
			return null;
		}
		
		//消息类型
		byte type = buffer.get();
		
		int bodyLength = buffer.getInt();

		if (bodyLength < 0)
		{
			throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}

		int neededLength = ShowcasePacket.HEADER_LENGHT + bodyLength;
		int test = readableLength - neededLength;
		if (test < 0) // 不够消息体长度(剩下的buffe组不了消息体)
		{
			return null;
		} else
		{
			ShowcasePacket imPacket = new ShowcasePacket();
			imPacket.setType(type);
			if (bodyLength > 0)
			{
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				imPacket.setBody(dst);
			}
			return imPacket;
		}
	}
}
