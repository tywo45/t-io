package org.tio.core.maintain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.tio.core.ChannelContext;
import org.tio.core.MapWithLock;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 * 2017年4月15日 下午12:13:19
 */
public class Ids<SessionContext, P extends Packet, R> {

	/**
	 * key: id
	 * value: ChannelContext
	 */
	private MapWithLock<String, ChannelContext<SessionContext, P, R>> map = new MapWithLock<String, ChannelContext<SessionContext, P, R>>(
			new HashMap<String, ChannelContext<SessionContext, P, R>>());

	/**
	 * @return the map
	 */
	public MapWithLock<String, ChannelContext<SessionContext, P, R>> getMap() {
		return map;
	}

	/**
	 * 
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock = map.getLock().writeLock();
		Map<String, ChannelContext<SessionContext, P, R>> m = map.getObj();
		try {
			lock.lock();
			m.remove(channelContext.getId());
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void bind(ChannelContext<SessionContext, P, R> channelContext) {
		String key = channelContext.getId();
		Lock lock = map.getLock().writeLock();
		Map<String, ChannelContext<SessionContext, P, R>> m = map.getObj();

		try {
			lock.lock();
			m.put(key, channelContext);
			//			channelContext.setId(id);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Find.
	 *
	 * @param id the id
	 * @return the channel context
	 */
	public ChannelContext<SessionContext, P, R> find(String id) {
		String key = id;
		Lock lock = map.getLock().readLock();
		Map<String, ChannelContext<SessionContext, P, R>> m = map.getObj();

		try {
			lock.lock();
			return m.get(key);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}
}
