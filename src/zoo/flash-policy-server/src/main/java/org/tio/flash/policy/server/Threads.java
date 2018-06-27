/**
 *
 */
package org.tio.flash.policy.server;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * 
 * @author tanyaowu 
 * 2017年11月1日 上午10:36:10
 */
public class Threads {
	public static int CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 1;

	// public static final int CORE_POOL_SIZE = _CORE_POOL_SIZE;// < 160 ? 160 :
	// _CORE_POOL_SIZE;

	public static final int MAX_POOL_SIZE_FOR_TIO = Math.max(CORE_POOL_SIZE * 4, 64);
	
	public static final int MAX_POOL_SIZE_FOR_GROUP = Math.max(CORE_POOL_SIZE * 4, 128);

	public static final long KEEP_ALIVE_TIME = 90L;
	public static ThreadPoolExecutor groupExecutor = null;
	public static SynThreadPoolExecutor tioExecutor = null;
	
//	public static ThreadPoolExecutor levelExecutor = null;

	static {
		LinkedBlockingQueue<Runnable> tioQueue = new LinkedBlockingQueue<>();
		String tioThreadName = "tio";
		tioExecutor = new SynThreadPoolExecutor(MAX_POOL_SIZE_FOR_TIO, MAX_POOL_SIZE_FOR_TIO, KEEP_ALIVE_TIME, tioQueue, DefaultThreadFactory.getInstance(tioThreadName, Thread.NORM_PRIORITY),
				tioThreadName);
		tioExecutor.prestartAllCoreThreads();
		
//		LinkedBlockingQueue<Runnable> levelQueue = new LinkedBlockingQueue<>();
//		String levelThreadName = "level";
//		levelExecutor = new SynThreadPoolExecutor(Const.PropertiesConf.LEVEL_THREAD_MAX_SIZE * 2, Const.PropertiesConf.LEVEL_THREAD_MAX_SIZE  * 2, KEEP_ALIVE_TIME, levelQueue, DefaultThreadFactory.getInstance(tioThreadName, Thread.NORM_PRIORITY),
//				levelThreadName);
//		levelExecutor.prestartAllCoreThreads();

		LinkedBlockingQueue<Runnable> groupQueue = new LinkedBlockingQueue<>();
		String groupThreadName = "tio-group";
		groupExecutor = new ThreadPoolExecutor(MAX_POOL_SIZE_FOR_GROUP, MAX_POOL_SIZE_FOR_GROUP, KEEP_ALIVE_TIME, TimeUnit.SECONDS, groupQueue,
				DefaultThreadFactory.getInstance(groupThreadName, Thread.NORM_PRIORITY));
		groupExecutor.prestartAllCoreThreads();
		
		
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(Threads.groupExecutor);
	}

	/**
	 *
	 */
	public Threads() {
	}

}
