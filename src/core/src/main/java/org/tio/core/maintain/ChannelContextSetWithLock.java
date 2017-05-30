package org.tio.core.maintain;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import org.tio.core.ChannelContext;
import org.tio.core.SetWithLock;
import org.tio.core.intf.Packet;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:35:09
 */
public class ChannelContextSetWithLock<SessionContext, P extends Packet, R> {

	/** remoteAndChannelContext key: "ip:port" value: ChannelContext. */
	private SetWithLock<ChannelContext<SessionContext, P, R>> setWithLock = new SetWithLock<ChannelContext<SessionContext, P, R>>(
			new HashSet<ChannelContext<SessionContext, P, R>>());

	public void add(ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock = setWithLock.getLock().writeLock();

		try {
			lock.lock();
			Set<ChannelContext<SessionContext, P, R>> m = setWithLock.getObj();
			m.add(channelContext);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public boolean remove(ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock = setWithLock.getLock().writeLock();

		try {
			lock.lock();
			Set<ChannelContext<SessionContext, P, R>> m = setWithLock.getObj();
			return m.remove(channelContext);
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public int size() {
		Lock lock = setWithLock.getLock().readLock();

		try {
			lock.lock();
			Set<ChannelContext<SessionContext, P, R>> m = setWithLock.getObj();
			return m.size();
		} catch (Exception e) {
			throw e;
		} finally {
			lock.unlock();
		}
	}

	public SetWithLock<ChannelContext<SessionContext, P, R>> getSetWithLock() {
		return setWithLock;
	}

}
