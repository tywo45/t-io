package org.tio.utils.queue;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2019年9月28日 上午9:01:00
 */
public class TioFullWaitQueueTest {
	private static Logger log = LoggerFactory.getLogger(TioFullWaitQueueTest.class);

	private static java.util.concurrent.atomic.AtomicInteger c = new AtomicInteger();

	public static void main(String[] args) {
		TioFullWaitQueue<Integer> queue = new TioFullWaitQueue<>(10, 5L);
		for (int i = 1; i <= 20; i++) {
			Thread thread = new Thread(new Producter(queue), String.valueOf(i));
			thread.start();
		}
		
		try {
			Thread.sleep(1000L * 1L);
		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}
		
		for (int i = 1; i <= 10; i++) {
			Thread thread = new Thread(new Consumer(queue), String.valueOf(i));
			thread.start();
		}

		try {
			Thread.sleep(1000L * 20L);
		} catch (InterruptedException e) {
			log.error(e.toString(), e);
		}
	}

	static class Producter implements Runnable {
		private TioFullWaitQueue<Integer> queue;

		public Producter(TioFullWaitQueue<Integer> queue) {
			this.queue = queue;
		}

		public void produce() {
			queue.add(c.incrementAndGet());
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				produce();
			}
		}
	}

	static class Consumer implements Runnable {
		private TioFullWaitQueue<Integer> queue;

		public Consumer(TioFullWaitQueue<Integer> queue) {
			this.queue = queue;
		}

		public Integer remove() {
			return queue.poll();
		}

		@Override
		public void run() {
			for (int i = 0; i < 1000; i++) {
				remove();
			}

		}
	}

}
