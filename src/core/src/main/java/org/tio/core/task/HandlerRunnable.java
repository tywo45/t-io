/**
 * 
 */
package org.tio.core.task;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ClientAction;
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
	public int handler(P packet) {
		int ret = 0;

		try {
			GroupContext<SessionContext, P, R> groupContext = channelContext.getGroupContext();

			Integer synSeq = packet.getSynSeq();
			if (synSeq != null && synSeq > 0) {
				ChannelContextMapWithLock<SessionContext, P, R> syns = channelContext.getGroupContext().getWaitingResps();
				P initPacket = syns.remove(synSeq);
				if (initPacket != null) {
					synchronized (initPacket) {
						syns.put(synSeq, packet);
						initPacket.notify();
					}
					groupContext.getGroupStat().getHandledPacket().incrementAndGet();
				} else {
					log.error("[{}]同步消息失败, synSeq is {}, 但是同步集合中没有对应key值", synFailCount.incrementAndGet(), synSeq);
				}
			} else {
				channelContext.traceClient(ClientAction.BEFORE_HANDLER, packet, null);
				groupContext.getAioHandler().handler(packet, channelContext);
				channelContext.traceClient(ClientAction.AFTER_HANDLER, packet, null);
				groupContext.getGroupStat().getHandledPacket().incrementAndGet();
			}
			ret++;
		} catch (Exception e) {
			log.error(e.toString(), e);
			return ret;
		} finally {

		}

		return ret;
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
