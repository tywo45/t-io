package org.tio.core.task;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelAction;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;
import org.tio.core.stat.ChannelStat;
import org.tio.core.stat.IpStat;
import org.tio.core.utils.ByteBufferUtils;
import org.tio.utils.SystemTimer;
import org.tio.utils.thread.pool.ISynRunnable;

/**
 * 解码任务对象，一个连接对应一个本对象
 *
 * @author 谭耀武
 * 2012-08-09
 */
public class DecodeRunnable implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(DecodeRunnable.class);

	/**
	 *
	 * @param packet
	 * @param byteCount
	 * @author tanyaowu
	 */
	public void handler(Packet packet, int byteCount) {
		//		if (packetHandlerMode == PacketHandlerMode.QUEUE) {
		//			handlerRunnable.addMsg(packet);
		//			groupContext.getTioExecutor().execute(handlerRunnable);
		//		} else {
		//			handlerRunnable.handler(packet);
		//		}

		switch (groupContext.packetHandlerMode) {
		case QUEUE:
			channelContext.handlerRunnable.addMsg(packet);
			groupContext.tioExecutor.execute(channelContext.handlerRunnable);
			break;
		default:
			channelContext.handlerRunnable.handler(packet);
			break;
		}
	}

	private ChannelContext channelContext = null;

	private GroupContext groupContext = null;

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
	public DecodeRunnable(ChannelContext channelContext) {
		this.channelContext = channelContext;
		this.groupContext = channelContext.groupContext;
	}

	/**
	 * 清空处理的队列消息
	 */
	public void clearMsgQueue() {
		lastByteBuffer = null;
		newByteBuffer = null;
	}

	/**
	 * @see java.lang.Runnable#run()
	 *
	 * @author tanyaowu
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

		label_2: while (true) {
			try {
				int initPosition = byteBuffer.position();
				int limit = byteBuffer.limit();
				int readableLength = limit - initPosition;
				Packet packet = null;
				if (channelContext.packetNeededLength != null) {
					log.info("{}, 解码所需长度:{}", channelContext, channelContext.packetNeededLength);
					if (readableLength >= channelContext.packetNeededLength) {
						packet = groupContext.getAioHandler().decode(byteBuffer, limit, initPosition, readableLength, channelContext);
					}
				} else {
					packet = groupContext.getAioHandler().decode(byteBuffer, limit, initPosition, readableLength, channelContext);
				}

				if (packet == null)// 数据不够，解不了码
				{
					lastByteBuffer = ByteBufferUtils.copy(byteBuffer, initPosition, limit);
					ChannelStat channelStat = channelContext.stat;
					channelStat.decodeFailCount++;
					//					int len = byteBuffer.limit() - initPosition;
					log.debug("{} 本次解码失败, 已经连续{}次解码失败，参与解码的数据长度共{}字节", channelContext, channelStat.decodeFailCount, readableLength);
					if (channelStat.decodeFailCount > 5) {
						if (channelContext.packetNeededLength == null) {
							log.info("{} 本次解码失败, 已经连续{}次解码失败，参与解码的数据长度共{}字节", channelContext, channelStat.decodeFailCount, readableLength);
						}

						//检查慢包攻击（只有自用版才有）
						if (channelStat.decodeFailCount > 10) {
							int capacity = lastByteBuffer.capacity();
							int per = capacity / channelStat.decodeFailCount;
							if (per < Math.min(groupContext.readBufferSize / 2, 256)) {
								throw new AioDecodeException("连续解码" + channelStat.decodeFailCount + "次都不成功，并且平均每次接收到的数据为" + per + "字节，有慢攻击的嫌疑");
							}
						}
					}
					return;
				} else //解码成功
				{
					channelContext.setPacketNeededLength(null);
					channelContext.stat.latestTimeOfReceivedPacket = SystemTimer.currentTimeMillis();

					ChannelStat channelStat = channelContext.stat;
					channelStat.decodeFailCount = 0;

					int afterDecodePosition = byteBuffer.position();
					int len = afterDecodePosition - initPosition;
					packet.setByteCount(len);

					groupContext.groupStat.receivedPackets.incrementAndGet();
					channelContext.stat.receivedPackets.incrementAndGet();

					if (groupContext.ipStats.durationList != null && groupContext.ipStats.durationList.size() > 0) {
						try {
							for (Long v : groupContext.ipStats.durationList) {
								IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
								ipStat.getReceivedPackets().incrementAndGet();
								groupContext.getIpStatListener().onAfterDecoded(channelContext, packet, len, ipStat);
							}
						} catch (Exception e1) {
							log.error(packet.logstr(), e1);
						}
					}
					channelContext.traceClient(ChannelAction.RECEIVED, packet, null);

					AioListener aioListener = groupContext.getAioListener();
					try {
						if (log.isDebugEnabled()) {
							log.debug("{} 收到消息 {}", channelContext, packet.logstr());
						}
						aioListener.onAfterDecoded(channelContext, packet, len);
					} catch (Throwable e) {
						log.error(e.toString(), e);
					}

					if (log.isDebugEnabled()) {
						log.debug("{}, 解包获得一个packet:{}", channelContext, packet.logstr());
					}
					handler(packet, len);

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
			} catch (Throwable e) {
				channelContext.setPacketNeededLength(null);
				log.error(channelContext + ", " + byteBuffer + ", 解码异常:" + e.toString(), e);

				if (e instanceof AioDecodeException) {
					List<Long> list = groupContext.ipStats.durationList;
					if (list != null && list.size() > 0) {
						try {
							for (Long v : list) {
								IpStat ipStat = groupContext.ipStats.get(v, channelContext.getClientNode().getIp());
								ipStat.getDecodeErrorCount().incrementAndGet();
								groupContext.getIpStatListener().onDecodeError(channelContext, ipStat);
							}
						} catch (Exception e1) {
							log.error(e1.toString(), e1);
						}
					}
				}

				Tio.close(channelContext, e, "解码异常:" + e.getMessage());
				return;
			}
		}
	}

	/**
	 * @param newByteBuffer the newByteBuffer to set
	 */
	public void setNewByteBuffer(ByteBuffer newByteBuffer) {
		this.newByteBuffer = newByteBuffer;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	public static void main(String[] args) {
		//		ReadWriteLock runningLock = new ReentrantReadWriteLock();
		long start = System.currentTimeMillis();
		ISynRunnable runnable = new MyISynRunnable();
		ConcurrentLinkedQueue<String> msgQueue = new ConcurrentLinkedQueue<>();

		for (int i = 0; i < 4000000; i++) {
			msgQueue.add(i + "");
			if (i > 10000) {
				msgQueue.poll();
			}
			if (runnable instanceof ISynRunnable) {
				ISynRunnable synRunnable = (ISynRunnable) runnable;
				ReadWriteLock runningLock = synRunnable.runningLock();
				Lock writeLock = runningLock.writeLock();
				boolean tryLock = false;
				try {
					tryLock = writeLock.tryLock();
					//					return tryLock;
					continue;
				} finally {
					if (tryLock) {
						writeLock.unlock();
					}
				}
			} else {
				//				return true;
				continue;
			}
		}
		long end = System.currentTimeMillis();
		long iv = end - start;
		System.out.println(iv);
	}

	public static class MyISynRunnable implements ISynRunnable {
		ReadWriteLock runningLock = new ReentrantReadWriteLock();

		@Override
		public void run() {

		}

		@Override
		public boolean isCanceled() {
			return false;
		}

		@Override
		public boolean isNeededExecute() {
			return false;
		}

		@Override
		public ReadWriteLock runningLock() {
			return runningLock;
		}

		@Override
		public void runTask() {

		}

		@Override
		public void setCanceled(boolean isCanceled) {

		}

	}

}
