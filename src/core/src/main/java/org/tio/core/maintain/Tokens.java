package org.tio.core.maintain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 *一对多  (token <--> ChannelContext)<br>
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:40
 */
public class Tokens {
	private static Logger log = LoggerFactory.getLogger(Tokens.class);

	/**
	 * key: token
	 * value: SetWithLock<ChannelContext>
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> mapWithLock = new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());

	/**
	 * 绑定token.
	 *
	 * @param token the token
	 * @param channelContext the channel context
	 * @author tanyaowu
	 */
	public void bind(String token, ChannelContext channelContext) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			if (StrUtil.isBlank(token)) {
				return;
			}
			String key = token;
			Lock lock = mapWithLock.writeLock();
			lock.lock();
			try {
				Map<String, SetWithLock<ChannelContext>> map = mapWithLock.getObj();
				SetWithLock<ChannelContext> setWithLock = map.get(key);
				if (setWithLock == null) {
					setWithLock = new SetWithLock<>(new HashSet<>());
					map.put(key, setWithLock);
				}
				setWithLock.add(channelContext);

				//			cacheMap.put(key, channelContext);

				channelContext.setToken(token);
			} catch (Throwable e) {
				throw e;
			} finally {
				lock.unlock();
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * Find.
	 *
	 * @param token the token
	 * @return the channel context
	 */
	public SetWithLock<ChannelContext> find(GroupContext groupContext, String token) {
		if (groupContext.isShortConnection) {
			return null;
		}

		if (StrUtil.isBlank(token)) {
			return null;
		}
		String key = token;
		Lock lock = mapWithLock.readLock();
		lock.lock();
		try {
			Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
			return m.get(key);
		} catch (Throwable e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * @return the mapWithLock
	 */
	public MapWithLock<String, SetWithLock<ChannelContext>> getMap() {
		return mapWithLock;
	}

	/**
	 * 解除channelContext绑定的token
	 *
	 * @param channelContext the channel context
	 */
	public void unbind(ChannelContext channelContext) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			String token = channelContext.getToken();
			if (StrUtil.isBlank(token)) {
				log.debug("{}, {}, 并没有绑定Token", groupContext.getName(), channelContext.toString());
				return;
			}

			Lock lock = mapWithLock.writeLock();
			lock.lock();
			try {
				Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
				SetWithLock<ChannelContext> setWithLock = m.get(token);
				if (setWithLock == null) {
					log.warn("{}, {}, token:{}, 没有找到对应的SetWithLock", groupContext.getName(), channelContext.toString(), token);
					return;
				}
				channelContext.setToken(null);
				setWithLock.remove(channelContext);

				if (setWithLock.getObj().size() == 0) {
					m.remove(token);
				}
			} catch (Throwable e) {
				throw e;
			} finally {
				lock.unlock();
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 解除groupContext范围内所有ChannelContext的 token绑定
	 *
	 * @param token the token
	 * @author tanyaowu
	 */
	public void unbind(GroupContext groupContext, String token) {
		try {
			if (groupContext.isShortConnection) {
				return;
			}

			if (StrUtil.isBlank(token)) {
				return;
			}

			Lock lock = mapWithLock.writeLock();
			lock.lock();
			try {
				Map<String, SetWithLock<ChannelContext>> m = mapWithLock.getObj();
				SetWithLock<ChannelContext> setWithLock = m.get(token);
				if (setWithLock == null) {
					return;
				}

				WriteLock writeLock = setWithLock.writeLock();
				writeLock.lock();
				try {
					Set<ChannelContext> set = setWithLock.getObj();
					if (set.size() > 0) {
						for (ChannelContext channelContext : set) {
							channelContext.setToken(null);
						}
						set.clear();
					}

					m.remove(token);
				} catch (Throwable e) {
					log.error(e.getMessage(), e);
				} finally {
					writeLock.unlock();
				}

			} catch (Throwable e) {
				throw e;
			} finally {
				lock.unlock();
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}
}
