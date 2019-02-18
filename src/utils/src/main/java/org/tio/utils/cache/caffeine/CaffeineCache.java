/**
 * 
 */
package org.tio.utils.cache.caffeine;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.AbsCache;
import org.tio.utils.caffeine.CaffeineUtils;
import org.tio.utils.hutool.StrUtil;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.RemovalListener;

/**
 * @author tanyaowu
 *
 */
public class CaffeineCache extends AbsCache {
	private static Logger log = LoggerFactory.getLogger(CaffeineCache.class);

	public static Map<String, CaffeineCache> map = new HashMap<>();

	public static CaffeineCache getCache(String cacheName, boolean skipNull) {
		CaffeineCache caffeineCache = map.get(cacheName);
		if (caffeineCache == null && !skipNull) {
			log.error("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, CaffeineCache.class.getSimpleName());
		}
		return caffeineCache;
	}

	public static CaffeineCache getCache(String cacheName) {
		return getCache(cacheName, false);
	}

	/**
	 * timeToLiveSeconds和timeToIdleSeconds不允许同时为null
	 * @param cacheName
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @return
	 * @author tanyaowu
	 */
	public static CaffeineCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		CaffeineCache CaffeineCache = register(cacheName, timeToLiveSeconds, timeToIdleSeconds, null);
		return CaffeineCache;
	}

	public static CaffeineCache register(String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds, RemovalListener<String, Serializable> removalListener) {
		CaffeineCache caffeineCache = map.get(cacheName);
		if (caffeineCache == null) {
			synchronized (CaffeineCache.class) {
				caffeineCache = map.get(cacheName);
				if (caffeineCache == null) {
					Integer initialCapacity = 10;
					Integer maximumSize = 5000000;
					boolean recordStats = false;
					LoadingCache<String, Serializable> loadingCache = CaffeineUtils.createLoadingCache(cacheName, timeToLiveSeconds, timeToIdleSeconds, initialCapacity,
					        maximumSize, recordStats, removalListener);

					Integer temporaryMaximumSize = 500000;
					LoadingCache<String, Serializable> temporaryLoadingCache = CaffeineUtils.createLoadingCache(cacheName, 10L, (Long) null, initialCapacity, temporaryMaximumSize,
					        recordStats, removalListener);
					caffeineCache = new CaffeineCache(cacheName, loadingCache, temporaryLoadingCache);

					caffeineCache.setTimeToIdleSeconds(timeToIdleSeconds);
					caffeineCache.setTimeToLiveSeconds(timeToLiveSeconds);

					map.put(cacheName, caffeineCache);
				}
			}
		}
		return caffeineCache;
	}

	//

	private LoadingCache<String, Serializable> loadingCache = null;

	private LoadingCache<String, Serializable> temporaryLoadingCache = null;

	private CaffeineCache(String cacheName, LoadingCache<String, Serializable> loadingCache, LoadingCache<String, Serializable> temporaryLoadingCache) {
		super(cacheName);
		this.loadingCache = loadingCache;
		this.temporaryLoadingCache = temporaryLoadingCache;
	}

	@Override
	public void clear() {
		loadingCache.invalidateAll();
		temporaryLoadingCache.invalidateAll();
	}

	@Override
	public Serializable get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}
		Serializable ret = loadingCache.getIfPresent(key);
		if (ret == null) {
			ret = temporaryLoadingCache.getIfPresent(key);
		}

		return ret;
	}

	@Override
	public Collection<String> keys() {
		ConcurrentMap<String, Serializable> map = loadingCache.asMap();
		return map.keySet();
	}

	@Override
	public void put(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		loadingCache.put(key, value);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		temporaryLoadingCache.put(key, value);
	}

	@Override
	public void remove(String key) {
		if (StrUtil.isBlank(key)) {
			return;
		}
		loadingCache.invalidate(key);
		temporaryLoadingCache.invalidate(key);
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public ConcurrentMap<String, Serializable> asMap() {
		return loadingCache.asMap();
	}

	/**
	 * 
	 * @return
	 * @author: tanyaowu
	 */
	public long size() {
		return loadingCache.estimatedSize();//.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}

	@Override
	public long ttl(String key) {
		throw new RuntimeException("不支持ttl");
	}
}
