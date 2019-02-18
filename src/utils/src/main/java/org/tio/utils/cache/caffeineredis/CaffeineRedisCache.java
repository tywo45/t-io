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

/**
 * @author tanyaowu
 * 2017年8月12日 下午9:13:54
 */
public class CaffeineRedisCache extends AbsCache {

	public static final String CACHE_CHANGE_TOPIC = "TIO_CACHE_CHANGE_TOPIC_CAFFEINE";

	private static Logger							log	= LoggerFactory.getLogger(CaffeineRedisCache.class);
	public static Map<String, CaffeineRedisCache>	map	= new HashMap<>();

	public static RTopic topic;

	private static boolean inited = false;

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
								caffeineRedisCache.caffeineCache.remove(key);
							} else if (type == CacheChangeType.CLEAR) {
								caffeineRedisCache.caffeineCache.clear();
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

	CaffeineCache caffeineCache;

	RedisCache redisCache;

	/**
	 * @param caffeineCache
	 * @param redisCache
	 * @author tanyaowu
	 */
	public CaffeineRedisCache(String cacheName, CaffeineCache caffeineCache, RedisCache redisCache) {
		super(cacheName);
		this.caffeineCache = caffeineCache;
		this.redisCache = redisCache;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	@Override
	public void clear() {
		caffeineCache.clear();
		redisCache.clear();

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, CacheChangeType.CLEAR);
		topic.publish(cacheChangedVo);
	}

	/**
	 * @param key
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public Serializable get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}

		Serializable ret = caffeineCache.get(key);
		if (ret == null) {
			ret = redisCache.get(key);
			if (ret != null) {
				caffeineCache.put(key, ret);
			}
		} else {//在本地就取到数据了，那么需要在redis那定时更新一下过期时间
			Long timeToIdleSeconds = redisCache.getTimeToIdleSeconds();
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
		return redisCache.keys();
	}

	/**
	 * @param key
	 * @param value
	 * @author tanyaowu
	 */
	@Override
	public void put(String key, Serializable value) {
		caffeineCache.put(key, value);
		redisCache.put(key, value);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		topic.publish(cacheChangedVo);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		caffeineCache.putTemporary(key, value);
		redisCache.putTemporary(key, value);

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

		caffeineCache.remove(key);
		redisCache.remove(key);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.REMOVE);
		topic.publish(cacheChangedVo);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key, Class<T> clazz) {
		return (T) get(key);
	}

	@Override
	public long ttl(String key) {
		return redisCache.ttl(key);
	}

}
