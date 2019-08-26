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
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.cluster.TioClusterConfig;
import org.tio.cluster.TioClusterVo;
import org.tio.core.ChannelContext.CloseCode;
import org.tio.core.intf.Packet;
import org.tio.core.intf.Packet.Meta;
import org.tio.server.ServerTioConfig;
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
		 * 把ip添加到黑名单，此黑名单只针对tioConfig有效，其它tioConfig不会把这个ip视为黑名单
		 * @param tioConfig
		 * @param ip
		 * @author tanyaowu
		 */
		public static boolean add(TioConfig tioConfig, String ip) {
			return tioConfig.ipBlacklist.add(ip);
		}

		/**
		 * 添加全局ip黑名单
		 * @param ip
		 * @return
		 * @author tanyaowu
		 */
		public static boolean add(String ip) {
			return org.tio.core.maintain.IpBlacklist.GLOBAL.add(ip);
		}

		/**
		 * 清空黑名单，只针对tioConfig有效
		 * @param tioConfig
		 * @author tanyaowu
		 */
		public static void clear(TioConfig tioConfig) {
			tioConfig.ipBlacklist.clear();
		}

		/**
		 * 清空全局黑名单
		 * @author tanyaowu
		 */
		public static void clear() {
			org.tio.core.maintain.IpBlacklist.GLOBAL.clear();
		}

		/**
		 * 获取ip黑名单列表
		 * @param tioConfig
		 * @return
		 * @author tanyaowu
		 */
		public static Collection<String> getAll(TioConfig tioConfig) {
			return tioConfig.ipBlacklist.getAll();
		}

		/**
		 * 获取全局黑名单
		 * @return
		 * @author tanyaowu
		 */
		public static Collection<String> getAll() {
			return org.tio.core.maintain.IpBlacklist.GLOBAL.getAll();
		}

		/**
		 * 是否在黑名单中
		 * @param tioConfig
		 * @param ip
		 * @return
		 * @author tanyaowu
		 */
		public static boolean isInBlacklist(TioConfig tioConfig, String ip) {
			return tioConfig.ipBlacklist.isInBlacklist(ip) || org.tio.core.maintain.IpBlacklist.GLOBAL.isInBlacklist(ip);
		}

		/**
		 * 把ip从黑名单中删除
		 * @param tioConfig
		 * @param ip
		 * @author tanyaowu
		 */
		public static void remove(TioConfig tioConfig, String ip) {
			tioConfig.ipBlacklist.remove(ip);
		}

		/**
		 * 删除全局黑名单
		 * @param ip
		 * @author tanyaowu
		 */
		public static void remove(String ip) {
			org.tio.core.maintain.IpBlacklist.GLOBAL.remove(ip);
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
		channelContext.tioConfig.bsIds.bind(channelContext, bsId);
	}

	/**
	 * 绑定群组
	 * @param channelContext
	 * @param group
	 * @author tanyaowu
	 */
	public static void bindGroup(ChannelContext channelContext, String group) {
		channelContext.tioConfig.groups.bind(group, channelContext);
	}

	/**
	 * 绑定token
	 * @param channelContext
	 * @param token
	 * @author tanyaowu
	 */
	public static void bindToken(ChannelContext channelContext, String token) {
		channelContext.tioConfig.tokens.bind(token, channelContext);
	}

	/**
	 * 绑定用户
	 * @param channelContext
	 * @param userid
	 * @author tanyaowu
	 */
	public static void bindUser(ChannelContext channelContext, String userid) {
		channelContext.tioConfig.users.bind(userid, channelContext);
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
	 * @param tioConfig
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSend(TioConfig tioConfig, String ip, int port, Packet packet) {
		return send(tioConfig, ip, port, packet, true);
	}

	/**
	 * 发消息到所有连接
	 * @param tioConfig
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToAll(TioConfig tioConfig, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToAll(tioConfig, packet, channelContextFilter, true);
	}

	/**
	 * 阻塞发消息给指定业务ID
	 * @param tioConfig
	 * @param bsId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToBsId(TioConfig tioConfig, String bsId, Packet packet) {
		return sendToBsId(tioConfig, bsId, packet, true);
	}

	/**
	 * 发消息到组
	 * @param tioConfig
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToGroup(TioConfig tioConfig, String group, Packet packet) {
		return bSendToGroup(tioConfig, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param tioConfig
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToGroup(TioConfig tioConfig, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToGroup(tioConfig, group, packet, channelContextFilter, true);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelContextId
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean bSendToId(TioConfig tioConfig, String channelContextId, Packet packet) {
		return sendToId(tioConfig, channelContextId, packet, true);
	}

	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static Boolean bSendToIp(TioConfig tioConfig, String ip, Packet packet) {
		return bSendToIp(tioConfig, ip, packet, null);
	}

	/**
	 * 阻塞发送到指定ip对应的集合
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @return
	 * @author: tanyaowu
	 */
	public static Boolean bSendToIp(TioConfig tioConfig, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToIp(tioConfig, ip, packet, channelContextFilter, true);
	}

	/**
	 * 发消息到指定集合
	 * @param tioConfig
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static Boolean bSendToSet(TioConfig tioConfig, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		return sendToSet(tioConfig, setWithLock, packet, channelContextFilter, true);
	}

	/**
	 * 阻塞发消息到指定token
	 * @param tioConfig
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToToken(TioConfig tioConfig, String token, Packet packet) {
		return sendToToken(tioConfig, token, packet, true);
	}

	/**
	 * 阻塞发消息给指定用户
	 * @param tioConfig
	 * @param userid
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean bSendToUser(TioConfig tioConfig, String userid, Packet packet) {
		return sendToUser(tioConfig, userid, packet, true);
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
	 * 
	 * @param channelContext
	 * @param remark
	 * @param closeCode
	 */
	public static void close(ChannelContext channelContext, String remark, CloseCode closeCode) {
		close(channelContext, null, remark, closeCode);
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

	public static void close(ChannelContext channelContext, Throwable throwable, String remark, CloseCode closeCode) {
		close(channelContext, throwable, remark, false, closeCode);
	}

	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove) {
		close(channelContext, throwable, remark, isNeedRemove, true);
	}

	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove, CloseCode closeCode) {
		close(channelContext, throwable, remark, isNeedRemove, true, closeCode);
	}

	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove, boolean needCloseLock) {
		close(channelContext, throwable, remark, isNeedRemove, needCloseLock, null);
	}

	/**
	 * 
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param isNeedRemove
	 * @param needCloseLock
	 */
	public static void close(ChannelContext channelContext, Throwable throwable, String remark, boolean isNeedRemove, boolean needCloseLock, CloseCode closeCode) {
		if (channelContext == null) {
			return;
		}
		if (channelContext.isWaitingClose) {
			log.debug("{} 正在等待被关闭", channelContext);
			return;
		}
		
		//先立即取消各项任务，这样可防止有新的任务被提交进来
		channelContext.decodeRunnable.setCanceled(true);
		channelContext.handlerRunnable.setCanceled(true);
		channelContext.sendRunnable.setCanceled(true);

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

		if (closeCode == null) {
			if (channelContext.getCloseCode() == CloseCode.INIT_STATUS) {
				channelContext.setCloseCode(CloseCode.NO_CODE);
			}
		} else {
			channelContext.setCloseCode(closeCode);
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

		channelContext.tioConfig.closeRunnable.addMsg(channelContext);
		channelContext.tioConfig.closeRunnable.execute();
	}

	/**
	 * 关闭连接
	 * @param tioConfig
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @author tanyaowu
	 */
	public static void close(TioConfig tioConfig, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		ChannelContext channelContext = tioConfig.clientNodes.find(clientIp, clientPort);
		close(channelContext, throwable, remark);
	}

	/**
	 * 关闭某群所有连接
	 * @param tioConfig
	 * @param group
	 * @param remark
	 * @return
	 */
	public static void closeGroup(TioConfig tioConfig, String group, String remark) {
		closeGroup(tioConfig, group, remark, null);
	}
	
	/**
	 * 关闭某群所有连接
	 * @param tioConfig
	 * @param group
	 * @param remark
	 * @param closeCode
	 */
	public static void closeGroup(TioConfig tioConfig, String group, String remark, CloseCode closeCode) {
		SetWithLock<ChannelContext> setWithLock = Tio.getChannelContextsByGroup(tioConfig, group);
		setWithLock.handle(new ReadLockHandler<Set<ChannelContext>>() {
			@Override
			public void handler(Set<ChannelContext> set) {
				for (ChannelContext channelContext : set) {
					Tio.close(channelContext, remark, closeCode);
				}
			}
		});
	}
	
	/**
	 * 获取所有连接，包括当前处于断开状态的
	 * @param tioConfig
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getAll(TioConfig tioConfig) {
		return tioConfig.connections;
	}
	
	/**
	 * 获取所有连接，包括当前处于断开状态的
	 * @param tioConfig
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getAll(TioConfig tioConfig)
	 */
	public static SetWithLock<ChannelContext> getAllChannelContexts(TioConfig tioConfig) {
		return getAll(tioConfig);
	}

	
	/**
	 * 此API仅供 tio client使用
	 * 获取所有处于正常连接状态的连接
	 * @param clientTioConfig
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getConnecteds(ClientTioConfig clientTioConfig) {
		return clientTioConfig.connecteds;
	}
	
	/**
	 * 此API仅供 tio client使用
	 * 获取所有处于正常连接状态的连接
	 * @param clientTioConfig
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getAllConnecteds(ClientTioConfig clientTioConfig)
	 */
	public static SetWithLock<ChannelContext> getAllConnectedsChannelContexts(ClientTioConfig clientTioConfig) {
		return getConnecteds(clientTioConfig);
	}

	/**
	 * 根据业务id找ChannelContext
	 * @param tioConfig
	 * @param bsId
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getByBsId(TioConfig tioConfig, String bsId) {
		return tioConfig.bsIds.find(tioConfig, bsId);
	}
	
	/**
	 * 根据业务id找ChannelContext
	 * @param tioConfig
	 * @param bsId
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getByBsId(TioConfig tioConfig, String bsId)
	 */
	public static ChannelContext getChannelContextByBsId(TioConfig tioConfig, String bsId) {
		return getByBsId(tioConfig, bsId);
	}

	/**
	 * 根据clientip和clientport获取ChannelContext
	 * @param tioConfig
	 * @param clientIp
	 * @param clientPort
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getByClientNode(TioConfig tioConfig, String clientIp, Integer clientPort) {
		return tioConfig.clientNodes.find(clientIp, clientPort);
	}
	/**
	 * 根据clientip和clientport获取ChannelContext
	 * @param tioConfig
	 * @param clientIp
	 * @param clientPort
	 * @return
	 * @author tanyaowu
	 * @deprecated getByClientNode(tioConfig, clientIp, clientPort)
	 */
	public static ChannelContext getChannelContextByClientNode(TioConfig tioConfig, String clientIp, Integer clientPort) {
		return  getByClientNode(tioConfig, clientIp, clientPort);
	}
	
	/**
	 * 根据ChannelContext.id获取ChannelContext
	 * @param channelContextId
	 * @return
	 * @author tanyaowu
	 */
	public static ChannelContext getByChannelContextId(TioConfig tioConfig, String channelContextId) {
		return tioConfig.ids.find(tioConfig, channelContextId);
	}

	/**
	 * 根据ChannelContext.id获取ChannelContext
	 * @param channelContextId
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getByChannelContextId(tioConfig, channelContextId)
	 */
	public static ChannelContext getChannelContextById(TioConfig tioConfig, String channelContextId) {
		return getByChannelContextId(tioConfig, channelContextId);
	}
	
	/**
	 * 获取一个组的所有客户端
	 * @param tioConfig
	 * @param group
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getByGroup(TioConfig tioConfig, String group) {
		return tioConfig.groups.clients(tioConfig, group);
	}
	
	/**
	 * 获取一个组的所有客户端
	 * @param tioConfig
	 * @param group
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getByGroup(tioConfig, group)
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByGroup(TioConfig tioConfig, String group) {
		return getByGroup(tioConfig, group);
	}

	/**
	 * 根据token获取SetWithLock<ChannelContext>
	 * @param tioConfig
	 * @param token
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getByToken(TioConfig tioConfig, String token) {
		return tioConfig.tokens.find(tioConfig, token);
	}
	
	/**
	 * 根据token获取SetWithLock<ChannelContext>
	 * @param tioConfig
	 * @param token
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getByToken(tioConfig, token)
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByToken(TioConfig tioConfig, String token) {
		return getByToken(tioConfig, token);
	}

	/**
	 * 根据userid获取SetWithLock<ChannelContext>
	 * @param tioConfig
	 * @param userid
	 * @return
	 * @author tanyaowu
	 */
	public static SetWithLock<ChannelContext> getByUserid(TioConfig tioConfig, String userid) {
		return tioConfig.users.find(tioConfig, userid);
	}
	
	/**
	 * 根据userid获取SetWithLock<ChannelContext>
	 * @param tioConfig
	 * @param userid
	 * @return
	 * @author tanyaowu
	 * @deprecated 用getByUserid(tioConfig, userid)
	 */
	public static SetWithLock<ChannelContext> getChannelContextsByUserid(TioConfig tioConfig, String userid) {
		return getByUserid(tioConfig, userid);
	}

	/**
	 *
	 * @param tioConfig
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfAll(TioConfig tioConfig, Integer pageIndex, Integer pageSize) {
		return getPageOfAll(tioConfig, pageIndex, pageSize, null);
	}

	/**
	 * 
	 * @param tioConfig
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 */
	public static <T> Page<T> getPageOfAll(TioConfig tioConfig, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getAllChannelContexts(tioConfig);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 * 这个方法是给客户器端用的
	 * @param clientTioConfig
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfConnecteds(ClientTioConfig clientTioConfig, Integer pageIndex, Integer pageSize) {
		return getPageOfConnecteds(clientTioConfig, pageIndex, pageSize, null);
	}

	/**
	 * 这个方法是给客户器端用的
	 * @param clientTioConfig
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 * @author tanyaowu
	 */
	public static <T> Page<T> getPageOfConnecteds(ClientTioConfig clientTioConfig, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getAllConnectedsChannelContexts(clientTioConfig);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 *
	 * @param tioConfig
	 * @param group
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 * @author tanyaowu
	 */
	public static Page<ChannelContext> getPageOfGroup(TioConfig tioConfig, String group, Integer pageIndex, Integer pageSize) {
		return getPageOfGroup(tioConfig, group, pageIndex, pageSize, null);
	}

	/**
	 * 
	 * @param tioConfig
	 * @param group
	 * @param pageIndex
	 * @param pageSize
	 * @param converter
	 * @return
	 */
	public static <T> Page<T> getPageOfGroup(TioConfig tioConfig, String group, Integer pageIndex, Integer pageSize, Converter<T> converter) {
		SetWithLock<ChannelContext> setWithLock = Tio.getChannelContextsByGroup(tioConfig, group);
		return PageUtils.fromSetWithLock(setWithLock, pageIndex, pageSize, converter);
	}

	/**
	 * 群组有多少个连接
	 * @param tioConfig
	 * @param group
	 * @return
	 */
	public static int groupCount(TioConfig tioConfig, String group) {
		SetWithLock<ChannelContext> setWithLock = tioConfig.groups.clients(tioConfig, group);
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
	 * @param tioConfig
	 * @param bsId
	 * @param packet
	 * @author tanyaowu
	 */
	public static void notifyClusterForBsId(TioConfig tioConfig, String bsId, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setBsId(bsId);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把群组消息通知到集群中的其它机器
	 * @param tioConfig
	 * @param group
	 * @param packet
	 */
	public static void notifyClusterForGroup(TioConfig tioConfig, String group, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setGroup(group);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把channelContextId消息通知到集群中的其它机器
	 * @param tioConfig
	 * @param channelContextId
	 * @param packet
	 */
	public static void notifyClusterForId(TioConfig tioConfig, String channelContextId, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setChannelId(channelContextId);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把IP消息通知到集群中的其它机器
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 */
	public static void notifyClusterForIp(TioConfig tioConfig, String ip, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setIp(ip);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把token消息通知到集群中的其它机器
	 * @param tioConfig
	 * @param token
	 * @param packet
	 */
	public static void notifyClusterForToken(TioConfig tioConfig, String token, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setToken(token);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 在集群环境下，把userid消息通知到集群中的其它机器
	 * @param tioConfig
	 * @param userid
	 * @param packet
	 */
	public static void notifyClusterForUser(TioConfig tioConfig, String userid, Packet packet) {
		TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();
		TioClusterVo tioClusterVo = new TioClusterVo(packet);
		tioClusterVo.setUserid(userid);
		tioClusterConfig.publish(tioClusterVo);
	}

	/**
	 * 
	 * @param channelContext
	 * @param remark
	 */
	public static void remove(ChannelContext channelContext, String remark) {
		remove(channelContext, remark, null);
	}

	/**
	 * 和close方法对应，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param remark
	 * @param closeCode
	 */
	public static void remove(ChannelContext channelContext, String remark, CloseCode closeCode) {
		remove(channelContext, null, remark, closeCode);
	}

	/**
	 * 和close方法对应，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 */
	public static void remove(ChannelContext channelContext, Throwable throwable, String remark) {
		remove(channelContext, throwable, remark, (CloseCode) null);
	}

	/**
	 * 和close方法对应，只不过不再进行重连等维护性的操作
	 * @param channelContext
	 * @param throwable
	 * @param remark
	 * @param closeCode
	 */
	public static void remove(ChannelContext channelContext, Throwable throwable, String remark, CloseCode closeCode) {
		close(channelContext, throwable, remark, true, closeCode);
	}

	/**
	 * 和close方法对应，只不过不再进行重连等维护性的操作
	 * @param tioConfig
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 */
	public static void remove(TioConfig tioConfig, String clientIp, Integer clientPort, Throwable throwable, String remark) {
		remove(tioConfig, clientIp, clientPort, throwable, remark, (CloseCode) null);
	}

	/**
	 * 删除clientip和clientPort为指定值的连接
	 * @param tioConfig
	 * @param clientIp
	 * @param clientPort
	 * @param throwable
	 * @param remark
	 * @param closeCode
	 */
	public static void remove(TioConfig tioConfig, String clientIp, Integer clientPort, Throwable throwable, String remark, CloseCode closeCode) {
		ChannelContext channelContext = tioConfig.clientNodes.find(clientIp, clientPort);
		remove(channelContext, throwable, remark, closeCode);
	}

	/**
	 * 删除clientip为指定值的所有连接
	 * @param serverTioConfig
	 * @param ip
	 * @param remark
	 */
	public static void remove(ServerTioConfig serverTioConfig, String ip, String remark) {
		remove(serverTioConfig, ip, remark, (CloseCode) null);
	}

	/**
	 *  删除clientip为指定值的所有连接
	 * @param serverTioConfig
	 * @param ip
	 * @param remark
	 * @param closeCode
	 */
	public static void remove(ServerTioConfig serverTioConfig, String ip, String remark, CloseCode closeCode) {
		SetWithLock<ChannelContext> setWithLock = serverTioConfig.ips.clients(serverTioConfig, ip);
		if (setWithLock == null) {
			return;
		}

		setWithLock.handle(new ReadLockHandler<Set<ChannelContext>>() {
			@Override
			public void handler(Set<ChannelContext> set) {
				for (ChannelContext channelContext : set) {
					Tio.remove(channelContext, remark, closeCode);
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

			if (channelContext.tioConfig.useQueueSend) {
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
			if (channelContext.tioConfig.useQueueSend) {
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
	 * @param tioConfig
	 * @param ip
	 * @param port
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean send(TioConfig tioConfig, String ip, int port, Packet packet) {
		return send(tioConfig, ip, port, packet, false);
	}

	/**
	 * 发送到指定的ip和port
	 * @param tioConfig
	 * @param ip
	 * @param port
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean send(TioConfig tioConfig, String ip, int port, Packet packet, boolean isBlock) {
		ChannelContext channelContext = tioConfig.clientNodes.find(ip, port);
		if (channelContext != null) {
			if (isBlock) {
				return bSend(channelContext, packet);
			} else {
				return send(channelContext, packet);
			}
		} else {
			log.info("{}, can find channelContext by {}:{}", tioConfig.getName(), ip, port);
			return false;
		}
	}

	public static void sendToAll(TioConfig tioConfig, Packet packet) {
		sendToAll(tioConfig, packet, null);
	}

	/**
	 * 发消息到所有连接
	 * @param tioConfig
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToAll(TioConfig tioConfig, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToAll(tioConfig, packet, channelContextFilter, false);
	}

	/**
	 *
	 * @param tioConfig
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToAll(TioConfig tioConfig, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = tioConfig.connections;
			if (setWithLock == null) {
				log.debug("{}, 没有任何连接", tioConfig.getName());
				return false;
			}
			Boolean ret = sendToSet(tioConfig, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

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
	 * @param tioConfig
	 * @param bsId
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean sendToBsId(TioConfig tioConfig, String bsId, Packet packet) {
		return sendToBsId(tioConfig, bsId, packet, false);
	}

	/**
	 * 发消息给指定业务ID
	 * @param tioConfig
	 * @param bsId
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean sendToBsId(TioConfig tioConfig, String bsId, Packet packet, boolean isBlock) {
		ChannelContext channelContext = Tio.getChannelContextByBsId(tioConfig, bsId);
		if (channelContext == null) {
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4bsId()) {
					notifyClusterForBsId(tioConfig, bsId, packet);
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
	 * @param tioConfig
	 * @param group
	 * @param packet
	 * @author tanyaowu
	 */
	public static void sendToGroup(TioConfig tioConfig, String group, Packet packet) {
		sendToGroup(tioConfig, group, packet, null);
	}

	/**
	 * 发消息到组
	 * @param tioConfig
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToGroup(TioConfig tioConfig, String group, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToGroup(tioConfig, group, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到组
	 * @param tioConfig
	 * @param group
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @return
	 */
	private static Boolean sendToGroup(TioConfig tioConfig, String group, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = tioConfig.groups.clients(tioConfig, group);
			if (setWithLock == null) {
				log.debug("{}, 组[{}]不存在", tioConfig.getName(), group);
				return false;
			}
			Boolean ret = sendToSet(tioConfig, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4group()) {
					notifyClusterForGroup(tioConfig, group, packet);
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
	public static Boolean sendToId(TioConfig tioConfig, String channelContextId, Packet packet) {
		return sendToId(tioConfig, channelContextId, packet, false);
	}

	/**
	 * 发消息给指定ChannelContext id
	 * @param channelContextId
	 * @param packet
	 * @param isBlock
	 * @return
	 * @author tanyaowu
	 */
	private static Boolean sendToId(TioConfig tioConfig, String channelContextId, Packet packet, boolean isBlock) {
		ChannelContext channelContext = Tio.getChannelContextById(tioConfig, channelContextId);
		if (channelContext == null) {
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4channelId()) {
					notifyClusterForId(tioConfig, channelContextId, packet);
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
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 * @author: tanyaowu
	 */
	public static void sendToIp(TioConfig tioConfig, String ip, Packet packet) {
		sendToIp(tioConfig, ip, packet, null);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @author: tanyaowu
	 */
	public static void sendToIp(TioConfig tioConfig, String ip, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToIp(tioConfig, ip, packet, channelContextFilter, false);
	}

	/**
	 * 发送到指定ip对应的集合
	 * @param tioConfig
	 * @param ip
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @return
	 * @author: tanyaowu
	 */
	private static Boolean sendToIp(TioConfig tioConfig, String ip, Packet packet, ChannelContextFilter channelContextFilter, boolean isBlock) {
		try {
			SetWithLock<ChannelContext> setWithLock = tioConfig.ips.clients(tioConfig, ip);
			if (setWithLock == null) {
				log.info("{}, 没有ip为[{}]的对端", tioConfig.getName(), ip);
				return false;
			}
			Boolean ret = sendToSet(tioConfig, setWithLock, packet, channelContextFilter, isBlock);
			return ret;
		} finally {
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4ip()) {
					notifyClusterForIp(tioConfig, ip, packet);
				}
			}
		}
	}

	/**
	 * 发消息到指定集合
	 * @param tioConfig
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @author tanyaowu
	 */
	public static void sendToSet(TioConfig tioConfig, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter) {
		sendToSet(tioConfig, setWithLock, packet, channelContextFilter, false);
	}

	/**
	 * 发消息到指定集合
	 * @param tioConfig
	 * @param setWithLock
	 * @param packet
	 * @param channelContextFilter
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToSet(TioConfig tioConfig, SetWithLock<ChannelContext> setWithLock, Packet packet, ChannelContextFilter channelContextFilter,
	        boolean isBlock) {
		boolean releasedLock = false;
		Lock lock = setWithLock.readLock();
		lock.lock();
		try {
			Set<ChannelContext> set = setWithLock.getObj();
			if (set.size() == 0) {
				log.debug("{}, 集合为空", tioConfig.getName());
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
					timeout = Math.max(timeout, 10);//timeout < 10 ? 10 : timeout;
					boolean awaitFlag = countDownLatch.await(timeout, TimeUnit.SECONDS);
					if (!awaitFlag) {
						log.error("{}, 同步群发超时, size:{}, timeout:{}, packet:{}", tioConfig.getName(), setWithLock.getObj().size(), timeout, packet.logstr());
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
	 * @param tioConfig
	 * @param token
	 * @param packet
	 * @return
	 * @author tanyaowu
	 */
	public static Boolean sendToToken(TioConfig tioConfig, String token, Packet packet) {
		return sendToToken(tioConfig, token, packet, false);
	}

	/**
	 * 发消息给指定token
	 * @param tioConfig
	 * @param token
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToToken(TioConfig tioConfig, String token, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = tioConfig.tokens.find(tioConfig, token);
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
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4user()) {
					notifyClusterForToken(tioConfig, token, packet);
				}
			}
		}
	}

	/**
	 * 发消息给指定用户
	 * @param tioConfig
	 * @param userid
	 * @param packet
	 * @author tanyaowu
	 */
	public static Boolean sendToUser(TioConfig tioConfig, String userid, Packet packet) {
		return sendToUser(tioConfig, userid, packet, false);
	}

	/**
	 * 发消息给指定用户
	 * @param tioConfig
	 * @param userid
	 * @param packet
	 * @param isBlock
	 * @author tanyaowu
	 */
	private static Boolean sendToUser(TioConfig tioConfig, String userid, Packet packet, boolean isBlock) {
		SetWithLock<ChannelContext> setWithLock = tioConfig.users.find(tioConfig, userid);
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
			if (tioConfig.isCluster() && !packet.isFromCluster()) {
				TioClusterConfig tioClusterConfig = tioConfig.getTioClusterConfig();

				if (tioClusterConfig.isCluster4user()) {
					notifyClusterForUser(tioConfig, userid, packet);
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
	 *参考：tioConfig.getAioHandler().handler(packet, channelContext);<br>
	 *
	 * @param channelContext
	 * @param packet 业务层必须设置好synSeq字段的值，而且要保证唯一（不能重复）。可以在tioConfig范围内用AtomicInteger
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

		MapWithLock<Integer, Packet> waitingResps = channelContext.tioConfig.getWaitingResps();
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
				log.error("{}, 同步发送超时, {}", channelContext.tioConfig.getName(), channelContext);
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
		channelContext.tioConfig.bsIds.unbind(channelContext);
	}

	/**
	 * 与所有组解除解绑关系
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(ChannelContext channelContext) {
		channelContext.tioConfig.groups.unbind(channelContext);
	}

	/**
	 * 与指定组解除绑定关系
	 * @param group
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindGroup(String group, ChannelContext channelContext) {
		channelContext.tioConfig.groups.unbind(group, channelContext);
	}

	/**
	 * 解除channelContext绑定的token
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindToken(ChannelContext channelContext) {
		channelContext.tioConfig.tokens.unbind(channelContext);
	}

	/**
	 * 解除token
	 * @param tioConfig
	 * @param token
	 */
	public static void unbindToken(TioConfig tioConfig, String token) {
		tioConfig.tokens.unbind(tioConfig, token);
	}

	//	org.tio.core.TioConfig.ipBlacklist

	/**
	 * 解除channelContext绑定的userid
	 * @param channelContext
	 * @author tanyaowu
	 */
	public static void unbindUser(ChannelContext channelContext) {
		channelContext.tioConfig.users.unbind(channelContext);
	}

	/**
	 * 解除userid的绑定。一般用于多地登录，踢掉前面登录的场景
	 * @param tioConfig
	 * @param userid
	 * @author: tanyaowu
	 */
	public static void unbindUser(TioConfig tioConfig, String userid) {
		tioConfig.users.unbind(tioConfig, userid);
	}

	private Tio() {
	}

}
