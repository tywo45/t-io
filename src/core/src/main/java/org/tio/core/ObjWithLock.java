package org.tio.core;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 自带读写锁的对象.
 *
 * @author tanyaowu
 */
public class ObjWithLock<T> {

	/** The obj. */
	private T obj = null;

	/**
	 * The lock.
	 *
	 * @含义: 
	 * @类型: ReentrantReadWriteLock
	 */
	private ReentrantReadWriteLock lock = null;

	/**
	 * Instantiates a new obj with read write lock.
	 *
	 * @param obj the obj
	 * @author: tanyaowu
	 * 2016年11月5日 下午1:50:34
	 */
	public ObjWithLock(T obj) {
		this(obj, new ReentrantReadWriteLock());
	}

	/**
	 * Instantiates a new obj with read write lock.
	 *
	 * @param obj the obj
	 * @param lock the lock
	 * @author: tanyaowu
	 * 2016年11月7日 上午10:16:08
	 */
	public ObjWithLock(T obj, ReentrantReadWriteLock lock) {
		super();
		this.obj = obj;
		this.lock = lock;
	}

	/**
	 * Gets the lock.
	 *
	 * @return the lock
	 * @author: tanyaowu
	 * 2016年11月7日 上午10:17:27
	 */
	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	//	/**
	//	 * Sets the lock.
	//	 *
	//	 * @param lock the new lock
	//	 * @author: tanyaowu
	//	 * 2016年11月7日 上午10:17:31
	//	 */
	//	public void setLock(ReentrantReadWriteLock lock)
	//	{
	//		this.lock = lock;
	//	}

	/**
	 * Gets the obj.
	 *
	 * @return the obj
	 * @author: tanyaowu
	 * 2016年11月7日 上午10:17:34
	 */
	public T getObj() {
		return obj;
	}

	/**
	 * Sets the obj.
	 *
	 * @param obj the new obj
	 * @author: tanyaowu
	 * 2016年11月7日 上午10:17:37
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}
}
