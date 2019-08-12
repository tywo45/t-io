package org.tio.utils.cache.guavaredis;

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
import org.tio.utils.cache.guava.GuavaCache;
import org.tio.utils.cache.redis.RedisCache;
import org.tio.utils.cache.redis.RedisExpireUpdateTask;
import org.tio.utils.hutool.StrUtil;

/**
 * @author tanyaowu
 * 2017年8月12日 下午9:13:54
 */
public class GuavaRedisCache extends AbsCache {

	public static final String CACHE_CHANGE_TOPIC = "TIO_CACHE_CHANGE_TOPIC_GUAVA";

	private static Logger						log	= LoggerFactory.getLogger(GuavaRedisCache.class);
	public static Map<String, GuavaRedisCache>	map	= new HashMap<>();

	static RTopic topic;

	private static boolean inited = false;

	public static GuavaRedisCache getCache(String cacheName) {
		GuavaRedisCache guavaRedisCache = map.get(cacheName);
		if (guavaRedisCache == null) {
			log.warn("cacheName[{}]还没注册，请初始化时调用：{}.register(cacheName, timeToLiveSeconds, timeToIdleSeconds)", cacheName, GuavaRedisCache.class.getSimpleName());
		}
		return guavaRedisCache;
	}

	private static void init(RedissonClient redisson) {
		if (!inited) {
			synchronized (GuavaRedisCache.class) {
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
							GuavaRedisCache guavaRedisCache = GuavaRedisCache.getCache(cacheName);
							if (guavaRedisCache == null) {
								log.info("不能根据cacheName[{}]找到GuavaRedisCache对象", cacheName);
								return;
							}

							CacheChangeType type = cacheChangedVo.getType();
							if (type == CacheChangeType.PUT || type == CacheChangeType.UPDATE || type == CacheChangeType.REMOVE) {
								String key = cacheChangedVo.getKey();
								guavaRedisCache.guavaCache.remove(key);
							} else if (type == CacheChangeType.CLEAR) {
								guavaRedisCache.guavaCache.clear();
							}
						}
					});
					inited = true;
				}
			}
		}
	}

	public static GuavaRedisCache register(RedissonClient redisson, String cacheName, Long timeToLiveSeconds, Long timeToIdleSeconds) {
		init(redisson);

		GuavaRedisCache guavaRedisCache = map.get(cacheName);
		if (guavaRedisCache == null) {
			synchronized (GuavaRedisCache.class) {
				guavaRedisCache = map.get(cacheName);
				if (guavaRedisCache == null) {
					RedisCache redisCache = RedisCache.register(redisson, cacheName, timeToLiveSeconds, timeToIdleSeconds);

					Long timeToLiveSecondsForGuava = timeToLiveSeconds;
					Long timeToIdleSecondsForGuava = timeToIdleSeconds;

					if (timeToLiveSecondsForGuava != null) {
						timeToLiveSecondsForGuava = Math.min(timeToLiveSecondsForGuava, MAX_EXPIRE_IN_LOCAL);
					}
					if (timeToIdleSecondsForGuava != null) {
						timeToIdleSecondsForGuava = Math.min(timeToIdleSecondsForGuava, MAX_EXPIRE_IN_LOCAL);
					}
					GuavaCache guavaCache = GuavaCache.register(cacheName, timeToLiveSecondsForGuava, timeToIdleSecondsForGuava);

					guavaRedisCache = new GuavaRedisCache(cacheName, guavaCache, redisCache);

					guavaRedisCache.setTimeToIdleSeconds(timeToIdleSeconds);
					guavaRedisCache.setTimeToLiveSeconds(timeToLiveSeconds);

					map.put(cacheName, guavaRedisCache);
				}
			}
		}
		return guavaRedisCache;
	}

	GuavaCache guavaCache;

	RedisCache redisCache;

	/**
	 * @param guavaCache
	 * @param distCache
	 * @author tanyaowu
	 */
	public GuavaRedisCache(String cacheName, GuavaCache guavaCache, RedisCache redisCache) {
		super(cacheName);
		this.guavaCache = guavaCache;
		this.redisCache = redisCache;
	}

	/**
	 *
	 * @author tanyaowu
	 */
	@Override
	public void clear() {
		guavaCache.clear();
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
	public Serializable _get(String key) {
		if (StrUtil.isBlank(key)) {
			return null;
		}

		Serializable ret = guavaCache.get(key);
		if (ret == null) {
			ret = redisCache.get(key);
			if (ret != null) {
				guavaCache.put(key, ret);
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
		guavaCache.put(key, value);
		redisCache.put(key, value);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.PUT);
		topic.publish(cacheChangedVo);
	}

	@Override
	public void putTemporary(String key, Serializable value) {
		guavaCache.putTemporary(key, value);
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

		guavaCache.remove(key);
		redisCache.remove(key);

		CacheChangedVo cacheChangedVo = new CacheChangedVo(cacheName, key, CacheChangeType.REMOVE);
		topic.publish(cacheChangedVo);
	}

	@Override
	public long ttl(String key) {
		return redisCache.ttl(key);
	}

}
