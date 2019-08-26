package org.tio.utils.lock;

import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自带读写锁的对象.
 *
 * @author tanyaowu
 */
public class ObjWithLock<T> implements Serializable {

	private static final long serialVersionUID = -3048283373239453901L;

	private static Logger log = LoggerFactory.getLogger(ObjWithLock.class);

	/**
	 * 
	 */
	private T obj = null;

	/**
	 * 
	 */
	private ReentrantReadWriteLock lock = null;

	/**
	 * 
	 * @param obj
	 * @author tanyaowu
	 */
	public ObjWithLock(T obj) {
		this(obj, new ReentrantReadWriteLock());
	}

	/**
	 * 
	 * @param obj
	 * @param lock
	 * @author tanyaowu
	 */
	public ObjWithLock(T obj, ReentrantReadWriteLock lock) {
		super();
		this.obj = obj;
		this.lock = lock;
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public ReentrantReadWriteLock getLock() {
		return lock;
	}

	/**
	 * 获取写锁
	 * @return
	 */
	public WriteLock writeLock() {
		return lock.writeLock();
	}

	/**
	 * 获取读锁
	 * @return
	 */
	public ReadLock readLock() {
		return lock.readLock();
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public T getObj() {
		return obj;
	}

	/**
	 * 
	 * @param obj
	 * @author tanyaowu
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}

	/**
	 * 操作obj时，带上读锁
	 * @param readLockHandler
	 */
	public void handle(ReadLockHandler<T> readLockHandler) {
		ReadLock readLock = lock.readLock();
		readLock.lock();
		try {
			readLockHandler.handler(obj);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * 操作obj时，带上写锁
	 * @param writeLockHandler
	 */
	public void handle(WriteLockHandler<T> writeLockHandler) {
		WriteLock writeLock = lock.writeLock();
		writeLock.lock();
		try {
			writeLockHandler.handler(obj);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

}
