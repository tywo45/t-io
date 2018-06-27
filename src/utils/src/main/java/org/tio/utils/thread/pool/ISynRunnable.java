package org.tio.utils.thread.pool;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * 
 * @author tanyaowu 
 * 2017年10月19日 上午9:41:53
 */
public interface ISynRunnable extends Runnable {
	/**
	 * 任务是否已经被取消
	 * @return
	 * @author: tanyaowu
	 */
	public boolean isCanceled();

	/**
	 * 是否仍然需要执行
	 * @return
	 * @author: tanyaowu
	 */
	public boolean isNeededExecute();

	/**
	 * 运行锁
	 * @return
	 * @author: tanyaowu
	 */
	public ReadWriteLock runningLock();

	/**
	 * 执行任务
	 * 
	 * @author: tanyaowu
	 */
	public void runTask();

	/**
	 * 设置该任务是否被取消
	 * @param isCanceled
	 * @author: tanyaowu
	 */
	public void setCanceled(boolean isCanceled);
}
