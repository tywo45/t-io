package org.tio.core.maintain;

import java.util.concurrent.locks.Lock;

import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.tio.core.ChannelContext;
import org.tio.core.ObjWithLock;
import org.tio.core.intf.Packet;

/**
 * The Class Users.
 *
 * @param <Ext> the generic type
 * @param <P> the generic type
 * @param <R> the generic type
 */
public class Users<SessionContext, P extends Packet, R> {

	/**
	 * key: userid
	 * value: ChannelContext
	 */
	private ObjWithLock<DualHashBidiMap<String, ChannelContext<SessionContext, P, R>>> map = new ObjWithLock<DualHashBidiMap<String, ChannelContext<SessionContext, P, R>>>(
			new DualHashBidiMap<String, ChannelContext<SessionContext, P, R>>());

	/**
	 * @return the map
	 */
	public ObjWithLock<DualHashBidiMap<String, ChannelContext<SessionContext, P, R>>> getMap() {
		return map;
	}

	/**
	 * 解除绑定
	 *
	 * @param channelContext the channel context
	 */
	public void unbind(ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext<SessionContext, P, R>> m = map.getObj();
		try {
			lock.lock();
			m.removeValue(channelContext);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 解除绑定
	 *
	 * @param userid the userid
	 * @author: tanyaowu
	 */
	public void unbind(String userid) {
		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext<SessionContext, P, R>> m = map.getObj();
		try {
			lock.lock();
			m.remove(userid);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 绑定userid.
	 *
	 * @param userid the userid
	 * @param channelContext the channel context
	 * @author: tanyaowu
	 */
	public void bind(String userid, ChannelContext<SessionContext, P, R> channelContext) {
		String key = userid;
		Lock lock = map.getLock().writeLock();
		DualHashBidiMap<String, ChannelContext<SessionContext, P, R>> m = map.getObj();

		try {
			lock.lock();
			m.put(key, channelContext);
			channelContext.setUserid(userid);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Find.
	 *
	 * @param userid the userid
	 * @return the channel context
	 */
	public ChannelContext<SessionContext, P, R> find(String userid) {
		String key = userid;
		Lock lock = map.getLock().readLock();
		DualHashBidiMap<String, ChannelContext<SessionContext, P, R>> m = map.getObj();

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
