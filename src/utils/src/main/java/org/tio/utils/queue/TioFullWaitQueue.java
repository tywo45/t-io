package org.tio.utils.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 暂时用ConcurrentLinkedQueue代替
 * @author tanyaowu 
 * 2019年9月30日 上午9:22:00
 */
public class TioFullWaitQueue<T> implements FullWaitQueue<T> {

	private ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

	/**
	 * 
	 * @param capacity
	 * @param useSingleProducer
	 * @author tanyaowu
	 */
	public TioFullWaitQueue(Integer capacity, boolean useSingleProducer) {
	}

	@Override
	public boolean add(T e) {
		return queue.add(e);
	}

	@Override
	public T poll() {
		return queue.poll();
	}

	@Override
	public int size() {
		return queue.size();
	}

	@Override
	public void clear() {
		queue.clear();
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

}
