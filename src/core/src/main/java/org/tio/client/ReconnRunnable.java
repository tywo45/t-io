package org.tio.client;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.SystemTimer;
import org.tio.utils.thread.pool.AbstractSynRunnable;

/**
 * @author tanyaowu
 *
 */
public class ReconnRunnable extends AbstractSynRunnable {
	private static Logger log = LoggerFactory.getLogger(ReconnRunnable.class);

	ClientChannelContext	channelContext	= null;
	TioClient				tioClient		= null;

	//		private static Map<Node, Long> cacheMap = new HashMap<>();

	public ReconnRunnable(ClientChannelContext channelContext, TioClient tioClient, Executor executor) {
		super(executor);
		this.channelContext = channelContext;
		this.tioClient = tioClient;
	}

	@Override
	public boolean isNeededExecute() {
		return false;
	}

	@Override
	public void runTask() {
		channelContext.getReconnCount().incrementAndGet();
		ReentrantReadWriteLock closeLock = channelContext.closeLock;
		WriteLock writeLock = closeLock.writeLock();
		writeLock.lock();
		try {
			if (!channelContext.isClosed) //已经连上了，不需要再重连了
			{
				return;
			}
			long start = SystemTimer.currTime;
			tioClient.reconnect(channelContext, 2);
			long end = SystemTimer.currTime;
			long iv = end - start;
			//			if (iv >= 100) {
			//				log.error("{}, 第{}次重连,重连耗时:{} ms", channelContext, channelContext.getReconnCount(), iv);
			//			} else {
			//				log.info("{}, 第{}次重连,重连耗时:{} ms", channelContext, channelContext.getReconnCount(), iv);
			//			}

			log.error("{}, 第{}次重连,重连耗时:{} ms", channelContext, channelContext.getReconnCount(), iv);

			//			if (channelContext.isClosed) {
			//				//					cacheMap.put(channelContext.getServerNode(), SystemTimer.currTime);
			//				return;
			//			}
		} catch (java.lang.Throwable e) {
			log.error(e.toString(), e);
		} finally {
			writeLock.unlock();
		}

	}
}
