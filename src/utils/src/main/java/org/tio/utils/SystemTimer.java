package org.tio.utils;

import java.util.ArrayList;
import java.util.List;
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
			currTime = System.currentTimeMillis();
			if (list != null) {
				for (TimerListener timerListener : list) {
					timerListener.onChange(currTime);
				}
			}
		}
	}

	private static volatile List<TimerListener> list = null;//new ArrayList<>();

	public static void addTimerListener(TimerListener timerListener) {
		if (list == null) {
			synchronized (TimerTask.class) {
				if (list == null) {
					list = new ArrayList<>();
				}
			}
		}
		list.add(timerListener);
	}

	public static interface TimerListener {
		void onChange(long currTime);
	}

	private final static ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "TioSystemTimer");
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final long PERIOD = Long.parseLong(System.getProperty("tio.system.timer.period", "10"));

	public static volatile long currTime = System.currentTimeMillis();

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
	 * Current currTime millis.
	 *
	 * @return the long
	 */
	public static long currentTimeMillis() {
		return currTime;
	}
}
