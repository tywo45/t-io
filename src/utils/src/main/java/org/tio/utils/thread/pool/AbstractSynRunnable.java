package org.tio.utils.thread.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tanyaowu
 *
 */
public abstract class AbstractSynRunnable implements ISynRunnable {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(AbstractSynRunnable.class);

	private ReadWriteLock runningLock = new ReentrantReadWriteLock();

	public Executor executor;

	private boolean isCanceled = false;

	/**
	 * Instantiates a new abstract syn runnable.
	 */
	protected AbstractSynRunnable(Executor executor) {
		this.setExecutor(executor);
	}

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public final void run() {
		if (isCanceled()) //任务已经被取消
		{
			return;
		}

		Lock writeLock = runningLock().writeLock();
		boolean trylock = writeLock.tryLock();
		if (!trylock) {
			return;
		}

		try {
			runTask();
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			writeLock.unlock();
			if (isNeededExecute()) {
				executor.execute(this);
			}
		}
	}

	/**
	 * @see org.tio.core.threadpool.intf.ISynRunnable#runningLock()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月3日 下午1:53:03
	 *
	 */
	@Override
	public ReadWriteLock runningLock() {
		return runningLock;
	}

	@Override
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
