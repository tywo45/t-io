/**
 * 
 */
package org.tio.utils.cache.caffeine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

/**
 * @author tanyaowu
 * @param <K>
 * @param <V>
 *
 */
public class DefaultRemovalListener<K, V> implements RemovalListener<K, V> {
	private static Logger log = LoggerFactory.getLogger(DefaultRemovalListener.class);

	private String cacheName = null;

	/**
	 * 
	 */
	public DefaultRemovalListener(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public void onRemoval(K key, V value, RemovalCause cause) {
		if (log.isDebugEnabled()) {
			log.debug("cacheName:{}, key:{}, value:{} was removed", cacheName, key, value);
		}
	}
}
