package org.tio.utils;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.tio.utils.date.DateUtils;

/**
 * 
 * @author tanyaowu 
 * 2018年6月17日 下午10:37:16
 */
public class HttpDateTimer {
	private static class TimerTask implements Runnable {
		@Override
		public void run() {
			httpDateString = DateUtils.httpDate();
		}
	}

	private final static ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable, "TioHttpDateTimer");
			thread.setDaemon(true);
			return thread;
		}
	});

	private static final long PERIOD = Long.parseLong(System.getProperty("http.date.timer.period", "1000"));

	private static volatile String httpDateString = DateUtils.httpDate();

	static {
		EXECUTOR.scheduleAtFixedRate(new TimerTask(), PERIOD, PERIOD, TimeUnit.MILLISECONDS);
		Runtime.getRuntime().addShutdownHook(new Thread("TioHttpDateTimer-Shutdown") {
			@Override
			public void run() {
				EXECUTOR.shutdown();
			}
		});
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public static String currDateString() {
		return httpDateString;
	}
}
