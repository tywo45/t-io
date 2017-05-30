package org.tio.core;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tanyaowu 
 * 2017年5月14日 上午9:55:37
 */
public class SetWithLock<T> extends ObjWithLock<Set<T>> {
	/**
	 * @param set
	 * @author: tanyaowu
	 */
	public SetWithLock(Set<T> set) {
		super(set);
	}

	/**
	 * @param set
	 * @param lock
	 * @author: tanyaowu
	 */
	public SetWithLock(Set<T> set, ReentrantReadWriteLock lock) {
		super(set, lock);
	}
}
