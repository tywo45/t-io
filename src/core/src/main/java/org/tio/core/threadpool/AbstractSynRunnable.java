package org.tio.core.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.threadpool.intf.SynRunnableIntf;

/**
 *  
 * @author tanyaowu 
 * 
 */
public abstract class AbstractSynRunnable implements SynRunnableIntf {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(AbstractSynRunnable.class);

	private ReadWriteLock runningLock = new ReentrantReadWriteLock();

	private Executor executor;

	/**
	 * Instantiates a new abstract syn runnable.
	 */
	protected AbstractSynRunnable(Executor executor) {
		this.setExecutor(executor);
	}

	/** 
	 * @see org.tio.core.threadpool.intf.SynRunnableIntf#runningLock()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月3日 下午1:53:03
	 * 
	 */
	@Override
	public ReadWriteLock runningLock() {
		return runningLock;
	}

	@Override
	public final void run() {
		if (isCanceled()) //任务已经被取消
		{
			return;
		}

		ReadWriteLock runningLock = runningLock();
		Lock writeLock = runningLock.writeLock();
		boolean trylock = writeLock.tryLock();
		if (!trylock) {
			return;
		}

		try {
			runTask();
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			writeLock.unlock();
			if (isNeededExecute()) {
				getExecutor().execute(this);
			}
		}
	}

	private boolean isCanceled = false;

	@Override
	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	/**
	 * @return the executor
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * @param executor the executor to set
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
}
