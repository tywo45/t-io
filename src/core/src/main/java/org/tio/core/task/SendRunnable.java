package org.tio.core.task;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelAction;
import org.tio.core.GroupContext;
import org.tio.core.WriteCompletionHandler;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.Packet;
import org.tio.core.intf.PacketWithMeta;
import org.tio.core.threadpool.AbstractQueueRunnable;
import org.tio.core.utils.AioUtils;
import org.tio.core.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月4日 上午9:19:18
 */
public class SendRunnable<SessionContext, P extends Packet, R> extends AbstractQueueRunnable<Object> {

	private static final Logger log = LoggerFactory.getLogger(SendRunnable.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	/**
	 * 
	 * @param channelContext
	 * @param executor
	 * @author: tanyaowu
	 */
	public SendRunnable(ChannelContext<SessionContext, P, R> channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
	}

	/**
	 * 清空消息队列
	 */
	public void clearMsgQueue() {
		Object p = null;
		while ((p = msgQueue.poll()) != null) {
			try {
				channelContext.processAfterSent(p, false);
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		}
	}

	/**
	 * 
	 * @param obj Packet or PacketWithMeta
	 * @author: tanyaowu
	 */
	@SuppressWarnings("unchecked")
	public void sendPacket(Object obj) {
		P packet = null;
		PacketWithMeta<P> packetWithMeta = null;

		boolean isPacket = obj instanceof Packet;
		if (isPacket) {
			packet = (P) obj;
		} else {
			packetWithMeta = (PacketWithMeta<P>) obj;
			packet = packetWithMeta.getPacket();
		}

		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		ByteBuffer byteBuffer = getByteBuffer(packet, groupContext, groupContext.getAioHandler());
		int packetCount = 1;

		if (isPacket) {
			sendByteBuffer(byteBuffer, packetCount, packet);
		} else {
			sendByteBuffer(byteBuffer, packetCount, packetWithMeta);
		}
	}

	/**
	 * 
	 */
	public boolean addMsg(Object obj) {
		if (this.isCanceled()) {
			log.error("{}, 任务已经取消，{}添加到发送队列失败", channelContext, obj);
			return false;
		}

		return msgQueue.add(obj);
	}

	/**
	 * 
	 * @param byteBuffer
	 * @param packetCount
	 * @param packets Packet or PacketWithMeta or List<PacketWithMeta> or List<Packet>
	 * @author: tanyaowu
	 */
	public void sendByteBuffer(ByteBuffer byteBuffer, Integer packetCount, Object packets) {
		if (byteBuffer == null) {
			log.error("{},byteBuffer is null", channelContext);
			return;
		}

		if (!AioUtils.checkBeforeIO(channelContext)) {
			return;
		}

		byteBuffer.flip();
		AsynchronousSocketChannel asynchronousSocketChannel = channelContext.getAsynchronousSocketChannel();
		WriteCompletionHandler<SessionContext, P, R> writeCompletionHandler = channelContext.getWriteCompletionHandler();
		try {
			//			long start = SystemTimer.currentTimeMillis();
			writeCompletionHandler.getWriteSemaphore().acquire();
			//			long end = SystemTimer.currentTimeMillis();
			//			long iv = end - start;
			//			if (iv > 100) {
			//				//log.error("{} 等发送锁耗时:{} ms", channelContext, iv);
			//			}

		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}
		asynchronousSocketChannel.write(byteBuffer, packets, writeCompletionHandler);

		channelContext.getStat().setLatestTimeOfSentPacket(SystemTimer.currentTimeMillis());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void runTask() {
		int queueSize = msgQueue.size();
		if (queueSize == 0) {
			return;
		}
		if (queueSize >= 2000) {
			queueSize = 1000;
		}

		//Packet or PacketWithMeta
		Object obj = null;
		P p = null;
		PacketWithMeta<P> packetWithMeta = null;
		GroupContext<SessionContext, P, R> groupContext = this.channelContext.getGroupContext();
		AioHandler<SessionContext, P, R> aioHandler = groupContext.getAioHandler();

		if (queueSize > 1) {
			ByteBuffer[] byteBuffers = new ByteBuffer[queueSize];
			int allBytebufferCapacity = 0;

			int packetCount = 0;
			List<Object> packets = new ArrayList<>(queueSize);
			for (int i = 0; i < queueSize; i++) {
				if ((obj = msgQueue.poll()) != null) {
					boolean isPacket = obj instanceof Packet;
					if (isPacket) {
						p = (P) obj;
						packets.add(p);
					} else {
						packetWithMeta = (PacketWithMeta<P>) obj;
						p = packetWithMeta.getPacket();
						packets.add(packetWithMeta);
					}

					ByteBuffer byteBuffer = getByteBuffer(p, groupContext, aioHandler);

					allBytebufferCapacity += byteBuffer.limit();
					packetCount++;
					byteBuffers[i] = byteBuffer;
				} else {
					break;
				}
			}

			ByteBuffer allByteBuffer = ByteBuffer.allocate(allBytebufferCapacity);
			byte[] dest = allByteBuffer.array();
			for (ByteBuffer byteBuffer : byteBuffers) {
				if (byteBuffer != null) {
					int length = byteBuffer.limit();
					int position = allByteBuffer.position();
					System.arraycopy(byteBuffer.array(), 0, dest, position, length);
					allByteBuffer.position(position + length);
				}
			}
			sendByteBuffer(allByteBuffer, packetCount, packets);
		} else {
			if ((obj = msgQueue.poll()) != null) {
				boolean isPacket = obj instanceof Packet;
				if (isPacket) {
					p = (P) obj;
					sendPacket(p);
				} else {
					packetWithMeta = (PacketWithMeta<P>) obj;
					p = packetWithMeta.getPacket();
					sendPacket(packetWithMeta);
				}
			}
		}
	}

	private ByteBuffer getByteBuffer(P packet, GroupContext<SessionContext, P, R> groupContext, AioHandler<SessionContext, P, R> aioHandler) {
		ByteBuffer byteBuffer = packet.getPreEncodedByteBuffer();
		if (byteBuffer != null) {
			byteBuffer = byteBuffer.duplicate();
		} else {
			byteBuffer = aioHandler.encode(packet, groupContext, channelContext);
		}
		return byteBuffer;
	}

}
