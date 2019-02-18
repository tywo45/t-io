package org.tio.core.maintain;

import java.util.Comparator;
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
import org.tio.core.intf.GroupListener;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.SetWithLock;

/**
 * 多对多  (group <--> ChannelContext)<br>
 * @author tanyaowu 
 * 2017年10月19日 上午9:40:21
 */
public class Groups {

	/**
	 * 对ChannelContext进行排序的比较器
	 * 该对象必须在服务启动前进行设置，并且不要再去修改，否则会导致有的排序了，有的没有排序
	 */
	private Comparator<ChannelContext> channelContextComparator = null;

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Groups.class);

	/** 一个组有哪些客户端<br>
	 * key: groupid<br>
	 * value: SetWithLock<ChannelContext><br>
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>> groupmap = new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());

	/**
	 * 和组绑定
	 * @param groupid
	 * @param channelContext
	 */
	public void bind(String groupid, ChannelContext channelContext) {
		bind(groupid, channelContext, true);
	}

	/**
	 * 和组绑定
	 * @param groupid
	 * @param channelContext
	 * @param callbackListener 是否回调GroupListener
	 */
	public void bind(String groupid, ChannelContext channelContext, boolean callbackListener) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			if (StrUtil.isBlank(groupid)) {
				return;
			}

			SetWithLock<ChannelContext> channelContexts = null;
			Lock lock1 = groupmap.writeLock();
			lock1.lock();
			try {
				Map<String, SetWithLock<ChannelContext>> map = groupmap.getObj();
				channelContexts = map.get(groupid);
				if (channelContexts == null) {
					channelContexts = new SetWithLock<>(MaintainUtils.createSet(channelContextComparator));
					map.put(groupid, channelContexts);
				}

				SetWithLock<String> set = channelContext.getGroups();
				if (set == null) {
					set = new SetWithLock<String>(new HashSet<>());
					channelContext.setGroups(set);
				}
				set.add(groupid);
				channelContexts.add(channelContext);

				if (callbackListener) {
					GroupListener groupListener = groupContext.getGroupListener();
					if (groupListener != null) {
						try {
							groupListener.onAfterBind(channelContext, groupid);
						} catch (Throwable e) {
							log.error(e.toString(), e);
						}
					}
				}
			} catch (Throwable e) {
				log.error(e.toString(), e);
			} finally {
				lock1.unlock();
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 一个组有哪些客户端
	 * @param groupid
	 * @return
	 * @author tanyaowu
	 */
	public SetWithLock<ChannelContext> clients(GroupContext groupContext, String groupid) {
		if (groupContext.isShortConnection) {
			return null;
		}

		if (StrUtil.isBlank(groupid)) {
			return null;
		}
		return groupmap.get(groupid);
	}

	/**
	 * @return the groupmap
	 */
	public MapWithLock<String, SetWithLock<ChannelContext>> getGroupmap() {
		return groupmap;
	}

	/**
	 * 某个客户端在哪些组中
	 * @param channelContext
	 * @return
	 * @author tanyaowu
	 */
	public SetWithLock<String> groups(ChannelContext channelContext) {
		GroupContext groupContext = channelContext.groupContext;
		if (groupContext.isShortConnection) {
			return null;
		}

		return channelContext.getGroups();
	}

	/**
	 * 与所有组解除绑定
	 * @param channelContext
	 * @author tanyaowu
	 */
	public void unbind(ChannelContext channelContext) {
		unbind(channelContext, true);
	}

	/**
	 * 
	 * @param channelContext
	 * @param callbackListener 是否回调GroupListener
	 */
	public void unbind(ChannelContext channelContext, boolean callbackListener) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			try {
				SetWithLock<String> setWithLock = channelContext.getGroups();
				if (setWithLock != null) {
					WriteLock writeLock = setWithLock.writeLock();
					writeLock.lock();
					try {
						Set<String> groups = setWithLock.getObj();
						if (groups != null && groups.size() > 0) {
							for (String groupid : groups) {
								try {
									unbind(groupid, channelContext, false, callbackListener);
								} catch (Exception e) {
									log.error(e.toString(), e);
								}
							}
							groups.clear();
							channelContext.setGroups(null);
						}
					} catch (Exception e) {
						log.error(e.toString(), e);
					} finally {
						writeLock.unlock();
					}
				}
			} catch (Throwable e) {
				throw e;
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		}
	}

	/**
	 * 与指定组解除绑定
	 * @param groupid
	 * @param channelContext
	 */
	public void unbind(String groupid, ChannelContext channelContext) {
		unbind(groupid, channelContext, true);
	}

	/**
	 * 与指定组解除绑定
	 * @param groupid
	 * @param channelContext
	 * @param deleteFromChannelContext
	 */
	public void unbind(String groupid, ChannelContext channelContext, boolean deleteFromChannelContext) {
		unbind(groupid, channelContext, deleteFromChannelContext, true);
	}

	/**
	 * 
	 * @param groupid
	 * @param channelContext
	 * @param deleteFromChannelContext
	 * @param callbackListener 是否回调GroupListener
	 */
	public void unbind(String groupid, ChannelContext channelContext, boolean deleteFromChannelContext, boolean callbackListener) {
		try {
			GroupContext groupContext = channelContext.groupContext;
			if (groupContext.isShortConnection) {
				return;
			}

			if (StrUtil.isBlank(groupid)) {
				return;
			}

			SetWithLock<ChannelContext> setWithLock = groupmap.getObj().get(groupid);
			if (setWithLock != null) {
				boolean ss = setWithLock.remove(channelContext);
				if (!ss) {
					log.error("{}, 移除失败, cid:{}", channelContext, channelContext.getId());

				}

				if (deleteFromChannelContext) {
					SetWithLock<String> setInChannelContext = channelContext.getGroups();
					if (setInChannelContext != null) {
						setInChannelContext.remove(groupid);
					}
				}

				if (callbackListener) {
					GroupListener groupListener = groupContext.getGroupListener();
					if (groupListener != null) {
						try {
							groupListener.onAfterUnbind(channelContext, groupid);
						} catch (Throwable e) {
							log.error(e.toString(), e);
						}
					}
				}

				//如果该群组没有任何连接，就把这个群组从map中删除，以释放空间
				if (setWithLock.getObj().size() == 0) {
					Lock lock2 = groupmap.writeLock();
					lock2.lock();
					try {
						if (setWithLock.getObj().size() == 0) {
							groupmap.getObj().remove(groupid);
						}
					} catch (Throwable e) {
						log.error(e.toString(), e);
					} finally {
						lock2.unlock();
					}
				}
			}
		} catch (Exception e) {
			log.error(e.toString(), e);
		}
	}

	public Comparator<ChannelContext> getChannelContextComparator() {
		return channelContextComparator;
	}

	public void setChannelContextComparator(Comparator<ChannelContext> channelContextComparator) {
		this.channelContextComparator = channelContextComparator;
	}
}
