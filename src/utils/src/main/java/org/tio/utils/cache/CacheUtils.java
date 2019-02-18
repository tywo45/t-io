package org.tio.utils.cache;

import java.io.Serializable;

import org.redisson.api.RedissonClient;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.cache.caffeineredis.CaffeineRedisCache;
import org.tio.utils.lock.LockUtils;

/**
 * cache使用的一些工具方法，简化业务代码
 * @author tanyaowu
 *
 */
public abstract class CacheUtils {

	private static final String PREFIX_TIMETOLIVESECONDS = CacheUtils.class.getName() + "_live";

	private static final String PREFIX_TIMETOIDLESECONDS = CacheUtils.class.getName() + "_idle";

	private static final Object LOCK_FOR_GETCACHE = new Object();

	private CacheUtils() {
	}

	private static class NullClass implements Serializable {
		private static final long serialVersionUID = -2298613658358477523L;
	}

	/**
	 * 用于临时存放于缓存中的对象，防止缓存null攻击
	 */
	private static final NullClass NULL_OBJ = new NullClass();

	/**
	 * 根据cacheKey从缓存中获取对象，如果缓存中没有该key对象，则用firsthandCreater获取对象，并将对象用cacheKey存于cache中
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @param cacheKey
	 * @param firsthandCreater
	 * @return
	 * @author tanyaowu
	 */
	public static <T extends Serializable> T get(Long timeToLiveSeconds, Long timeToIdleSeconds, String cacheKey, FirsthandCreater<T> firsthandCreater) {
		return get(timeToLiveSeconds, timeToIdleSeconds, cacheKey, false, firsthandCreater);
	}

	/**
	 * 根据cacheKey从缓存中获取对象，如果缓存中没有该key对象，则用firsthandCreater获取对象，并将对象用cacheKey存于cache中
	 * timeToLiveSeconds和timeToIdleSeconds一个传null一个传值
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @param cacheKey 请业务侧保证cacheKey的唯一性，建议的做法是由prefix + key组成，譬如"user.124578"，其中user就是prefix，124578就是key
	 * @param putTempToCacheIfNull 当FirsthandCreater获取不到对象时，是否使用临时对象，以防缓存攻击。true:可以防止缓存null攻击
	 * @param firsthandCreater
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public static <T extends Serializable> T get(Long timeToLiveSeconds, Long timeToIdleSeconds, String cacheKey, boolean putTempToCacheIfNull,
	        FirsthandCreater<T> firsthandCreater) {
		CaffeineCache cache = getCaffeineCache(timeToLiveSeconds, timeToIdleSeconds);
		return get(cache, cacheKey, putTempToCacheIfNull, firsthandCreater);
	}

	/**
	 * 根据cacheKey从缓存中获取对象，如果缓存中没有该key对象，则用firsthandCreater获取对象，并将对象用cacheKey存于cache中
	 * @param cache
	 * @param cacheKey
	 * @param firsthandCreater
	 * @return
	 * @author tanyaowu
	 */
	public static <T extends Serializable> T get(ICache cache, String cacheKey, FirsthandCreater<T> firsthandCreater) {
		return get(cache, cacheKey, false, firsthandCreater);
	}

	/**
	 * 根据cacheKey从缓存中获取对象，如果缓存中没有该key对象，则用firsthandCreater获取对象，并将对象用cacheKey存于cache中
	 * @param cache
	 * @param cacheKey
	 * @param putTempToCacheIfNull 当FirsthandCreater获取不到对象时，是否使用临时对象，以防缓存攻击。true:可以防止缓存null攻击
	 * @param firsthandCreater
	 * @return
	 * @author tanyaowu
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T get(ICache cache, String cacheKey, boolean putTempToCacheIfNull, FirsthandCreater<T> firsthandCreater) {
		Serializable ret = cache.get(cacheKey);
		if (ret != null) {
			if (ret instanceof NullClass) {
				return null;
			}
			return (T) ret;
		}

		String lockKey = cache.getCacheName() + cacheKey;
		Object lock = LockUtils.getLockObj(lockKey, cache);

		synchronized (lock) {
			ret = cache.get(cacheKey);
			if (ret != null) {
				if (ret instanceof NullClass) {
					return null;
				}
				return (T) ret;
			}

			ret = firsthandCreater.create();
			if (ret == null) {
				if (putTempToCacheIfNull) {
					cache.putTemporary(cacheKey, NULL_OBJ);
				}
			} else {
				cache.put(cacheKey, ret);
			}
		}

		return (T) ret;
	}

	/**
	 * 根据参数获取或创建CaffeineCache对象
	 * @param timeToLiveSeconds
	 * @param timeToIdleSeconds
	 * @return
	 * @author tanyaowu
	 */
	public static CaffeineCache getCaffeineCache(Long timeToLiveSeconds, Long timeToIdleSeconds) {
		String cacheName = getCacheName(timeToLiveSeconds, timeToIdleSeconds);
		CaffeineCache caffeineCache = CaffeineCache.getCache(cacheName, true);
		if (caffeineCache == null) {
			synchronized (LOCK_FOR_GETCACHE) {
				caffeineCache = CaffeineCache.getCache(cacheName, true);
				if (caffeineCache == null) {
					caffeineCache = CaffeineCache.register(cacheName, timeToLiveSeconds, timeToIdleSeconds);
				}
			}
		}

		return caffeineCache;
	}

	public static CaffeineRedisCache getCaffeineRedisCache(RedissonClient redisson, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		String cacheName = getCacheName(timeToLiveSeconds, timeToIdleSeconds);
		CaffeineRedisCache caffeineCache = CaffeineRedisCache.getCache(cacheName, true);
		if (caffeineCache == null) {
			synchronized (LOCK_FOR_GETCACHE) {
				caffeineCache = CaffeineRedisCache.getCache(cacheName, true);
				if (caffeineCache == null) {
					caffeineCache = CaffeineRedisCache.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);
				}
			}
		}

		return caffeineCache;
	}

	private static String getCacheName(Long timeToLiveSeconds, Long timeToIdleSeconds) {
		if (timeToLiveSeconds != null) {
			return PREFIX_TIMETOLIVESECONDS + timeToLiveSeconds;
		} else if (timeToIdleSeconds != null) {
			return PREFIX_TIMETOIDLESECONDS + timeToIdleSeconds;
		} else {
			throw new RuntimeException("timeToLiveSeconds和timeToIdleSeconds不允许同时为空");
		}
	}

	public static void main(String[] args) {
	}

}
