package org.tio.core;

import java.nio.ByteBuffer;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext.CloseCode;
import org.tio.core.WriteCompletionHandler.WriteCompletionVo;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.core.stat.IpStat;
import org.tio.utils.SystemTimer;

/**
 *
 * @author tanyaowu
 *
 */
public class WriteCompletionHandler implements CompletionHandler<Integer, WriteCompletionVo> {

	public static class WriteCompletionVo {
		private ByteBuffer byteBuffer = null;

		private Object obj = null;

		/**
		 * @param byteBuffer
		 * @param obj
		 * @author tanyaowu
		 */
		public WriteCompletionVo(ByteBuffer byteBuffer, Object obj) {
			super();
			this.byteBuffer = byteBuffer; //[pos=0 lim=212 cap=212]
			this.obj = obj;
		}
	}

	private static Logger log = LoggerFactory.getLogger(WriteCompletionHandler.class);

	private ChannelContext channelContext = null;

	private java.util.concurrent.Semaphore writeSemaphore = new Semaphore(1);

	public WriteCompletionHandler(ChannelContext channelContext) {
		this.channelContext = channelContext;
	}

	@Override
	public void completed(Integer result, WriteCompletionVo writeCompletionVo) {
		//		Object attachment = writeCompletionVo.getObj();
		ByteBuffer byteBuffer = writeCompletionVo.byteBuffer; //[pos=212 lim=212 cap=212]
		if (byteBuffer.hasRemaining()) {
			//			int iv = byteBuffer.capacity() - byteBuffer.position();
			log.info("{} {}/{} has sent", channelContext, byteBuffer.position(), byteBuffer.limit());
			channelContext.asynchronousSocketChannel.write(byteBuffer, writeCompletionVo, this);
			channelContext.stat.latestTimeOfSentByte = SystemTimer.currTime;
		} else {
			channelContext.stat.latestTimeOfSentPacket = SystemTimer.currTime;
			handle(result, null, writeCompletionVo);
		}

	}

	@Override
	public void failed(Throwable throwable, WriteCompletionVo writeCompletionVo) {
		//		Object attachment = writeCompletionVo.getObj();
		handle(0, throwable, writeCompletionVo);
	}

	/**
	 * @return the writeSemaphore
	 */
	public java.util.concurrent.Semaphore getWriteSemaphore() {
		return writeSemaphore;
	}

	/**
	 * 
	 * @param result
	 * @param throwable
	 * @param writeCompletionVo
	 * @author tanyaowu
	 */
	public void handle(Integer result, Throwable throwable, WriteCompletionVo writeCompletionVo) {
		this.writeSemaphore.release();
		Object attachment = writeCompletionVo.obj;//();
		TioConfig tioConfig = channelContext.tioConfig;
		boolean isSentSuccess = result > 0;

		if (isSentSuccess) {
			if (tioConfig.statOn) {
				tioConfig.groupStat.sentBytes.addAndGet(result);
				channelContext.stat.sentBytes.addAndGet(result);
			}

			if (tioConfig.ipStats.durationList != null && tioConfig.ipStats.durationList.size() > 0) {
				for (Long v : tioConfig.ipStats.durationList) {
					IpStat ipStat = (IpStat) channelContext.tioConfig.ipStats.get(v, channelContext);
					ipStat.getSentBytes().addAndGet(result);
				}
			}
		}

		try {
			boolean isPacket = attachment instanceof Packet;
			if (isPacket) {
				if (isSentSuccess) {
					if (tioConfig.ipStats.durationList != null && tioConfig.ipStats.durationList.size() > 0) {
						for (Long v : tioConfig.ipStats.durationList) {
							IpStat ipStat = (IpStat) channelContext.tioConfig.ipStats.get(v, channelContext);
							ipStat.getSentPackets().incrementAndGet();
						}
					}
				}
				handleOne(result, throwable, (Packet) attachment, isSentSuccess);
			} else {
				List<?> ps = (List<?>) attachment;
				for (Object obj : ps) {
					handleOne(result, throwable, (Packet) obj, isSentSuccess);
				}
			}

			if (!isSentSuccess) {
				Tio.close(channelContext, throwable, "写数据返回:" + result, CloseCode.WRITE_COUNT_IS_NEGATIVE);
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {

		}
	}

	/**
	 * 
	 * @param result
	 * @param throwable
	 * @param packet
	 * @param isSentSuccess
	 * @author tanyaowu
	 */
	public void handleOne(Integer result, Throwable throwable, Packet packet, Boolean isSentSuccess) {
		Meta meta = packet.getMeta();

		if (meta != null) {
			meta.setIsSentSuccess(isSentSuccess);
		}

		try {
			channelContext.processAfterSent(packet, isSentSuccess);
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}

	}

}
