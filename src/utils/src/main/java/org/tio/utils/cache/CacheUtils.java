package org.tio.utils.cache;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.cache.caffeineredis.CaffeineRedisCache;
import org.tio.utils.lock.LockUtils;

/**
 * cache使用的一些工具方法，简化业务代码
 * @author tanyaowu
 *
 */
public abstract class CacheUtils {
	private static Logger		log							= LoggerFactory.getLogger(CacheUtils.class);
	private static final String	PREFIX_TIMETOLIVESECONDS	= CacheUtils.class.getName() + "_live";
	private static final String	PREFIX_TIMETOIDLESECONDS	= CacheUtils.class.getName() + "_idle";
	private static final Object	LOCK_FOR_GETCACHE			= new Object();

	private CacheUtils() {
	}





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

	@SuppressWarnings("unchecked")
	private static <T extends Serializable> T getFromCacheOnly(ICache cache, String cacheKey) {
		return (T) cache.get(cacheKey);
//		Serializable ret = cache.get(cacheKey);
//		if (ret != null) {
//			if (ret instanceof NullClass) {
//				return null;
//			}
//			return (T) ret;
//		}
//		return null;
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
	public static <T extends Serializable> T get(ICache cache, String cacheKey, boolean putTempToCacheIfNull, FirsthandCreater<T> firsthandCreater) {
		return get(cache, cacheKey, putTempToCacheIfNull, firsthandCreater, 60L);
	}

	/**
	 * 
	 * @param <T>
	 * @param cache
	 * @param cacheKey
	 * @param putTempToCacheIfNull
	 * @param firsthandCreater
	 * @param readTimeoutWithSeconds 获取读锁的超时时间
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Serializable> T get(ICache cache, String cacheKey, boolean putTempToCacheIfNull, FirsthandCreater<T> firsthandCreater, Long readTimeoutWithSeconds) {
		Serializable ret = getFromCacheOnly(cache, cacheKey);
		if (ret != null) {
			return (T) ret;
		}

		String lockKey = cache.getCacheName() + cacheKey;
		ReentrantReadWriteLock rwLock = LockUtils.getReentrantReadWriteLock(lockKey, cache);
		WriteLock writeLock = rwLock.writeLock();
		boolean tryWrite = writeLock.tryLock();
		if (tryWrite) {
			try {
				ret = getFromCacheOnly(cache, cacheKey);
				if (ret != null) {
					return (T) ret;
				}

				try {
					ret = firsthandCreater.create();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				if (ret == null) {
					if (putTempToCacheIfNull) {
						cache.putTemporary(cacheKey, org.tio.utils.cache.ICache.NULL_OBJ);
					}
				} else {
					cache.put(cacheKey, ret);
				}
			} finally {
				writeLock.unlock();
			}
		} else {
			ReadLock readLock = rwLock.readLock();
			boolean tryRead = false;
			try {
				if (readTimeoutWithSeconds == null || readTimeoutWithSeconds <= 0) {
					readTimeoutWithSeconds = 60L;
				}
				tryRead = readLock.tryLock(readTimeoutWithSeconds, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				log.error(e.toString(), e);
			}
			if (tryRead) {
				try {
					ret = getFromCacheOnly(cache, cacheKey);
					if (ret != null) {
						return (T) ret;
					}
				} finally {
					readLock.unlock();
				}
			} else {
				return null;
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
