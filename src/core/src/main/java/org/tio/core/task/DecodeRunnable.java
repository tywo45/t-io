package org.tio.core.task;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelAction;
import org.tio.core.GroupContext;
import org.tio.core.PacketHandlerMode;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.core.utils.SystemTimer;

/**
 * 解码
 * 
 * @author 谭耀武
 * 2012-08-09
 * 
 */
public class DecodeRunnable<SessionContext, P extends Packet, R> implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(DecodeRunnable.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	/**
	 * 上一次解码剩下的数据
	 */
	private ByteBuffer lastByteBuffer = null;

	/**
	 * 新收到的数据
	 */
	private ByteBuffer newByteBuffer = null;

	/**
	 * 
	 */
	public DecodeRunnable(ChannelContext<SessionContext, P, R> channelContext) {
		this.channelContext = channelContext;
	}

	/**
	 * 清空处理的队列消息
	 */
	public void clearMsgQueue() {
		lastByteBuffer = null;
		newByteBuffer = null;
	}

	//	/**
	//	 * 
	//	 * @param packets
	//	 * @param byteCount
	//	 */
	//	private void submit(P packet, int byteCount)
	//	{
	//		handler(channelContext, packet, byteCount);
	//	}

	/**
	 * 
	 * @param channelContext
	 * @param packet
	 * @param byteCount
	 * @author: tanyaowu
	 */
	public static <SessionContext, P extends Packet, R> void handler(ChannelContext<SessionContext, P, R> channelContext, P packet, int byteCount) {
		//		if (channelContext.isClosed() || channelContext.isRemoved())
		//		{
		//			log.error("{}, closed:{}, removed:{}, packet:{}, stack:{}", channelContext, channelContext.isClosed(), channelContext.isRemoved(), packet.logstr(), ThreadUtils.stackTrace());
		//			return;
		//		}

		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		PacketHandlerMode packetHandlerMode = groupContext.getPacketHandlerMode();

		HandlerRunnable<SessionContext, P, R> handlerRunnable = channelContext.getHandlerRunnable();
		if (packetHandlerMode == PacketHandlerMode.QUEUE) {

			handlerRunnable.addMsg(packet);
			groupContext.getTioExecutor().execute(handlerRunnable);
		} else {
			handlerRunnable.handler(packet);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	/** 
	 * @see java.lang.Runnable#run()
	 * 
	 * @author: tanyaowu
	 * 2017年3月21日 下午4:26:39
	 * 
	 */
	@Override
	public void run() {
		ByteBuffer byteBuffer = newByteBuffer;
		if (byteBuffer != null) {
			if (lastByteBuffer != null) {
				byteBuffer = ByteBufferUtils.composite(lastByteBuffer, byteBuffer);
				lastByteBuffer = null;
			}
		} else {
			return;
		}

		try {
			label_2: while (true) {
				int initPosition = byteBuffer.position();
				P packet = channelContext.getGroupContext().getAioHandler().decode(byteBuffer, channelContext);

				if (packet == null)// 数据不够，组不了包
				{
					//					if (log.isDebugEnabled())
					//					{
					//						log.debug("{},数据不够，组不了包", channelContext.toString());
					//					}
					lastByteBuffer = ByteBufferUtils.copy(byteBuffer, initPosition, byteBuffer.limit());
					return;
				} else //组包成功
				{
					channelContext.getStat().setLatestTimeOfReceivedPacket(SystemTimer.currentTimeMillis());

					int afterDecodePosition = byteBuffer.position();
					int len = afterDecodePosition - initPosition;

					//					if (len == 0)
					//					{
					//						String logstr = channelContext + "解码成功, " + packet.logstr() + "," + byteBuffer + " 但是却只消耗了0字节, 这有可能会导致死循环. " + ThreadUtils.stackTrace();
					//						log.error(logstr);
					//					}

					channelContext.getGroupContext().getGroupStat().getReceivedPacket().incrementAndGet();
					channelContext.getGroupContext().getGroupStat().getReceivedBytes().addAndGet(len);

					channelContext.getStat().getReceivedPackets().incrementAndGet();
					channelContext.getStat().getReceivedBytes().addAndGet(len);

					packet.setByteCount(len);
					handler(channelContext, packet, len);

					AioListener<SessionContext, P, R> aioListener = channelContext.getGroupContext().getAioListener();
					try {
						if (log.isInfoEnabled()) {
							log.info("{} 收到消息 {}", channelContext, packet.logstr());
						}
						aioListener.onAfterReceived(channelContext, packet, len);
					} catch (Exception e) {
						log.error(e.toString(), e);
					}

					int remainingLength = byteBuffer.limit() - byteBuffer.position();
					if (remainingLength > 0)//组包后，还剩有数据
					{
						if (log.isDebugEnabled()) {
							log.debug("{},组包后，还剩有数据:{}", channelContext, remainingLength);
						}
						continue label_2;
					} else//组包后，数据刚好用完
					{
						lastByteBuffer = null;
						log.debug("{},组包后，数据刚好用完", channelContext);
						return;
					}
				}
			}
		} catch (AioDecodeException e) {
			log.error(channelContext.toString() + "解码异常", e);
			Aio.close(channelContext, e, "解码异常:" + e.getMessage());
			return;
		}
	}

	/**
	 * @param newByteBuffer the newByteBuffer to set
	 */
	public void setNewByteBuffer(ByteBuffer newByteBuffer) {
		this.newByteBuffer = newByteBuffer;
	}

}
