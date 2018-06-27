package org.tio.utils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author tanyaowu
 * 2017年8月13日 上午11:36:12
 */
public class SystemTimer {
	private static class TimerTask implements Runnable {
		@Override
		public void run() {
			time = System.currentTimeMillis();
		}
	}

	private final static ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "TioSystemTimer");
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final long PERIOD = Long.parseLong(System.getProperty("system.timer.period", "10"));

	private static volatile long time = System.currentTimeMillis();

	static {
		EXECUTOR.scheduleAtFixedRate(new TimerTask(), PERIOD, PERIOD, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread("TioSystemTimer-Shutdown") {
			@Override
			public void run() {
				EXECUTOR.shutdown();
			}
		});
	}

	/**
	 * Current time millis.
	 *
	 * @return the long
	 */
	public static long currentTimeMillis() {
		return time;
	}
}
