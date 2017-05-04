package org.tio.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.intf.Packet;
import org.tio.core.threadpool.DefaultThreadFactory;
import org.tio.core.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:33:00
 */
public class ReconnConf<SessionContext, P extends Packet, R> {
	private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

	/**
	 * 重连的间隔时间，单位毫秒
	 */
	private long interval = 5000;

	/**
	 * 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连
	 */
	private int retryCount = 0;

	LinkedBlockingQueue<ChannelContext<SessionContext, P, R>> queue = new LinkedBlockingQueue<ChannelContext<SessionContext, P, R>>();

	//用来重连的线程池
	private ThreadPoolExecutor threadPoolExecutor = null;

	/**
	 * 
	 *
	 * @author: tanyaowu
	 * 
	 */
	public ReconnConf() {
		if (threadPoolExecutor == null) {
			synchronized (ReconnConf.class) {
				if (threadPoolExecutor == null) {
					threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS,
							new LinkedBlockingQueue<Runnable>(), DefaultThreadFactory.getInstance("tio-client-reconn"));
				}
			}

		}

	}

	/**
	 * @param interval
	 *
	 * @author: tanyaowu
	 * 
	 */
	public ReconnConf(long interval) {
		this();
		this.setInterval(interval);
	}

	/**
	 * @param interval
	 * @param retryCount
	 *
	 * @author: tanyaowu
	 * 
	 */
	public ReconnConf(long interval, int retryCount) {
		this();
		this.interval = interval;
		this.retryCount = retryCount;
	}

	public static <SessionContext, P extends Packet, R> void put(ClientChannelContext<SessionContext, P, R> clientChannelContext) {
		isNeedReconn(clientChannelContext, true);
	}

	public static <SessionContext, P extends Packet, R> boolean isNeedReconn(ClientChannelContext<SessionContext, P, R> clientChannelContext, boolean putIfTrue) {
		ClientGroupContext<SessionContext, P, R> clientGroupContext = (ClientGroupContext<SessionContext, P, R>) clientChannelContext.getGroupContext();
		ReconnConf<SessionContext, P, R> reconnConf = clientGroupContext.getReconnConf();
		if (reconnConf != null && reconnConf.getInterval() > 0) {
			if (reconnConf.getRetryCount() <= 0 || reconnConf.getRetryCount() >= clientChannelContext.getReconnCount()) {
				if (putIfTrue) {
					clientChannelContext.getStat().setTimeInReconnQueue(SystemTimer.currentTimeMillis());
					reconnConf.getQueue().add(clientChannelContext);
				}
				return true;
			} else {
				log.info("不需要重连{}", clientChannelContext);
				return false;
			}
		}

		return false;
	}

	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * @return the queue
	 */
	public LinkedBlockingQueue<ChannelContext<SessionContext, P, R>> getQueue() {
		return queue;
	}

	/**
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * @param retryCount the retryCount to set
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	/**
	 * @return the threadPoolExecutor
	 */
	public ThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	//	/**
	//	 * @param threadPoolExecutor the threadPoolExecutor to set
	//	 */
	//	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor)
	//	{
	//		this.threadPoolExecutor = threadPoolExecutor;
	//	}

}
