package org.tio.utils.lock;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author tanyaowu
 * 2017年5月14日 上午9:55:37
 */
public class ListWithLock<T> extends ObjWithLock<List<T>> {
	private static final long	serialVersionUID	= 8549668315606224029L;
	private static final Logger	log					= LoggerFactory.getLogger(ListWithLock.class);

	/**
	 * @param durationList
	 * @author tanyaowu
	 */
	public ListWithLock(List<T> list) {
		super(list);
	}

	/**
	 * @param durationList
	 * @param lock
	 * @author tanyaowu
	 */
	public ListWithLock(List<T> list, ReentrantReadWriteLock lock) {
		super(list, lock);
	}

	/**
	 *
	 * @param t
	 * @return
	 * @author tanyaowu
	 */
	public boolean add(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			return list.add(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	/**
	 *
	 *
	 * @author tanyaowu
	 */
	public void clear() {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			list.clear();
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 *
	 * @param t
	 * @return
	 * @author tanyaowu
	 */
	public boolean remove(T t) {
		WriteLock writeLock = this.writeLock();
		writeLock.lock();
		try {
			List<T> list = this.getObj();
			return list.remove(t);
		} catch (Throwable e) {
			log.error(e.getMessage(), e);
		} finally {
			writeLock.unlock();
		}
		return false;
	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public int size() {
		ReadLock readLock = this.readLock();
		readLock.lock();
		try {
			List<T> list = this.getObj();
			return list.size();
		} finally {
			readLock.unlock();
		}
	}
}
