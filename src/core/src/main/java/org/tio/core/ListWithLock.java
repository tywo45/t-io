package org.tio.core;

import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tanyaowu 
 * 2017年5月14日 上午9:55:37
 */
public class ListWithLock<T> extends ObjWithLock<List<T>> {
	/**
	 * @param list
	 * @author: tanyaowu
	 */
	public ListWithLock(List<T> list) {
		super(list);
	}

	/**
	 * @param list
	 * @param lock
	 * @author: tanyaowu
	 */
	public ListWithLock(List<T> list, ReentrantReadWriteLock lock) {
		super(list, lock);
	}
}
