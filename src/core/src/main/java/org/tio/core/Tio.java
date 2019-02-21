package org.tio.core;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.TioClusterVo;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.server.ServerGroupContext;
import org.tio.utils.convert.Converter;
import org.tio.utils.lock.MapWithLock;
import org.tio.utils.lock.ReadLockHandler;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.page.Page;
import org.tio.utils.page.PageUtils;

/**
 * The Class Tio. t-io用户关心的API几乎全在这
 *
 * @author tanyaowu
 */
public class Tio {
	public static class IpBlacklist {
		/**
		 * 把ip添加到黑名单
		 * @param groupContext
		 * @param ip
		 * @author tanyaowu
		 */
		public static boolean add(GroupContext groupContext, String ip) {
			return groupContext.ipBlacklist.add(ip);
		}

		/**
		 * 清空黑名单
		 * @param groupContext
		 * @author tanyaowu
		 */
		public static void clear(GroupContext groupContext) {
			groupContext.ipBlacklist.clear();
		}

		/**
		 * 获取ip黑名单列表
		 * @param groupContext
		 * @return
		 * @author tanyaowu
		 */
		public static Collection<String> getAll(GroupContext groupContext) {
			return groupContext.ipBlacklist.getAll();
		}

		/**
		 * 是否在黑名单中
		 * @param groupContext
		 * @param ip
		 * @return
		 * @author tanyaowu
		 */
		public static boolean isInBlacklist(GroupContext groupContext, String ip) {
			return groupContext.ipBlacklist.isInBlacklist(ip);
		}

		/**
		 * 把ip从黑名单中删除
		 * @param groupContext
		 * @param ip
		 * @author tanyaowu
		 */
		public static void remove(GroupContext groupContext, String ip) {
			groupContext.ipBlacklist.remove(ip);
		}
	}

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(Tio.class);

	/**
	 * 绑定业务id
	 * @param channelContext
	 * @param bsId
	 * @author tanyaowu
	 */
	public static void bindBsId(ChannelContext channelContext, String bsId) {
		channelContext.groupContext.bsIds.bind(channelContext, bsId);
	}

	/**
	 * 绑定群组
	 * @param channelContext
	 * @param group
	 * @author tanyaowu
	 */
	public static void bindGroup(ChannelContext channelContext, String group) {
		channelContext.groupContext.groups.bind(group, channelContext);
	}

	/**
	 * 绑定token
	 * @param channelContext
	 * @param token
	 * @author tanyaowu
	 */
	public static void bindToken(ChannelContext channelContext, String token) {
		channelContext.groupContext.tokens.bind(token, channelContext);
	}

	/**
	 * 绑定用户
	 * @param channelContext
	 * @param userid
	 * @author tanyaowu
	 */
	public static void bindUser(ChannelContext channelContext, String userid) {
		channelContext.groupContext.users.bind(userid, channelContext);
	}

	/**
	 * 阻塞发送消息到指定ChannelContext
	 * @param channelContext
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSend(ChannelContext channelContext, Packet packet) {
		if (channelContext == null) {
			return false;
		}
		CountDownLatch countDownLatch = new CountDownLatch(1);
		return send(channelContext, packet, countDownLatch, PacketSendMode.SINGLE_BLOCK);
	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSend(GroupContext groupContext, String ip, int port, Packet packet) {
		return send(groupContext, ip, port, packet, true);
	}

	/**
	 * 发消息到所有连接
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToAll(groupContext, packet, channelContextFilter, true);
	}

	/**
	 * 阻塞发消息给指定业务ID
	 * @param groupContext
	 * @param bsId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToBsId(GroupContext groupContext, String bsId, Packet packet) {
		return sendToBsId(groupContext, bsId, packet, true);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToGroup(GroupContext groupContext, String group, Packet packet) {
		return bSendToGroup(groupContext, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToGroup(groupContext, group, packet, channelContextFilter, true);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelContextId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToId(GroupContext groupContext, String channelContextId, Packet packet) {
		return sendToId(groupContext, channelContextId, packet, true);
	}

	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static Boolean bSendToIp(GroupContext groupContext, String ip, Packet packet) {
		return bSendToIp(groupContext, ip, packet, null);
	}

	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @return
	 * @author: tanyaowu
	 */
	public static Boolean bSendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToIp(groupContext, ip, packet, channelContextFilter, true);
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToSet(groupContext, setWithLock, packet, channelContextFilter, true);
	}

	/**
	 * 阻塞发消息到指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToToken(GroupContext groupContext, String token, Packet packet) {
		return sendToToken(groupContext, token, packet, true);
	}

	/**
	 * 阻塞发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToUser(GroupContext groupContext, String userid, Packet packet) {
		return sendToUser(groupContext, userid, packet, true);
	}

	/**
	 * 关闭连接
	 * @param channelContext
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(ChannelContext channelContext, String remark) {
		close(channelContext, null, remark);
	}

	/**
	 * 关闭连接
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(ChannelContext channelContext, Throwable throwable, String remark) {
		close(channelContext, throwable, remark, false);
	}

	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
		close(channelContext, throwable, remark, isNeedRemove, true);
	}

	/**
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isNeedRemove
	 * @param needCloseLock
	 */
	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove, boolean needCloseLock) {
		if (channelContext == null) {
			return;
		}
		if (channelContext.isWaitingClose) {
			log.debug("{} 正在等待被关闭", channelContext);
			return;
		}

		WriteLock writeLock = null;
		if (needCloseLock) {
			writeLock = channelContext.closeLock.writeLock();

			boolean tryLock = writeLock.tryLock();
			if (!tryLock) {
				return;
			}
			channelContext.isWaitingClose = true;
			writeLock.unlock();
		} else {
			channelContext.isWaitingClose = true;
		}

		if (channelContext.asynchronousSocketChannel != null) {
			try {
				channelContext.asynchronousSocketChannel.shutdownInput();
			} catch (Throwable e) {
				//log.error(e.toString(), e);
			}
			try {
				channelContext.asynchronousSocketChannel.shutdownOutput();
			} catch (Throwable e) {
				//log.error(e.toString(), e);
			}
			try {
				channelContext.asynchronousSocketChannel.close();
			} catch (Throwable e) {
				//log.error(e.toString(), e);
			}
		}

		channelContext.closeMeta.setRemark(remark);
		channelContext.closeMeta.setThrowable(throwable);
		if (!isNeedRemove) {
			if (channelContext.isServer()) {
				isNeedRemove = true;
			} else {
				ClientChannelContext clientChannelContext = (ClientChannelContext) channelContext;
				if (!ReconnConf.isNeedReconn(clientChannelContext, false)) { //不需要重连
					isNeedRemove = true;
				}
			}
		}
		channelContext.closeMeta.setNeedRemove(isNeedRemove);

		channelContext.groupContext.closeRunnable.addMsg(channelContext);
		channelContext.groupContext.closeRunnable.execute();
	}

	/**
	 * 关闭连接
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		ChannelContext channelContext = groupContext.clientNodes.find(clientIp, clientPort);
		close(channelContext, throwable, remark);
	}

	/**
	 * 关闭某群所有连接
	 * @param groupContext
	 * @param group
	 * @param remark
	 * @return
	 */
	public static void closeGroup(GroupContext groupContext, String group, String remark) {
		SetWithLock<ChannelContext> setWithLock = Tio.getChannelContextsByGroup(groupContext, group);
		setWithLock.handle(new ReadLockHandler<Set<ChannelContext>>() {
			@Override
			public void handler(Set<ChannelContext> set) {
				for (ChannelContext channelContext : set) {
					Tio.close(channelContext, remark);
				}
			}
		});
	}

	/**
	 * 获取所有连接，包括当前处于断开状态的
	 * @param groupContext
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getAllChannelContexts(GroupContext groupContext) {
		return groupContext.connections;
	}

	/**
	 * 获取所有处于正常连接状态的连接
	 * @param clientGroupContext
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getAllConnectedsChannelContexts(ClientGroupContext clientGroupContext) {
		return clientGroupContext.connecteds;
	}

	/**
	 * 根据业务id找ChannelContext
	 * @param groupContext
	 * @param bsId
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getChannelContextByBsId(GroupContext groupContext, String bsId) {
		return groupContext.bsIds.find(groupContext, bsId);
	}

	/**
	 * 根据clientip和clientport获取ChannelContext
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getChannelContextByClientNode(GroupContext groupContext, String clientIp, Integer clientPort) {
		return groupContext.clientNodes.find(clientIp, clientPort);
	}

	/**
	 * 根据id获取ChannelContext
	 * @param channelContextId
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getChannelContextById(GroupContext groupContext, String channelContextId) {
		return groupContext.ids.find(groupContext, channelContextId);
	}

	/**
	 * 获取一个组的所有客户端
	 * @param groupContext
	 * @param group
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByGroup(GroupContext groupContext, String group) {
		return groupContext.groups.clients(groupContext, group);
	}

	/**
	 * 根据token获取SetWithLock<ChannelContext>
	 * @param groupContext
	 * @param token
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByToken(GroupContext groupContext, String token) {
		return groupContext.tokens.find(groupContext, token);
	}

	/**
	 * 根据userid获取SetWithLock<ChannelContext>
	 * @param groupContext
	 * @param userid
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByUserid(GroupContext groupContext, String userid) {
		return groupContext.users.find(groupContext, userid);
	}

	/**
	 *
	 * @param groupContext
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize) {
		return getPageOfAll(groupContext, pageIndex, pageSize, null);
	}

	/**
	 * 
	 * @param groupContext
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 */
	public static <T> Page<T> getPageOfAll(GroupContext groupContext, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getAllChannelContexts(groupContext);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 * 这个方法是给客户器端用的
	 * @param clientGroupContext
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfConnecteds(ClientGroupContext clientGroupContext, Integer pageIndex, Integer pageSize) {
		return getPageOfConnecteds(clientGroupContext, pageIndex, pageSize, null);
	}

	/**
	 * 这个方法是给客户器端用的
	 * @param clientGroupContext
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 * @author tanyaowu
	 */
	public static <T> Page<T> getPageOfConnecteds(ClientGroupContext clientGroupContext, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getAllConnectedsChannelContexts(clientGroupContext);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 *
	 * @param groupContext
	 * @param group
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize) {
		return getPageOfGroup(groupContext, group, pageIndex, pageSize, null);
	}

	/**
	 * 
	 * @param groupContext
	 * @param group
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 */
	public static <T> Page<T> getPageOfGroup(GroupContext groupContext, String group, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getChannelContextsByGroup(groupContext, group);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 * 群组有多少个连接
	 * @param groupContext
	 * @param group
	 * @return
	 */
	public static int groupCount(GroupContext groupContext, String group) {
		SetWithLock<ChannelContext> setWithLock = groupContext.groups.clients(groupContext, group);
		if (setWithLock == null) {
			return 0;
		}

		return setWithLock.getObj().size();
	}

	/**
	 * 某通道是否在某群组中
	 * @param group
	 * @param channelContext
	 * @return true：在该群组
	 * @author: tanyaowu
	 */
	public static boolean isInGroup(String group, ChannelContext channelContext) {
		SetWithLock<String> set = channelContext.getGroups();
		if (set == null) {
			return false;
		}
		return set.getObj().contains(group);
	}

	/**
	 * 
	 * @param groupContext
	 * @param bsId
	 * @param packet
	 * @author tanyaowu
	 */
	public static void notifyClusterForBsId(GroupContext groupContext, String bsId, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setBsId(bsId);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把群组消息通知到集群中的其它机器
	 * @param groupContext
	 * @param group
	 * @param packet
	 */
	public static void notifyClusterForGroup(GroupContext groupContext, String group, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setGroup(group);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把channelContextId消息通知到集群中的其它机器
	 * @param groupContext
	 * @param channelContextId
	 * @param packet
	 */
	public static void notifyClusterForId(GroupContext groupContext, String channelContextId, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setChannelId(channelContextId);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把IP消息通知到集群中的其它机器
	 * @param groupContext
	 * @param ip
	 * @param packet
	 */
	public static void notifyClusterForIp(GroupContext groupContext, String ip, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setIp(ip);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把token消息通知到集群中的其它机器
	 * @param groupContext
	 * @param token
	 * @param packet
	 */
	public static void notifyClusterForToken(GroupContext groupContext, String token, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setToken(token);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把userid消息通知到集群中的其它机器
	 * @param groupContext
	 * @param userid
	 * @param packet
	 */
	public static void notifyClusterForUser(GroupContext groupContext, String userid, Packet packet) {
		TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setUserid(userid);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(ChannelContext channelContext, String remark) {
		remove(channelContext, null, remark);
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(ChannelContext channelContext, Throwable throwable, String remark) {
		close(channelContext, throwable, remark, true);
	}

	/**
	 * 和close方法一样，只不过不再进行重连等维护性的操作
	 * @param groupContext
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void remove(GroupContext groupContext, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		ChannelContext channelContext = groupContext.clientNodes.find(clientIp, clientPort);
		remove(channelContext, throwable, remark);
	}

	/**
	 * 删除clientip为指定值的所有连接
	 * @param groupContext
	 * @param ip
	 * @param remark
	 * @author: tanyaowu
	 */
	public static void remove(ServerGroupContext serverGroupContext, String ip, String remark) {
		SetWithLock<ChannelContext> setWithLock = serverGroupContext.ips.clients(serverGroupContext, ip);
		if (setWithLock == null) {
			return;
		}

		setWithLock.handle(new ReadLockHandler<Set<ChannelContext>>() {
			@Override
			public void handler(Set<ChannelContext> set) {
				for (ChannelContext channelContext : set) {
					Tio.remove(channelContext, remark);
				}
			}
		});
	}

	/**
	 * 发送消息到指定ChannelContext
	 * @param channelContext
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean send(ChannelContext channelContext, Packet packet) {
		return send(channelContext, packet, null, null);
	}

	/**
	 *
	 * @param channelContext
	 * @param packet
	 * @param countDownLatch
	 * @param packetSendMode
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean send(final ChannelContext channelContext, final Packet packet, CountDownLatch countDownLatch, PacketSendMode packetSendMode) {
		try {
			if (packet == null || channelContext == null) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				return false;
			}

			if (channelContext.isVirtual) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				return true;
			}

			if (channelContext.isClosed || channelContext.isRemoved) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				if (channelContext != null) {
					log.info("can't send data, {}, isClosed:{}, isRemoved:{}", channelContext, channelContext.isClosed, channelContext.isRemoved);
				}
				return false;
			}

			boolean isSingleBlock = countDownLatch != null && packetSendMode == PacketSendMode.SINGLE_BLOCK;

			boolean isAdded = false;
			if (countDownLatch != null) {
				Meta meta = new Meta();
				meta.setCountDownLatch(countDownLatch);
				packet.setMeta(meta);
			}

			if (channelContext.groupContext.useQueueSend) {
				isAdded = channelContext.sendRunnable.addMsg(packet);
			} else {
				isAdded = channelContext.sendRunnable.sendPacket(packet);
			}

			if (!isAdded) {
				if (countDownLatch != null) {
					countDownLatch.countDown();
				}
				return false;
			}
			if (channelContext.groupContext.useQueueSend) {
				channelContext.sendRunnable.execute();
			}

			if (isSingleBlock) {
				long timeout = 10;
				try {
					Boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
					if (!awaitFlag) {
						log.error("{}, 阻塞发送超时, timeout:{}s, packet:{}", channelContext, timeout, packet.logstr());
					}
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}

				Boolean isSentSuccess = packet.getMeta().getIsSentSuccess();
				return isSentSuccess;
			} else {
				return true;
			}
		} catch (Throwable e) {
			log.error(channelContext + ", " + e.toString(), e);
			return false;
		}

	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean send(GroupContext groupContext, String ip, int port, Packet packet) {
		return send(groupContext, ip, port, packet, false);
	}

	/**
	 * 发送到指定的ip和port
	 * @param groupContext
	 * @param ip
	 * @param port
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean send(GroupContext groupContext, String ip, int port, Packet packet, boolean isBlock) {
		ChannelContext channelContext = groupContext.clientNodes.find(ip, port);
		if (channelContext != null) {
			if (isBlock) {
				return bSend(channelContext, packet);
			} else {
				return send(channelContext, packet);
			}
		} else {
			log.info("{}, can find channelContext by {}:{}", groupContext.getName(), ip, port);
			return false;
		}
	}

	public static void sendToAll(GroupContext groupContext, Packet packet) {
		sendToAll(groupContext, packet, null);
	}

	/**
	 * 发消息到所有连接
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToAll(groupContext, packet, channelContextFilter, false);
	}

	/**
	 *
	 * @param groupContext
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToAll(GroupContext groupContext, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = groupContext.connections;
			if (setWithLock == null) {
				log.debug("{}, 没有任何连接", groupContext.getName());
				return false;
			}
			Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4all()) {
					TioClusterVo tioClusterVo = new TioClusterVo(packet);
					tioClusterVo.setToAll(true);
					tioClusterConfig.publish(tioClusterVo);
				}
			}
		}
	}

	/**
	 * 发消息给指定业务ID
	 * @param groupContext
	 * @param bsId
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean sendToBsId(GroupContext groupContext, String bsId, Packet packet) {
		return sendToBsId(groupContext, bsId, packet, false);
	}

	/**
	 * 发消息给指定业务ID
	 * @param groupContext
	 * @param bsId
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean sendToBsId(GroupContext groupContext, String bsId, Packet packet, boolean isBlock) {
		ChannelContext channelContext = Tio.getChannelContextByBsId(groupContext, bsId);
		if (channelContext == null) {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4bsId()) {
					notifyClusterForBsId(groupContext, bsId, packet);
				}
			}
			return false;
		}
		if (isBlock) {
			return bSend(channelContext, packet);
		} else {
			return send(channelContext, packet);
		}
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static void sendToGroup(GroupContext groupContext, String group, Packet packet) {
		sendToGroup(groupContext, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToGroup(groupContext, group, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到组
	 * @param groupContext
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	private static Boolean sendToGroup(GroupContext groupContext, String group, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = groupContext.groups.clients(groupContext, group);
			if (setWithLock == null) {
				log.debug("{}, 组[{}]不存在", groupContext.getName(), group);
				return false;
			}
			Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4group()) {
					notifyClusterForGroup(groupContext, group, packet);
				}
			}
		}
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelContextId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean sendToId(GroupContext groupContext, String channelContextId, Packet packet) {
		return sendToId(groupContext, channelContextId, packet, false);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelContextId
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean sendToId(GroupContext groupContext, String channelContextId, Packet packet, boolean isBlock) {
		ChannelContext channelContext = Tio.getChannelContextById(groupContext, channelContextId);
		if (channelContext == null) {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4channelId()) {
					notifyClusterForId(groupContext, channelContextId, packet);
				}
			}
			return false;
		}
		if (isBlock) {
			return bSend(channelContext, packet);
		} else {
			return send(channelContext, packet);
		}
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static void sendToIp(GroupContext groupContext, String ip, Packet packet) {
		sendToIp(groupContext, ip, packet, null);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @author: tanyaowu
	 */
	public static void sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToIp(groupContext, ip, packet, channelContextFilter, false);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param groupContext
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @return
	 * @author: tanyaowu
	 */
	private static Boolean sendToIp(GroupContext groupContext, String ip, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = groupContext.ips.clients(groupContext, ip);
			if (setWithLock == null) {
				log.info("{}, 没有ip为[{}]的对端", groupContext.getName(), ip);
				return false;
			}
			Boolean ret = sendToSet(groupContext, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4ip()) {
					notifyClusterForIp(groupContext, ip, packet);
				}
			}
		}
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToSet(groupContext, setWithLock, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到指定集合
	 * @param groupContext
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToSet(GroupContext groupContext, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter,
	        boolean isBlock) {
		boolean releasedLock = false;
		Lock lock = setWithLock.readLock();
		lock.lock();
		try {
			Set<ChannelContext> set = setWithLock.getObj();
			if (set.size() == 0) {
				log.debug("{}, 集合为空", groupContext.getName());
				return false;
			}

			CountDownLatch countDownLatch = null;
			if (isBlock) {
				countDownLatch = new CountDownLatch(set.size());
			}
			int sendCount = 0;
			for (ChannelContext channelContext : set) {
				if (channelContextFilter != null) {
					boolean isfilter = channelContextFilter.filter(channelContext);
					if (!isfilter) {
						if (isBlock) {
							countDownLatch.countDown();
						}
						continue;
					}
				}

				sendCount++;
				if (isBlock) {
					send(channelContext, packet, countDownLatch, PacketSendMode.GROUP_BLOCK);
				} else {
					send(channelContext, packet, null, null);
				}
			}
			lock.unlock();
			releasedLock = true;

			if (sendCount == 0) {
				return false;
			}

			if (isBlock) {
				try {
					long timeout = sendCount / 5;
					timeout = timeout < 10 ? 10 : timeout;
					boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
					if (!awaitFlag) {
						log.error("{}, 同步群发超时, size:{}, timeout:{}, packet:{}", groupContext.getName(), setWithLock.getObj().size(), timeout, packet.logstr());
						return false;
					} else {
						return true;
					}
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
					return false;
				} finally {

				}
			} else {
				return true;
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
			return false;
		} finally {
			if (!releasedLock) {
				lock.unlock();
			}
		}
	}

	/**
	 * 发消息到指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean sendToToken(GroupContext groupContext, String token, Packet packet) {
		return sendToToken(groupContext, token, packet, false);
	}

	/**
	 * 发消息给指定token
	 * @param groupContext
	 * @param token
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToToken(GroupContext groupContext, String token, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = groupContext.tokens.find(groupContext, token);
		try {
			if (setWithLock == null) {
				return false;
			}

			ReadLock readLock = setWithLock.readLock();
			readLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				boolean ret = false;
				for (ChannelContext channelContext : set) {
					boolean singleRet = false;
					// 不要用 a = a || b()，容易漏执行后面的函数
					if (isBlock) {
						singleRet = bSend(channelContext, packet);
					} else {
						singleRet = send(channelContext, packet);
					}
					if (singleRet) {
						ret = true;
					}
				}
				return ret;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				readLock.unlock();
			}
			return false;
		} finally {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4user()) {
					notifyClusterForToken(groupContext, token, packet);
				}
			}
		}
	}

	/**
	 * 发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet) {
		return sendToUser(groupContext, userid, packet, false);
	}

	/**
	 * 发消息给指定用户
	 * @param groupContext
	 * @param userid
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToUser(GroupContext groupContext, String userid, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = groupContext.users.find(groupContext, userid);
		try {
			if (setWithLock == null) {
				return false;
			}

			ReadLock readLock = setWithLock.readLock();
			readLock.lock();
			try {
				Set<ChannelContext> set = setWithLock.getObj();
				boolean ret = false;
				for (ChannelContext channelContext : set) {
					boolean singleRet = false;
					// 不要用 a = a || b()，容易漏执行后面的函数
					if (isBlock) {
						singleRet = bSend(channelContext, packet);
					} else {
						singleRet = send(channelContext, packet);
					}
					if (singleRet) {
						ret = true;
					}
				}
				return ret;
			} catch (Throwable e) {
				log.error(e.getMessage(), e);
			} finally {
				readLock.unlock();
			}
			return false;
		} finally {
			if (groupContext.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = groupContext.getTioClusterConfig();

				if (tioClusterConfig.isCluster4user()) {
					notifyClusterForUser(groupContext, userid, packet);
				}
			}
		}
	}

	/**
	 * 发送并等待响应.<br>
	 * 注意：<br>
	 * 1、参数packet的synSeq不为空且大于0（null、等于小于0都不行）<br>
	 * 2、对端收到此消息后，需要回一条synSeq一样的消息。业务需要在decode()方法中为packet的synSeq赋值<br>
	 * 3、对于同步发送，框架层面并不会帮应用去调用handler.handler(packet, channelContext)方法，应用需要自己去处理响应的消息包，
	 *参考：groupContext.getAioHandler().handler(packet, channelContext);<br>
	 *
	 * @param channelContext
	 * @param packet 业务层必须设置好synSeq字段的值，而且要保证唯一（不能重复）。可以在groupContext范围内用AtomicInteger
	 * @param timeout
	 * @return
	 * @author tanyaowu
	 */
	@SuppressWarnings("finally")
	public static Packet synSend(ChannelContext channelContext, Packet packet, long timeout) {
		Integer synSeq = packet.getSynSeq();
		if (synSeq == null || synSeq <= 0) {
			throw new RuntimeException("synSeq必须大于0");
		}

		MapWithLock<Integer, Packet> waitingResps = channelContext.groupContext.getWaitingResps();
		try {
			waitingResps.put(synSeq, packet);

			synchronized (packet) {
				send(channelContext, packet);
				try {
					packet.wait(timeout);
				} catch (InterruptedException e) {
					log.error(e.toString(), e);
				}
			}
		} catch (Throwable e) {
			log.error(e.toString(), e);
		} finally {
			Packet respPacket = waitingResps.remove(synSeq);
			if (respPacket == null) {
				log.error("respPacket == null,{}", channelContext);
				return null;
			}
			if (respPacket == packet) {
				log.error("{}, 同步发送超时, {}", channelContext.groupContext.getName(), channelContext);
				return null;
			}
			return respPacket;
		}
	}

	/**
	 * 解绑业务id
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindBsId(ChannelContext channelContext) {
		channelContext.groupContext.bsIds.unbind(channelContext);
	}

	/**
	 * 与所有组解除解绑关系
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(ChannelContext channelContext) {
		channelContext.groupContext.groups.unbind(channelContext);
	}

	/**
	 * 与指定组解除绑定关系
	 * @param group
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(String group, ChannelContext channelContext) {
		channelContext.groupContext.groups.unbind(group, channelContext);
	}

	/**
	 * 解除channelContext绑定的token
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindToken(ChannelContext channelContext) {
		channelContext.groupContext.tokens.unbind(channelContext);
	}

	//	org.tio.core.GroupContext.ipBlacklist

	/**
	 * 解除channelContext绑定的userid
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindUser(ChannelContext channelContext) {
		channelContext.groupContext.users.unbind(channelContext);
	}

	/**
	 * 解除userid的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param groupContext
	 * @param userid
	 * @author: tanyaowu
	 */
	public static void unbindUser(GroupContext groupContext, String userid) {
		groupContext.users.unbind(groupContext, userid);
	}

	private Tio() {
	}

}
