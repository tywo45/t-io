/**
 * 
 */
package org.tio.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelAction;
import org.tio.core.GroupContext;
import org.tio.core.intf.Packet;
import org.tio.core.maintain.ChannelContextMapWithLock;
import org.tio.core.threadpool.AbstractQueueRunnable;

/**
 * 
 * @author 谭耀武
 * 2012-08-09
 * 
 */
public class HandlerRunnable<SessionContext, P extends Packet, R> extends AbstractQueueRunnable<P> {
	private static final Logger log = LoggerFactory.getLogger(HandlerRunnable.class);

	private ChannelContext<SessionContext, P, R> channelContext = null;

	public HandlerRunnable(ChannelContext<SessionContext, P, R> channelContext, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
	}

	private AtomicLong synFailCount = new AtomicLong();

	/**
	 * 处理packet
	 * @param packet
	 * @return
	 *
	 * @author: tanyaowu
	 */
	public void handler(P packet) {
		//		int ret = 0;

		GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();
		try {

			Integer synSeq = packet.getSynSeq();
			if (synSeq != null && synSeq > 0) {
				ChannelContextMapWithLock<SessionContext, P, R> syns = channelContext.getGroupContext().getWaitingResps();
				P initPacket = syns.remove(synSeq);
				if (initPacket != null) {
					synchronized (initPacket) {
						syns.put(synSeq, packet);
						initPacket.notify();
					}
				} else {
					log.error("[{}]同步消息失败, synSeq is {}, 但是同步集合中没有对应key值", synFailCount.incrementAndGet(), synSeq);
				}
			} else {
				groupContext.getAioHandler().handler(packet, channelContext);
			}
			//			ret++;
		} catch (Exception e) {
			log.error(e.toString(), e);
			//			return ret;
		} finally {
			channelContext.getStat().getHandledPackets().incrementAndGet();
			channelContext.getStat().getHandledBytes().addAndGet(packet.getByteCount());

			groupContext.getGroupStat().getHandledPacket().incrementAndGet();
			groupContext.getGroupStat().getHandledBytes().addAndGet(packet.getByteCount());

		}

		//		return ret;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ":" + channelContext.toString();
	}

	/** 
	 * @see org.tio.core.threadpool.intf.SynRunnableIntf#runTask()
	 * 
	 * @author: tanyaowu
	 * 2016年12月5日 下午3:02:49
	 * 
	 */
	@Override
	public void runTask() {
		P packet = null;
		while ((packet = msgQueue.poll()) != null) {
			handler(packet);
		}
	}
}
