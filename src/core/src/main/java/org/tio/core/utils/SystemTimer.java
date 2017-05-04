/**
 * 
 */
package org.tio.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SystemTimer {
	/** The Constant executor. */
	private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	/** The Constant tickUnit. */
	private static final long tickUnit = Long.parseLong(System.getProperty("notify.systimer.tick", "10"));

	/** The time. */
	private static volatile long time = System.currentTimeMillis();

	/**
	 * The Class TimerTicker.
	 */
	private static class TimerTicker implements Runnable {

		/** 
		 * @see java.lang.Runnable#run()
		 * 
		 * @author: tanyaowu
		 * 2016年11月15日 上午9:07:45
		 * 
		 */
		@Override
		public void run() {
			time = System.currentTimeMillis();
		}
	}

	/**
	 * Current time millis.
	 *
	 * @return the long
	 */
	public static long currentTimeMillis() {
		return time;
	}

	static {
		executor.scheduleAtFixedRate(new TimerTicker(), tickUnit, tickUnit, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executor.shutdown();
			}
		});
	}
}
