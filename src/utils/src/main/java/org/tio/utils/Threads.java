/**
 *
 */
package org.tio.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 *
 * @author tanyaowu
 * 2017年7月7日 上午11:12:03
 */
public class Threads {
	public static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 1;

	public static final int MAX_POOL_SIZE_FOR_TIO = Math.max(CORE_POOL_SIZE * 4, 128);
	
	public static final int MAX_POOL_SIZE_FOR_GROUP = Math.max(CORE_POOL_SIZE * 4, 256);

	public static final long KEEP_ALIVE_TIME = 360000L;
	public static ThreadPoolExecutor groupExecutor = null;
	public static SynThreadPoolExecutor tioExecutor = null;
	public static SynThreadPoolExecutor tioCloseExecutor = null;

	static {
		LinkedBlockingQueue<Runnable> tioQueue = new LinkedBlockingQueue<>();
		String tioThreadName = "tio";
		tioExecutor = new SynThreadPoolExecutor(MAX_POOL_SIZE_FOR_TIO, MAX_POOL_SIZE_FOR_TIO, KEEP_ALIVE_TIME, tioQueue, DefaultThreadFactory.getInstance(tioThreadName, Thread.NORM_PRIORITY),
				tioThreadName);
		tioExecutor.prestartAllCoreThreads();
		
		LinkedBlockingQueue<Runnable> tioCloseQueue = new LinkedBlockingQueue<>();
		String tioCloseThreadName = "tio-close";
		tioCloseExecutor = new SynThreadPoolExecutor(16, 16, KEEP_ALIVE_TIME, tioCloseQueue, DefaultThreadFactory.getInstance(tioCloseThreadName, Thread.NORM_PRIORITY),
				tioCloseThreadName);
		tioCloseExecutor.prestartAllCoreThreads();

		LinkedBlockingQueue<Runnable> groupQueue = new LinkedBlockingQueue<>();
		String groupThreadName = "tio-group";
		groupExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE_FOR_GROUP, MAX_POOL_SIZE_FOR_GROUP, KEEP_ALIVE_TIME, TimeUnit.SECONDS, groupQueue,
				DefaultThreadFactory.getInstance(groupThreadName, Thread.NORM_PRIORITY));
		groupExecutor.prestartAllCoreThreads();
	}

	/**
	 *
	 */
	private Threads() {
	}

}
