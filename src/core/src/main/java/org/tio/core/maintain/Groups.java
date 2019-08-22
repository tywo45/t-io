package org.tio.core.maintain;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.intf.GroupListener;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.LockUtils;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.ReadWriteLockHandler;
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
	private Comparator<ChannelContext>							channelContextComparator	= null;
	/** The log. */
	private static Logger										log							= LoggerFactory.getLogger(Groups.class);
	/** 一个组有哪些客户端<br>
	 * key: groupid<br>
	 * value: SetWithLock<ChannelContext><br>
	 */
	private MapWithLock<String, SetWithLock<ChannelContext>>	groupmap					= new MapWithLock<>(new HashMap<String, SetWithLock<ChannelContext>>());
	private String												rwKey						= "_tio_groups_bind__";

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
		if (channelContext.tioConfig.isShortConnection) {
			return;
		}

		if (StrUtil.isBlank(groupid)) {
			return;
		}
		
		SetWithLock<ChannelContext> channelSet = groupmap.get(groupid);
		if (channelSet == null) {
			try {
				LockUtils.runReadOrWrite(rwKey + groupid, this, new ReadWriteLockHandler() {
					@Override
					public Object read() {
						return null;
					}

					@Override
					public Object write() {
						SetWithLock<ChannelContext> channelSet = new SetWithLock<>(MaintainUtils.createSet(channelContextComparator));
						channelSet.add(channelContext);
						groupmap.put(groupid, channelSet);
						return null;
					}
				});
			} catch (Exception e) {
				log.error(e.toString(), e);
			}
		} else {
			channelSet.add(channelContext);
		}

		channelContext.getGroups().add(groupid);

		if (callbackListener) {
			GroupListener groupListener = channelContext.tioConfig.getGroupListener();
			if (groupListener != null) {
				try {
					groupListener.onAfterBind(channelContext, groupid);
				} catch (Throwable e) {
					log.error(e.toString(), e);
				}
			}
		}
	}

	/**
	 * 一个组有哪些客户端
	 * @param groupid
	 * @return
	 * @author tanyaowu
	 */
	public SetWithLock<ChannelContext> clients(TioConfig tioConfig, String groupid) {
		if (tioConfig.isShortConnection) {
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
		TioConfig tioConfig = channelContext.tioConfig;
		if (tioConfig.isShortConnection) {
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
		if (channelContext.tioConfig.isShortConnection) {
			return;
		}

		try {
			SetWithLock<String> setWithLock = channelContext.getGroups();
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
					channelContext.getGroups().clear();
				}
			} catch (Exception e) {
				log.error(e.toString(), e);
			} finally {
				writeLock.unlock();
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
		if (channelContext.tioConfig.isShortConnection) {
			return;
		}

		if (StrUtil.isBlank(groupid)) {
			return;
		}

		try {
			SetWithLock<ChannelContext> channelSet = groupmap.get(groupid);
			if (channelSet != null) {
				boolean ss = channelSet.remove(channelContext);
				if (!ss) {
					log.warn("{}, 移除失败,group:{} cid:{}", channelContext, groupid, channelContext.getId());
				}

				if (deleteFromChannelContext) {
					channelContext.getGroups().remove(groupid);
				}

				if (callbackListener) {
					GroupListener groupListener = channelContext.tioConfig.getGroupListener();
					if (groupListener != null) {
						try {
							groupListener.onAfterUnbind(channelContext, groupid);
						} catch (Throwable e) {
							log.error(e.toString(), e);
						}
					}
				}

				//如果该群组没有任何连接，就把这个群组从map中删除，以释放空间
				if (channelSet.getObj().size() == 0) {
					groupmap.remove(groupid);
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
