package org.tio.utils.thread.pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author tanyaowu
 * 2017年4月28日 下午1:30:39
 */
public class DefaultThreadFactory implements ThreadFactory {

	/** The cacheMap of name and thread factory. */
	private static Map<String, DefaultThreadFactory> mapOfNameAndThreadFactory = new HashMap<>();

	/** The cacheMap of name and atomic integer. */
	private static Map<String, AtomicInteger> mapOfNameAndAtomicInteger = new HashMap<>();

	public static DefaultThreadFactory getInstance(String threadName) {
		return getInstance(threadName, Thread.NORM_PRIORITY);
	}

	/**
	 * Gets the single INSTANCE of DefaultThreadFactory.
	 *
	 * @param threadName the thread name
	 * @param priority the priority
	 * @return single INSTANCE of DefaultThreadFactory
	 */
	public static DefaultThreadFactory getInstance(String threadName, Integer priority) {
		DefaultThreadFactory defaultThreadFactory = mapOfNameAndThreadFactory.get(threadName);
		if (defaultThreadFactory == null) {
			defaultThreadFactory = new DefaultThreadFactory();
			if (priority != null) {
				defaultThreadFactory.priority = priority;
			}

			defaultThreadFactory.setThreadName(threadName);
			mapOfNameAndThreadFactory.put(threadName, defaultThreadFactory);
			mapOfNameAndAtomicInteger.put(threadName, new AtomicInteger());
		}
		return defaultThreadFactory;
	}

	/** The thread pool name. */
	private String threadPoolName = null;

	/** The priority. */
	private int priority = Thread.NORM_PRIORITY;

	/**
	 * Instantiates a new default thread factory.
	 */
	private DefaultThreadFactory() {

	}

	/**
	 * Gets the thread pool name.
	 *
	 * @return the thread pool name
	 */
	public String getThreadPoolName() {
		return threadPoolName;
	}

	/**
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 *
	 * @param r
	 * @return
	 * @author tanyaowu
	 * 2016年11月15日 上午9:07:00
	 *
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setName(this.getThreadPoolName() + "-" + mapOfNameAndAtomicInteger.get(this.getThreadPoolName()).incrementAndGet());
		thread.setPriority(priority);
		return thread;
	}

	/**
	 * Sets the thread name.
	 *
	 * @param threadName the new thread name
	 */
	public void setThreadName(String threadName) {
		this.threadPoolName = threadName;
	}

}
