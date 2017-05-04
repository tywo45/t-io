package org.tio.core.threadpool.intf;

import java.util.concurrent.locks.ReadWriteLock;

public interface SynRunnableIntf extends Runnable {
	public ReadWriteLock runningLock();

	public boolean isNeededExecute();

	public boolean isCanceled();

	public void setCanceled(boolean isCanceled);

	public void runTask();
}
