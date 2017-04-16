package org.tio.core.task;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.PacketSendMode;
import org.tio.core.WriteCompletionHandler;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
import org.tio.core.intf.PacketWithSendModel;
import org.tio.core.threadpool.AbstractQueueRunnable;
import org.tio.core.utils.AioUtils;
import org.tio.core.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:19:18
 */
public class SendRunnable<SessionContext, P extends Packet, R> extends AbstractQueueRunnable<Object>
{

	private static final Logger log = LoggerFactory.getLogger(SendRunnable.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	/**
	 * 
	 * @param channelContext
	 * @param executor
	 * @author: tanyaowu
	 */
	public SendRunnable(ChannelContext<SessionContext, P, R> channelContext, Executor executor)
	{
		super(executor);
		this.channelContext = channelContext;
	}

	/**
	 * 清空消息队列
	 */
	public void clearMsgQueue()
	{
		msgQueue.clear();
	}

	/**
	 * 
	 * @param packet
	 * @param packetWithSendModel
	 * @author: tanyaowu
	 */
	public void sendPacket(P packet, PacketWithSendModel packetWithSendModel)
	{
		log.info("{}, 准备发送:{}", channelContext, packet.logstr());
		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, groupContext.getAioHandler());
		int packetCount = 1;
		sendByteBuffer(byteBuffer, packetCount, packet, packetWithSendModel);
	}

	/**
	 * 
	 * @param byteBuffer
	 * @param packetCount
	 * @param packets
	 * @param packetWithSendModel
	 * @author: tanyaowu
	 */
	public void sendByteBuffer(ByteBuffer byteBuffer, Integer packetCount, Object packets, PacketWithSendModel packetWithSendModel)
	{
		if (byteBuffer == null)
		{
			log.error("{},byteBuffer is null", channelContext);
			return;
		}

		if (!AioUtils.checkBeforeIO(channelContext))
		{
			return;
		}

		byteBuffer.flip();
		AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
		WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler = channelContext.getWriteCompletionHandler();
		try
		{
			writeCompletionHandler.getWriteSemaphore().acquire();
		} catch (InterruptedException e)
		{
			log.error(e.toString(), e);
		}
		if (packetWithSendModel != null)
		{
			asynchronousSocketChannel.write(byteBuffer, packetWithSendModel, writeCompletionHandler);
		} else
		{
			asynchronousSocketChannel.write(byteBuffer, packets, writeCompletionHandler);
		}

		channelContext.getStat().setLatestTimeOfSentPacket(SystemTimer.currentTimeMillis());
	}

	public void setChannelContext(ChannelContext<SessionContext, P, R> channelContext)
	{
		this.channelContext = channelContext;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(this.getClass().getSimpleName()).append(":");
		builder.append(channelContext.toString());
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void runTask()
	{
		int queueSize = msgQueue.size();
		if (queueSize == 0)
		{
			return;
		}
		if (queueSize >= 2000)
		{
			queueSize = 1000;
		}

		Object ele = null;
		GroupContext<SessionContext, P, R> groupContext = this.channelContext.getGroupContext();
		PacketSendMode packetSendMode = groupContext.getPacketSendMode();
		AioHandler<SessionContext, P, R> aioHandler = groupContext.getAioHandler();

		if (queueSize > 1)
		{
			ByteBuffer[] byteBuffers = new ByteBuffer[queueSize];
			int allBytebufferCapacity = 0;

			int packetCount = 0;
			List<Object> packets = new ArrayList<>();
			for (int i = 0; i < queueSize; i++)
			{
				if ((ele = msgQueue.poll()) != null)
				{
					boolean isPacket = ele instanceof Packet;
//					boolean isPacketWithSendModel = false;
//					if (!isPacket)
//					{
//						isPacketWithSendModel = ele instanceof PacketWithSendModel;
//					}
					PacketWithSendModel packetWithSendModel = null;
					P packet = null;
					if (isPacket)
					{
						packet = (P) ele;
						packets.add(packet);
					} else
					{
						packetWithSendModel = (PacketWithSendModel) ele;
						packet = (P) packetWithSendModel.getPacket();
						packets.add(packetWithSendModel);
					}

					ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, aioHandler);
					log.info("{}, 准备发送:{}", channelContext, packet.logstr());

					allBytebufferCapacity += byteBuffer.limit();
					packetCount++;
					byteBuffers[i] = byteBuffer;
				} else
				{
					break;
				}
			}

			ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
			byte[] dest = allByteBuffer.array();
			for (ByteBuffer byteBuffer : byteBuffers)
			{
				if (byteBuffer != null)
				{
					int length = byteBuffer.limit();
					int position = allByteBuffer.position();
					System.arraycopy(byteBuffer.array(), 0, dest, position, length);
					allByteBuffer.position(position + length);
				}
			}
			sendByteBuffer(allByteBuffer, packetCount, packets, null);
		} else
		{
			if ((ele = msgQueue.poll()) != null)
			{

				boolean isPacket = ele instanceof Packet;
//				boolean isPacketWithSendModel = false;
//				if (!isPacket)
//				{
//					isPacketWithSendModel = ele instanceof PacketWithSendModel;
//				}
				PacketWithSendModel packetWithSendModel = null;
				P packet = null;
				if (isPacket)
				{
					packet = (P) ele;
				} else
				{
					packetWithSendModel = (PacketWithSendModel) ele;
					packet = (P) packetWithSendModel.getPacket();
				}

				sendPacket(packet, packetWithSendModel);
			}
		}
	}

	private ByteBuffer getByteBuffer(P packet, GroupContext<SessionContext, P, R> groupContext, AioHandler<SessionContext, P, R> aioHandler)
	{
		ByteBuffer byteBuffer = packet.getPreEncodedByteBuffer();
		if (byteBuffer != null)
		{
			byteBuffer = byteBuffer.duplicate();
		} else
		{
			byteBuffer = aioHandler.encode(packet, groupContext, channelContext);
		}
		return byteBuffer;
	}

}
