package org.tio.utils.thread.pool;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 */
public abstract class AbstractSynRunnable implements Runnable {

	/**
	 * 是否已经提交到线程池了
	 */
	private boolean executed = false;

	/**
	 * 提交成功次数
	 */
	public AtomicInteger executeCount = new AtomicInteger();

	/**
	 * 避免重复提交次数
	 */
	public AtomicInteger avoidRepeatExecuteCount = new AtomicInteger();

	/**
	 * 被循环执行的次数
	 */
	public AtomicInteger loopCount = new AtomicInteger();

	/**
	 * 运行次数
	 */
	public AtomicInteger runCount = new AtomicInteger();

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(AbstractSynRunnable.class);

	private ReadWriteLock runningLock = new ReentrantReadWriteLock();

	public Executor executor;

	private boolean isCanceled = false;

	/**
	 * Instantiates a new abstract syn runnable.
	 */
	protected AbstractSynRunnable(Executor executor) {
		this.executor = executor;
	}

	/**
	 * 把本任务对象提交到线程池去执行
	 * @author tanyaowu
	 */
	public void execute() {
		executor.execute(this);
	}

	public abstract boolean isNeededExecute();

	public boolean isCanceled() {
		return isCanceled;
	}

	@Override
	public final void run() {
		Lock writeLock = runningLock().writeLock();

		writeLock.lock();
		try {
			runCount.incrementAndGet();

			if (isCanceled()) //任务已经被取消
			{
				return;
			}

			loopCount.set(0);

			runTask();

			while (isNeededExecute() && loopCount.incrementAndGet() <= 10) {
				runTask();
			}

		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			setExecuted(false);
			writeLock.unlock();

			//下面这段代码一定要在unlock()后面，别弄错了 ^_^
			if (isNeededExecute()) {
				execute();
			}
		}
	}

	/**
	 * 
	 * @author tanyaowu
	 */
	public abstract void runTask();

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public ReadWriteLock runningLock() {
		return runningLock;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	/**
	 * 是否已经提交到线程池了
	 * @return the executed
	 */
	public boolean isExecuted() {
		return executed;
	}

	/**
	 * 是否已经提交到线程池了
	 * @param executed the executed to set
	 */
	public void setExecuted(boolean executed) {
		this.executed = executed;
	}

	public String logstr() {
		return this.getClass().getName();
	}
}
