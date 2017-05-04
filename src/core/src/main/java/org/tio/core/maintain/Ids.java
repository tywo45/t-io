package org.tio.core.maintain;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.tio.core.ChannelContext;
import org.tio.core.ObjWithLock;
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
	private ObjWithLock<Map<String, ChannelContext<?, ?, ?>>> map = new ObjWithLock<Map<String, ChannelContext<?, ?, ?>>>(new HashMap<String, ChannelContext<?, ?, ?>>());

	/**
	 * @return the map
	 */
	public ObjWithLock<Map<String, ChannelContext<?, ?, ?>>> getMap() {
		return map;
	}

	/**
	 * 
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(ChannelContext<?, ?, ?> channelContext) {
		Lock lock = map.getLock().writeLock();
		Map<String, ChannelContext<?, ?, ?>> m = map.getObj();
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
	public void bind(ChannelContext<?, ?, ?> channelContext) {
		String key = channelContext.getId();
		Lock lock = map.getLock().writeLock();
		Map<String, ChannelContext<?, ?, ?>> m = map.getObj();

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
	public ChannelContext<?, ?, ?> find(String id) {
		String key = id;
		Lock lock = map.getLock().readLock();
		Map<String, ChannelContext<?, ?, ?>> m = map.getObj();

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
