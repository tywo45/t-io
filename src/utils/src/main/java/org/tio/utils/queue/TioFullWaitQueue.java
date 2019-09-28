package org.tio.utils.queue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu 
 * 2019年9月28日 上午8:49:47
 */
public class TioFullWaitQueue<T> implements FullWaitQueue<T> {
	private static Logger	log					= LoggerFactory.getLogger(TioFullWaitQueue.class);
	private T[]				array				= null;												//定义为数组，在创建对象时就确定容量
	private Lock			lock				= new ReentrantLock();
	private Condition		fullWaitCondition	= lock.newCondition();
	private long			waitTimeoutInSecond	= 5L;
	private int				capacity			= 0;
	private int				size				= 0;
	private int				tailIndex			= 0;												//队列尾部指针，用于添加元素
	private int				headIndex			= 0;												//队列头部指针，用于删除元素

	@SuppressWarnings("unchecked")
	public TioFullWaitQueue(int capacity, long waitTimeoutInSecond) {
		this.capacity = capacity;
		this.waitTimeoutInSecond = waitTimeoutInSecond;
		array = (T[]) new Object[capacity];
	}

	private void init() {
		lock.lock();
		try {
			size = 0;
			tailIndex = 0; //队列尾部指针，用于添加元素
			headIndex = 0; //队列头部指针，用于删除元素
			for (int i = 0; i < array.length; i++) {
				array[i] = null;
			}
		} finally {
			lock.unlock();
		}
	}

	public boolean isFull() {
		return size == capacity;
	}

	@Override
	public boolean add(T t) {
		if (t == null) {
			return false;
		}
		lock.lock();
		try {
			if (isFull()) {
				boolean f = fullWaitCondition.await(waitTimeoutInSecond, TimeUnit.SECONDS);
				if (!f) {
					log.error("队列已满， 并且没有等到空位置，数据将被丢弃.{}", t);
					return false;
				}
				log.info("队列已满，不过等到了空位置");
			}
			array[tailIndex] = t;
			if (++tailIndex == capacity) {
				tailIndex = 0;
			}
			size++;
			//			System.out.println("插入一个元素:" + t + "，数组为：" + Arrays.toString(array));
			return true;
		} catch (InterruptedException e) {
			log.error(e.toString(), e);
			return false;
		} finally {
			lock.unlock();
		}
	}

	/**
	* Retrieves and removes the head of this queue,
	* or returns {@code null} if this queue is empty.
	*
	* @return the head of this queue, or {@code null} if this queue is empty
	*/
	@Override
	public T poll() {
		lock.lock();
		try {
			if (size == 0) {
				return null;
			}
			T t = array[headIndex];
			array[headIndex] = null;
			//			System.out.println("移除一个元素:" + t + "，数组为：" + Arrays.toString(array));
			if (++headIndex == capacity) {
				headIndex = 0;
			}
			size--;
			fullWaitCondition.signal();
			return t;
		} finally {
			lock.unlock();
		}
	}

	/** 
	 * 
	 * @author tanyaowu
	 */
	@Override
	public void clear() {
		init();
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public int size() {
		return size;
	}

}
