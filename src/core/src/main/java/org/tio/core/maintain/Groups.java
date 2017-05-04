package org.tio.core.maintain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.ObjWithLock;
import org.tio.core.intf.Packet;

public class Groups<SessionContext, P extends Packet, R> {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Groups.class);

	/** 一个组有哪些客户端
	 * key: groupid
	 * value: Set<ChannelContext<?, ?, ?>
	 */
	private ObjWithLock<Map<String, ObjWithLock<Set<ChannelContext<SessionContext, P, R>>>>> groupmap = new ObjWithLock<Map<String, ObjWithLock<Set<ChannelContext<SessionContext, P, R>>>>>(
			new ConcurrentHashMap<String, ObjWithLock<Set<ChannelContext<SessionContext, P, R>>>>());

	/** 一个客户端在哪组组中
	 *  key: ChannelContext
	 *  value: Set<groupid<?, ?, ?>
	 */
	private ObjWithLock<Map<ChannelContext<SessionContext, P, R>, ObjWithLock<Set<String>>>> channelmap = new ObjWithLock<Map<ChannelContext<SessionContext, P, R>, ObjWithLock<Set<String>>>>(
			new ConcurrentHashMap<ChannelContext<SessionContext, P, R>, ObjWithLock<Set<String>>>());

	/**
	 * @return the groupmap
	 */
	public ObjWithLock<Map<String, ObjWithLock<Set<ChannelContext<SessionContext, P, R>>>>> getGroupmap() {
		return groupmap;
	}

	/**
	 * @return the channelmap
	 */
	public ObjWithLock<Map<ChannelContext<SessionContext, P, R>, ObjWithLock<Set<String>>>> getChannelmap() {
		return channelmap;
	}

	/**
	 * 与所有组解除绑定
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock = channelmap.getLock().writeLock();
		try {
			ObjWithLock<Set<String>> set = null;
			try {
				lock.lock();
				Map<ChannelContext<SessionContext, P, R>, ObjWithLock<Set<String>>> m = channelmap.getObj();
				set = m.get(channelContext);
				m.remove(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock.unlock();
			}

			if (set != null) {
				Set<String> groups = set.getObj();
				if (groups != null && groups.size() > 0) {
					for (String groupid : groups) {
						unbind(groupid, channelContext);
					}
				}
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 与指定组解除绑定
	 * @param groupid
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void unbind(String groupid, ChannelContext<SessionContext, P, R> channelContext) {
		ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> set = groupmap.getObj().get(groupid);

		if (set != null) {
			Lock lock1 = set.getLock().writeLock();
			try {
				lock1.lock();
				set.getObj().remove(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock1.unlock();
			}

			if (set.getObj().size() == 0) {
				Lock lock2 = groupmap.getLock().writeLock();
				try {
					lock2.lock();
					groupmap.getObj().remove(groupid);
				} catch (Exception e) {
					log.error(e.toString(), e);
				} finally {
					lock2.unlock();
				}
			}
		}
	}

	/**
	 * 和组绑定
	 * @param groupid
	 * @param channelContext
	 * @author: tanyaowu
	 */
	public void bind(String groupid, ChannelContext<SessionContext, P, R> channelContext) {
		Lock lock1 = groupmap.getLock().writeLock();
		ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> channelContexts = null;
		try {
			lock1.lock();
			channelContexts = groupmap.getObj().get(groupid);
			if (channelContexts == null) {
				channelContexts = new ObjWithLock<Set<ChannelContext<SessionContext, P, R>>>(new HashSet<ChannelContext<SessionContext, P, R>>());
			}
			groupmap.getObj().put(groupid, channelContexts);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			lock1.unlock();
		}

		if (channelContexts != null) {
			Lock lock11 = channelContexts.getLock().writeLock();
			try {
				lock11.lock();
				channelContexts.getObj().add(channelContext);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock11.unlock();
			}
		}

		Lock lock2 = channelmap.getLock().writeLock();
		ObjWithLock<Set<String>> groups = null;// = channelmap.getObj().get(channelContext);
		try {
			lock2.lock();
			groups = channelmap.getObj().get(channelContext);
			if (groups == null) {
				groups = new ObjWithLock<Set<String>>(new HashSet<String>());
			}
			channelmap.getObj().put(channelContext, groups);
		} catch (Exception e) {
			log.error(e.toString(), e);
		} finally {
			lock2.unlock();
		}

		if (groups != null) {
			Lock lock22 = groups.getLock().writeLock();
			try {
				lock22.lock();
				groups.getObj().add(groupid);
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				lock22.unlock();
			}
		}
	}

	/**
	 * 一个组有哪些客户端
	 * @param groupid
	 * @return
	 * @author: tanyaowu
	 */
	public ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> clients(String groupid) {
		ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> set = groupmap.getObj().get(groupid);
		return set;
	}

	/**
	 * 某个客户端在哪些组中
	 * @param channelContext
	 * @return
	 * @author: tanyaowu
	 */
	public ObjWithLock<Set<String>> groups(ChannelContext<SessionContext, P, R> channelContext) {
		ObjWithLock<Set<String>> set = channelmap.getObj().get(channelContext);
		return set;
	}
}
