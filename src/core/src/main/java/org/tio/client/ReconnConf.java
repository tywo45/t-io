package org.tio.client;

import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.utils.SystemTimer;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:33:00
 */
public class ReconnConf {
	private static Logger log = LoggerFactory.getLogger(ChannelContext.class);

	public static ReconnConf getReconnConf(ClientChannelContext clientChannelContext) {
		ClientGroupContext clientGroupContext = (ClientGroupContext) clientChannelContext.groupContext;
		ReconnConf reconnConf = clientGroupContext.getReconnConf();
		return reconnConf;
	}

	public static ReconnConf getReconnConf(ChannelContext channelContext) {
		return getReconnConf((ClientChannelContext) channelContext);
	}

	/**
	 * 
	 * @param clientChannelContext
	 * @param putIfNeedConn 如果需要重连，则把该ClientChannelContext放到重连队列中
	 * @return
	 */
	public static boolean isNeedReconn(ClientChannelContext clientChannelContext, boolean putIfNeedConn) {
		if (clientChannelContext == null) {
			return false;
		}
		ReconnConf reconnConf = getReconnConf(clientChannelContext);
		if (reconnConf == null) {
			return false;
		}

		if (reconnConf.getInterval() > 0) {
			if (reconnConf.getRetryCount() <= 0 || reconnConf.getRetryCount() > clientChannelContext.getReconnCount().get()) {
				if (putIfNeedConn) {
					ClientGroupContext clientGroupContext = (ClientGroupContext) clientChannelContext.groupContext;
					clientGroupContext.closeds.add(clientChannelContext);
					clientChannelContext.stat.timeInReconnQueue = SystemTimer.currTime;
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
	 * @param clientChannelContext
	 * @return true:需要重连;     false:不需要重连
	 */
	public static boolean put(ClientChannelContext clientChannelContext) {
		if (clientChannelContext == null) {
			return false;
		}

		//		ReconnConf reconnConf = ReconnConf.getReconnConf(clientChannelContext);	
		return isNeedReconn(clientChannelContext, true);
	}

	/**
	 * 重连的间隔时间，单位毫秒
	 */
	private long interval = 5000;

	/**
	 * 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连
	 */
	private int retryCount = 0;

	LinkedBlockingQueue<ChannelContext> queue = new LinkedBlockingQueue<>();

	/**
	 * 用来重连的线程池
	 */
	private volatile SynThreadPoolExecutor threadPoolExecutor = null;

	/**
	 *
	 *
	 * @author tanyaowu
	 *
	 */
	public ReconnConf() {
		if (threadPoolExecutor == null) {
			synchronized (ReconnConf.class) {
				if (threadPoolExecutor == null) {
					//					threadPoolExecutor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, TimeUnit.SECONDS,
					//							new LinkedBlockingQueue<Runnable>(), DefaultThreadFactory.getInstance("tio-client-reconn"));
					//					
					//					

					LinkedBlockingQueue<Runnable> tioQueue = new LinkedBlockingQueue<>();
					//			ArrayBlockingQueue<Runnable> tioQueue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
					String tioThreadName = "tio-client-reconn";
					DefaultThreadFactory defaultThreadFactory = DefaultThreadFactory.getInstance(tioThreadName, Thread.MAX_PRIORITY);

					threadPoolExecutor = new SynThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 60L, tioQueue,
					        defaultThreadFactory, tioThreadName);
					//			tioExecutor = new SynThreadPoolExecutor(AVAILABLE_PROCESSORS * 2, Integer.MAX_VALUE, 60, new SynchronousQueue<Runnable>(), defaultThreadFactory, tioThreadName);

				}
			}

		}

	}

	/**
	 * @param interval
	 *
	 * @author tanyaowu
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
	 * @author tanyaowu
	 *
	 */
	public ReconnConf(long interval, int retryCount) {
		this();
		this.interval = interval;
		this.retryCount = retryCount;
	}

	/**
	 * @return the interval
	 */
	public long getInterval() {
		return interval;
	}

	/**
	 * @return the queue
	 */
	public LinkedBlockingQueue<ChannelContext> getQueue() {
		return queue;
	}

	/**
	 * 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连
	 * @return the retryCount
	 */
	public int getRetryCount() {
		return retryCount;
	}

	/**
	 * @return the threadPoolExecutor
	 */
	public SynThreadPoolExecutor getThreadPoolExecutor() {
		return threadPoolExecutor;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval) {
		this.interval = interval;
	}

	/**
	 * 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连
	 * @param retryCount the retryCount to set
	 */
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	//	/**
	//	 * @param threadPoolExecutor the threadPoolExecutor to set
	//	 */
	//	public void setThreadPoolExecutor(ThreadPoolExecutor threadPoolExecutor)
	//	{
	//		this.threadPoolExecutor = threadPoolExecutor;
	//	}

}
