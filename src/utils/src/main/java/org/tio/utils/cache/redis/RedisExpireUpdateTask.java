package org.tio.utils.cache.redis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.redisson.api.RBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.utils.lock.SetWithLock;

/**
 * 定时更新redis的过期时间
 * @author tanyaowu
 * 2017年8月14日 下午1:34:06
 */
public class RedisExpireUpdateTask {
	private static Logger log = LoggerFactory.getLogger(RedisExpireUpdateTask.class);

	private static boolean started = false;

	private static Set<ExpireVo> set = new HashSet<>();

	private static SetWithLock<ExpireVo> setWithLock = new SetWithLock<>(set);

	public static void add(String cacheName, String key, long expire) {
		ExpireVo expireVo = new ExpireVo(cacheName, key, expire);
		setWithLock.add(expireVo);
	}

	public static void start() {
		if (started) {
			return;
		}
		synchronized (RedisExpireUpdateTask.class) {
			if (started) {
				return;
			}
			started = true;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					WriteLock writeLock = setWithLock.writeLock();
					writeLock.lock();
					try {
						Set<ExpireVo> set = setWithLock.getObj();
						for (ExpireVo expireVo : set) {
							log.debug("更新缓存过期时间, cacheName:{}, key:{}, expire:{}", expireVo.getCacheName(), expireVo.getKey(), expireVo.getTimeToIdleSeconds());

							RedisCache redisCache = RedisCache.getCache(expireVo.getCacheName());
							RBucket<Serializable> bucket = redisCache.getBucket(expireVo.getKey());
							bucket.expireAsync(expireVo.getTimeToIdleSeconds(), TimeUnit.SECONDS);
						}
						set.clear();
					} catch (Throwable e) {
						log.error(e.getMessage(), e);
					} finally {
						writeLock.unlock();
						try {
							Thread.sleep(1000 * 10);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
					}
				}

			}
		}, RedisExpireUpdateTask.class.getName()).start();
	}

	/**
	 *
	 * @author tanyaowu
	 */
	private RedisExpireUpdateTask() {
		//		this.redisson = redisson;
	}
}
