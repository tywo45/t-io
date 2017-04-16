package org.tio.examples.im.client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.exception.AioDecodeException;
import org.tio.examples.im.client.handler.AuthRespHandler;
import org.tio.examples.im.client.handler.ChatRespHandler;
import org.tio.examples.im.client.handler.HandshakeRespHandler;
import org.tio.examples.im.client.handler.ImAioHandlerIntf;
import org.tio.examples.im.client.handler.JoinRespHandler;
import org.tio.examples.im.common.CommandStat;
import org.tio.examples.im.common.ImPacket;
import org.tio.examples.im.common.ImSessionContext;
import org.tio.examples.im.common.packets.Command;
import org.tio.examples.im.common.utils.GzipUtils;

/**
 * 
 * @author tanyaowu 
 *
 */
public class ImClientAioHandler implements ClientAioHandler<ImSessionContext, ImPacket, Object>
{
	private static Logger log = LoggerFactory.getLogger(ImClientAioHandler.class);

	private static Map<Command, ImAioHandlerIntf> handlerMap = new HashMap<>();
	static
	{
		handlerMap.put(Command.COMMAND_AUTH_RESP, new AuthRespHandler());
		handlerMap.put(Command.COMMAND_CHAT_RESP, new ChatRespHandler());
		handlerMap.put(Command.COMMAND_JOIN_GROUP_RESP, new JoinRespHandler());
		handlerMap.put(Command.COMMAND_HANDSHAKE_RESP, new HandshakeRespHandler());
	}

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public ImClientAioHandler()
	{
	}

	/**
	 * @param args
	 *
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:13:15
	 * 
	 */
	public static void main(String[] args)
	{
	}

	/** 
	 * @see org.tio.core.intf.AioHandler#handler(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @throws Exception 
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public Object handler(ImPacket packet, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws Exception
	{
		Command command = packet.getCommand();
		ImAioHandlerIntf handler = handlerMap.get(command);
		if (handler != null)
		{
			Object obj = handler.handler(packet, channelContext);
			CommandStat.getCount(command).handled.incrementAndGet();
			return obj;
		} else
		{
			CommandStat.getCount(command).handled.incrementAndGet();
			log.warn("找不到对应的命令码[{}]处理类", command);
			return null;
		}

	}

	/** 
	 * @see org.tio.core.intf.AioHandler#encode(org.tio.core.intf.Packet)
	 * 
	 * @param packet
	 * @return
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ByteBuffer encode(ImPacket packet, GroupContext<ImSessionContext, ImPacket, Object> groupContext, ChannelContext<ImSessionContext, ImPacket, Object> channelContext)
	{
		if (packet.getCommand() == Command.COMMAND_HEARTBEAT_REQ)
		{
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put(ImPacket.HEARTBEAT_BYTE);
			return buffer;
		}
		if (packet.getCommand() == Command.COMMAND_HANDSHAKE_REQ)
		{
			ByteBuffer buffer = ByteBuffer.allocate(1);
			buffer.put(ImPacket.HANDSHAKE_BYTE);
			return buffer;
		}

		byte[] body = packet.getBody();
		int bodyLen = 0;
		boolean isCompress = false;
		boolean is4ByteLength = false;
		if (body != null)
		{
			bodyLen = body.length;

			if (bodyLen > 200)
			{
				try
				{
					byte[] gzipedbody = GzipUtils.gZip(body);
					if (gzipedbody.length < body.length)
					{
						log.error("压缩前:{}, 压缩后:{}", body.length, gzipedbody.length);
						body = gzipedbody;
						packet.setBody(gzipedbody);
						bodyLen = gzipedbody.length;
						isCompress = true;
					}
				} catch (IOException e)
				{
					log.error(e.getMessage(), e);
				}
			}

			if (bodyLen > Short.MAX_VALUE)
			{
				is4ByteLength = true;
			}
		}

		int allLen = ImPacket.LEAST_HEADER_LENGHT + bodyLen;

		ByteBuffer buffer = ByteBuffer.allocate(allLen);
		buffer.order(groupContext.getByteOrder());


		buffer.put(ImPacket.VERSION);
		buffer.put((byte) packet.getCommand().getNumber());
		buffer.put(isCompress ? (byte)1 : (byte)0);
		buffer.putInt(packet.getSynSeq());
		buffer.putShort((short)bodyLen);


		if (body != null)
		{
			buffer.put(body);
		}
		return buffer;
	}

	private static ImPacket handshakeRespPacket = new ImPacket(Command.COMMAND_HANDSHAKE_RESP);
	
	/** 
	 * @see org.tio.core.intf.AioHandler#decode(java.nio.ByteBuffer)
	 * 
	 * @param buffer
	 * @return
	 * @throws AioDecodeException
	 * @author: tanyaowu
	 * 2016年11月18日 上午9:37:44
	 * 
	 */
	@Override
	public ImPacket decode(ByteBuffer buffer, ChannelContext<ImSessionContext, ImPacket, Object> channelContext) throws AioDecodeException
	{
		ImSessionContext imSessionContext = channelContext.getSessionContext();
		byte firstbyte = buffer.get();
		
		if (!imSessionContext.isHandshaked())  //如果还没有握手，则先进行握手操作
		{
			if (ImPacket.HANDSHAKE_BYTE == firstbyte)
			{
				return handshakeRespPacket;
			} else
			{
				throw new AioDecodeException("还没握手");
			}
		}
		
		buffer.position(buffer.position() - 1);//位置复元
		
		
		int readableLength = buffer.limit() - buffer.position();

		int headerLength = ImPacket.LEAST_HEADER_LENGHT;
		ImPacket imPacket = null;
		firstbyte = buffer.get();
		byte version = firstbyte;
		
		if (readableLength < headerLength)
		{
			return null;
		}
		Byte code = buffer.get();
		Command command = Command.forNumber(code);  
		
		boolean isCompress = buffer.get() == 1;  //是否压缩了消息体
		int seq = buffer.getInt();               //同步序列号		
		
		int bodyLength = buffer.getShort();
		if (bodyLength > ImPacket.MAX_LENGTH_OF_BODY || bodyLength < 0)
		{
			throw new AioDecodeException("bodyLength [" + bodyLength + "] is not right, remote:" + channelContext.getClientNode());
		}

		

		//		@SuppressWarnings("unused")
		//		int reserve = buffer.getInt();//保留字段

		//		PacketMeta<ImPacket> packetMeta = new PacketMeta<>();
		int neededLength = headerLength + bodyLength;
		int test = readableLength - neededLength;
		if (test < 0) // 不够消息体长度(剩下的buffe组不了消息体)
		{
			//			packetMeta.setNeededLength(neededLength);
			return null;
		} else
		{
			imPacket = new ImPacket();
			imPacket.setCommand(command);

			if (seq != 0)
			{
				imPacket.setSynSeq(seq);
			}

			if (bodyLength > 0)
			{
				byte[] dst = new byte[bodyLength];
				buffer.get(dst);
				if (isCompress)
				{
					try
					{
						byte[] unGzippedBytes = GzipUtils.unGZip(dst);
						imPacket.setBody(unGzippedBytes);
						//						imPacket.setBodyLen(unGzippedBytes.length);
					} catch (IOException e)
					{
						throw new AioDecodeException(e);
					}
				} else
				{
					imPacket.setBody(dst);
					//					imPacket.setBodyLen(dst.length);
				}
			}

			//			packetMeta.setPacket(imPacket);
			return imPacket;

		}

	}

	private static ImPacket heartbeatPacket = new ImPacket(Command.COMMAND_HEARTBEAT_REQ);

	/** 
	 * @see org.tio.client.intf.ClientAioHandler#heartbeatPacket()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月6日 下午2:18:16
	 * 
	 */
	@Override
	public ImPacket heartbeatPacket()
	{
		return heartbeatPacket;
	}

}
