package org.tio.core;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author tanyaowu 
 * 2017年5月14日 上午9:55:37
 */
public class MapWithLock<K, V> extends ObjWithLock<Map<K, V>> {

	/**
	 * @param map
	 * @param lock
	 * @author: tanyaowu
	 */
	public MapWithLock(Map<K, V> map, ReentrantReadWriteLock lock) {
		super(map, lock);
	}

	/**
	 * @param map
	 * @author: tanyaowu
	 */
	public MapWithLock(Map<K, V> map) {
		super(map);
	}

}
