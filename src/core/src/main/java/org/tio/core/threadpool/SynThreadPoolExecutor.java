package org.tio.core.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

import org.tio.core.threadpool.intf.SynRunnableIntf;

/**
 * 
 * @author tanyaowu 
 * 2017年4月26日 下午2:18:30
 */
public class SynThreadPoolExecutor extends ThreadPoolExecutor {
	//	private static Logger log = LoggerFactory.getLogger(SynThreadPoolExecutor.class);

	/** The name. */
	private String name = null;

	/**
	 * 
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime 单位: 秒
	 * @param runnableQueue
	 * @param threadFactory
	 * @param name
	 * @author: tanyaowu
	 */
	public SynThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, BlockingQueue<Runnable> runnableQueue, ThreadFactory threadFactory, String name) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, runnableQueue, threadFactory);
		this.name = name;
	}

	/**
	 * 
	 * @param runnable
	 * @return
	 * @author: tanyaowu
	 */
	private boolean checkBeforeExecute(Runnable runnable) {
		if (runnable instanceof SynRunnableIntf) {
			SynRunnableIntf synRunnableIntf = (SynRunnableIntf) runnable;
			ReadWriteLock runningLock = synRunnableIntf.runningLock();
			Lock writeLock = runningLock.writeLock();
			boolean tryLock = false;
			try {
				tryLock = writeLock.tryLock();
				return tryLock;
			} finally {
				if (tryLock) {
					writeLock.unlock();
				}
			}
		} else {
			return true;
		}

	}

	@Override
	public void execute(Runnable runnable) {
		if (checkBeforeExecute(runnable)) {
			super.execute(runnable);
		}
	}

	@Override
	public <R> Future<R> submit(Runnable runnable, R result) {
		if (checkBeforeExecute(runnable)) {
			Future<R> ret = super.submit(runnable, result);
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

}
