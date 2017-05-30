package org.tio.core;

import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.intf.Packet;
import org.tio.core.intf.PacketWithMeta;
import org.tio.core.stat.GroupStat;

/**
 * 
 * @author tanyaowu 
 *
 */
public class WriteCompletionHandler<SessionContext, P extends Packet, R> implements CompletionHandler<Integer, Object> {

	private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	private java.util.concurrent.Semaphore writeSemaphore = new Semaphore(1);

	public WriteCompletionHandler(ChannelContext<SessionContext, P, R> channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void completed(Integer result, Object attachment) {
		handle(result, null, attachment);
	}

	@Override
	public void failed(Throwable throwable, Object attachment) {
		handle(0, throwable, attachment);
	}

	/**
	 * 
	 * @param result
	 * @param throwable
	 * @param attachment Packet or PacketWithMeta or List<PacketWithMeta> or List<Packet>
	 * @author: tanyaowu
	 */
	public void handle(Integer result, Throwable throwable, Object attachment) {
		this.writeSemaphore.release();

		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		GroupStat groupStat = groupContext.getGroupStat();
		//		AioListener<SessionContext, P, R> aioListener = groupContext.getAioListener();
		boolean isSentSuccess = result > 0;

		if (isSentSuccess) {
			groupStat.getSentBytes().addAndGet(result);
		}

		int packetCount = 0;
		try {
			boolean isPacket = attachment instanceof Packet;
			boolean isPacketWithMeta = !isPacket && attachment instanceof PacketWithMeta;

			if (isPacket || isPacketWithMeta) {
				if (isSentSuccess) {
					groupStat.getSentPacket().incrementAndGet();
				}
				handleOne(result, throwable, attachment, isSentSuccess);
			} else {
				List<?> ps = (List<?>) attachment;
				if (isSentSuccess) {
					packetCount = ps.size();
					groupStat.getSentPacket().addAndGet(packetCount);
				}

				for (Object obj : ps) {
					handleOne(result, throwable, obj, isSentSuccess);
				}
			}

			if (!isSentSuccess) {
				Aio.close(channelContext, throwable, "写数据返回:" + result);
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {

		}
	}

	/**
	 * 
	 * @param result
	 * @param throwable
	 * @param obj PacketWithMeta or Packet
	 * @param isSentSuccess
	 * @author: tanyaowu
	 */
	@SuppressWarnings("unchecked")
	public void handleOne(Integer result, Throwable throwable, Object obj, Boolean isSentSuccess) {
		P packet = null;
		PacketWithMeta<P> packetWithMeta = null;

		boolean isPacket = obj instanceof Packet;
		if (isPacket) {
			packet = (P) obj;
		} else {
			packetWithMeta = (PacketWithMeta<P>) obj;
			packetWithMeta.setIsSentSuccess(isSentSuccess);
			packet = packetWithMeta.getPacket();
		}

		try {
			channelContext.processAfterSent(obj, isSentSuccess);
		} catch (Exception e) {
			log.error(e.toString(), e);
		}

	}

	/**
	 * @return the writeSemaphore
	 */
	public java.util.concurrent.Semaphore getWriteSemaphore() {
		return writeSemaphore;
	}

}
