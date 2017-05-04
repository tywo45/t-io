package org.tio.core.threadpool;

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

	/** The thread pool name. */
	private String threadPoolName = null;

	/** The map of name and thread factory. */
	private static Map<String, DefaultThreadFactory> mapOfNameAndThreadFactory = new HashMap<String, DefaultThreadFactory>();

	/** The map of name and atomic integer. */
	private static Map<String, AtomicInteger> mapOfNameAndAtomicInteger = new HashMap<String, AtomicInteger>();

	/** The priority. */
	private int priority = Thread.NORM_PRIORITY;

	/**
	 * Gets the single instance of DefaultThreadFactory.
	 *
	 * @param threadName the thread name
	 * @param priority the priority
	 * @return single instance of DefaultThreadFactory
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

	public static DefaultThreadFactory getInstance(String threadName) {
		return getInstance(threadName, Thread.NORM_PRIORITY);
	}

	/**
	 * Instantiates a new default thread factory.
	 */
	private DefaultThreadFactory() {

	}

	/** 
	 * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
	 * 
	 * @param r
	 * @return
	 * @author: tanyaowu
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
	 * Gets the thread pool name.
	 *
	 * @return the thread pool name
	 */
	public String getThreadPoolName() {
		return threadPoolName;
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
