package org.tio.utils.cache.caffeineredis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.cache.AbsCache;
import org.tio.utils.cache.CacheChangeType;
import org.tio.utils.cache.CacheChangedVo;
import org.tio.utils.cache.caffeine.CaffeineCache;
import org.tio.utils.cache.redis.RedisCache;
import org.tio.utils.cache.redis.RedisExpireUpdateTask;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.LockUtils;
import org.tio.utils.lock.ReadWriteLockHandler;
import org.tio.utils.lock.ReadWriteLockHandler.ReadWriteRet;

/**
 * @author tanyaowu
 * 2017年8月12日 下午9:13:54
 */
public class CaffeineRedisCache extends AbsCache {
	public static final String						CACHE_CHANGE_TOPIC	= "TIO_CACHE_CHANGE_TOPIC_CAFFEINE";
	private static Logger							log					= LoggerFactory.getLogger(CaffeineRedisCache.class);
	public static Map<String, CaffeineRedisCache>	map					= new HashMap<>();
	public static RTopic							topic;
	private static boolean							inited				= false;
	CaffeineCache									localCache			= null;
	RedisCache										distCache			= null;

	public static CaffeineRedisCache getCache(String cacheName, boolean skipNull) {
		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null && !skipNull) {
			log.warn("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, CaffeineRedisCache.class.getSimpleName());
		}
		return caffeineRedisCache;
	}

	public static CaffeineRedisCache getCache(String cacheName) {
		return getCache(cacheName, false);
	}

	private static void init(RedissonClient redisson) {
		if (!inited) {
			synchronized (CaffeineRedisCache.class) {
				if (!inited) {
					topic = redisson.getTopic(CACHE_CHANGE_TOPIC);
					topic.addListener(CacheChangedVo.class, new MessageListener<CacheChangedVo>() {
						@Override
						public void onMessage(CharSequence channel, CacheChangedVo cacheChangedVo) {
							String clientid = cacheChangedVo.getClientId();
							if (StrUtil.isBlank(clientid)) {
								log.error("clientid is null");
								return;
							}
							if (Objects.equals(CacheChangedVo.CLIENTID, clientid)) {
								log.debug("自己发布的消息,{}", clientid);
								return;
							}

							String cacheName = cacheChangedVo.getCacheName();
							CaffeineRedisCache caffeineRedisCache = CaffeineRedisCache.getCache(cacheName);
							if (caffeineRedisCache == null) {
								log.info("不能根据cacheName[{}]找到CaffeineRedisCache对象", cacheName);
								return;
							}

							CacheChangeType type = cacheChangedVo.getType();
							if (type == CacheChangeType.PUT || type == CacheChangeType.UPDATE || type == CacheChangeType.REMOVE) {
								String key = cacheChangedVo.getKey();
								caffeineRedisCache.localCache.remove(key);
							} else if (type == CacheChangeType.CLEAR) {
								caffeineRedisCache.localCache.clear();
							}
						}
					});
					inited = true;
				}
			}
		}
	}

	public static CaffeineRedisCache register(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		init(redisson);

		CaffeineRedisCache caffeineRedisCache = map.get(cacheName);
		if (caffeineRedisCache == null) {
			synchronized (CaffeineRedisCache.class) {
				caffeineRedisCache = map.get(cacheName);
				if (caffeineRedisCache == null) {
					RedisCache redisCache = RedisCache.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);

					Long timeToLiveSecondsForCaffeine = timeToLiveSeconds;
					Long timeToIdleSecondsForCaffeine = timeToIdleSeconds;

					if (timeToLiveSecondsForCaffeine != null) {
						timeToLiveSecondsForCaffeine = Math.min(timeToLiveSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					if (timeToIdleSecondsForCaffeine != null) {
						timeToIdleSecondsForCaffeine = Math.min(timeToIdleSecondsForCaffeine, MAX_EXPIRE_IN_LOCAL);
					}
					CaffeineCache caffeineCache = CaffeineCache.register(cacheName, timeToLiveSecondsForCaffeine, timeToIdleSecondsForCaffeine);

					caffeineRedisCache = new CaffeineRedisCache(cacheName, caffeineCache, redisCache);

					caffeineRedisCache.setTimeToIdleSeconds(timeToIdleSeconds);
					caffeineRedisCache.setTimeToLiveSeconds(timeToLiveSeconds);

					map.put(cacheName, caffeineRedisCache);
				}
			}
		}
		return caffeineRedisCache;
	}

	/**
	 * @param localCache
	 * @param distCache
	 * @author tanyaowu
	 */
	public CaffeineRedisCache(String cacheName, CaffeineCache caffeineCache, RedisCache redisCache) {
		super(cacheName);
		this.localCache = caffeineCache;
		this.distCache = redisCache;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	@Override
	public void clear() {
		localCache.clear();
		distCache.clear();

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, CacheChangeType.CLEAR);
		topic.publish(cacheChangedVo);
	}

	/**
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Serializable _get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}

		Serializable ret = localCache.get(key);
		if (ret == null) {
			ReadWriteRet readWriteRet = null;
			try {
				readWriteRet = LockUtils.runReadOrWrite("_tio_cr_" + key, this, new ReadWriteLockHandler() {
					@Override
					public Object read() {
						return null;
					}

					@Override
					public Object write() {
						Serializable ret = distCache.get(key);
						if (ret != null) {
							localCache.put(key, ret);
						}
						return ret;
					}
					
				});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
			ret = (Serializable) readWriteRet.writeRet;
//			///////////////////////////////////
//			
//			ReentrantReadWriteLock rWriteLock = LockUtils.getReentrantReadWriteLock("_tio_cr_" + key, this);
//			WriteLock writeLock = rWriteLock.writeLock();
//			boolean tryWrite = writeLock.tryLock();
//			if (tryWrite) {
//				try {
//					ret = distCache.get(key);
//					if (ret != null) {
//						localCache.put(key, ret);
//					} 
//				} finally {
//					writeLock.unlock();
//				}
//			} else {
//				ReadLock readLock = rWriteLock.readLock();
//				boolean tryRead = false;
//				try {
//					tryRead = readLock.tryLock(120, TimeUnit.SECONDS);
//					if (tryRead) {
//						//获取read锁，仅仅是为了等待写锁的完成，所以获取后，立即释放
//						readLock.unlock();
//					}
//				} catch (InterruptedException e) {
//					log.error(e.toString(), e);
//				}
//			}
		} else {//在本地就取到数据了，那么需要在redis那定时更新一下过期时间
			Long timeToIdleSeconds = distCache.getTimeToIdleSeconds();
			if (timeToIdleSeconds != null) {
				RedisExpireUpdateTask.add(cacheName, key, timeToIdleSeconds);
			}
		}
		return ret;
	}

	/**
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Iterable<String> keys() {
		return distCache.keys();
	}

	/**
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	@Override
	public void put(String key, Serializable value) {
		localCache.put(key, value);
		distCache.put(key, value);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		topic.publish(cacheChangedVo);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		localCache.putTemporary(key, value);
		distCache.putTemporary(key, value);

		//
		//		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		//		topic.publish(cacheChangedVo);
	}

	/**
	 * @param key
	 * @author tanyaowu
	 */
	@Override
	public void remove(String key) {
		if (StrUtil.isBlank(key)) {
			return;
		}

		localCache.remove(key);
		distCache.remove(key);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.REMOVE);
		topic.publish(cacheChangedVo);
	}

	@Override
	public long ttl(String key) {
		return distCache.ttl(key);
	}

}
