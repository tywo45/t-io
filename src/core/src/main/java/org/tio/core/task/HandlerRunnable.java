/**
 *
 */
package org.tio.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.core.stat.IpStat;
import org.tio.utils.SystemTimer;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.thread.pool.AbstractQueueRunnable;

/**
 *
 * @author 谭耀武
 * 2012-08-09
 *
 */
public class HandlerRunnable extends AbstractQueueRunnable<Packet> {
	private static final Logger log = LoggerFactory.getLogger(HandlerRunnable.class);

	private ChannelContext	channelContext	= null;
	private TioConfig	tioConfig	= null;

	private AtomicLong synFailCount = new AtomicLong();

	public HandlerRunnable(ChannelContext channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
		tioConfig = channelContext.tioConfig;
	}

	/**
	 * 处理packet
	 * @param packet
	 * @return
	 *
	 * @author tanyaowu
	 */
	public void handler(Packet packet) {
		//		int ret = 0;

		long start = SystemTimer.currTime;
		try {
			Integer synSeq = packet.getSynSeq();
			if (synSeq != null && synSeq > 0) {
				MapWithLock<Integer, Packet> syns = tioConfig.getWaitingResps();
				Packet initPacket = syns.remove(synSeq);
				if (initPacket != null) {
					synchronized (initPacket) {
						syns.put(synSeq, packet);
						initPacket.notify();
					}
				} else {
					log.error("[{}]同步消息失败, synSeq is {}, 但是同步集合中没有对应key值", synFailCount.incrementAndGet(), synSeq);
				}
			} else {
				tioConfig.getAioHandler().handler(packet, channelContext);
			}
		} catch (Throwable e) {
			log.error(packet.logstr(), e);
		} finally {
			long end = SystemTimer.currTime;
			long iv = end - start;
			if (tioConfig.statOn) {
				channelContext.stat.handledPackets.incrementAndGet();
				channelContext.stat.handledBytes.addAndGet(packet.getByteCount());
				channelContext.stat.handledPacketCosts.addAndGet(iv);

				tioConfig.groupStat.handledPackets.incrementAndGet();
				tioConfig.groupStat.handledBytes.addAndGet(packet.getByteCount());
				tioConfig.groupStat.handledPacketCosts.addAndGet(iv);
			}

			if (tioConfig.ipStats.durationList != null && tioConfig.ipStats.durationList.size() > 0) {
				try {
					for (Long v : tioConfig.ipStats.durationList) {
						IpStat ipStat = (IpStat) tioConfig.ipStats.get(v, channelContext);
						ipStat.getHandledPackets().incrementAndGet();
						ipStat.getHandledBytes().addAndGet(packet.getByteCount());
						ipStat.getHandledPacketCosts().addAndGet(iv);
						tioConfig.getIpStatListener().onAfterHandled(channelContext, packet, ipStat, iv);
					}
				} catch (Exception e1) {
					log.error(e1.toString(), e1);
				}
			}

			if (tioConfig.getAioListener() != null) {
				try {
					tioConfig.getAioListener().onAfterHandled(channelContext, packet, iv);
				} catch (Exception e) {
					log.error(e.toString(), e);
				}
			}

		}
	}

	/**
	 * @see org.tio.core.SynRunnable.intf.ISynRunnable#runTask()
	 *
	 * @author tanyaowu
	 * 2016年12月5日 下午3:02:49
	 *
	 */
	@Override
	public void runTask() {
		Packet packet = null;
		while ((packet = msgQueue.poll()) != null) {
			handler(packet);
		}
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	@Override
	public String logstr() {
		return toString();
	}
}
