package org.tio.server;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.GroupContext;
import org.tio.core.Tio;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.maintain.IpBlacklist;
import org.tio.core.ssl.SslConfig;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.json.Json;
import org.tio.utils.lock.SetWithLock;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

/**
 * 
 * @author tanyaowu 
 * 2016年10月10日 下午5:51:56
 */
public class ServerGroupContext extends GroupContext {
	static Logger log = LoggerFactory.getLogger(ServerGroupContext.class);

	private AcceptCompletionHandler acceptCompletionHandler = null;

	private ServerAioHandler serverAioHandler = null;

	private ServerAioListener serverAioListener = null;

	/** The accept executor. */
	//private ThreadPoolExecutor acceptExecutor = null;

	private Thread checkHeartbeatThread = null;

	/**
	 * 
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @author: tanyaowu
	 */
	public ServerGroupContext(ServerAioHandler serverAioHandler, ServerAioListener serverAioListener) {
		this(null, serverAioHandler, serverAioListener);
	}

	/**
	 * 
	 * @param name
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @author: tanyaowu
	 */
	public ServerGroupContext(String name, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener) {
		this(name, serverAioHandler, serverAioListener, null, null);
	}

	//	/**
	//	 * 
	//	 * @param name
	//	 * @param serverAioHandler
	//	 * @param serverAioListener
	//	 * @author: tanyaowu
	//	 */
	//	public ServerGroupContext(String name, TioClusterConfig tioClusterConfig, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener) {
	//		this(name, tioClusterConfig, serverAioHandler, serverAioListener, null, null);
	//	}

	/**
	 * 
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	public ServerGroupContext(ServerAioHandler serverAioHandler, ServerAioListener serverAioListener, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this(null, serverAioHandler, serverAioListener, tioExecutor, groupExecutor);
	}

	/**
	 * 
	 * @param name
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	public ServerGroupContext(String name, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener, SynThreadPoolExecutor tioExecutor,
	        ThreadPoolExecutor groupExecutor) {
		super(tioExecutor, groupExecutor);
		this.ipBlacklist = new IpBlacklist(id, this);
		init(name, serverAioHandler, serverAioListener, tioExecutor, groupExecutor);
	}

	/**
	 * 
	 * @param name
	 * @param tioClusterConfig
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author: tanyaowu
	 */
	//	public ServerGroupContext(String name, TioClusterConfig tioClusterConfig, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener,
	//			SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
	//		super(tioClusterConfig, tioExecutor, groupExecutor);
	//		init(name, serverAioHandler, serverAioListener, tioExecutor, groupExecutor);
	//	}

	/**
	 * 
	 * @param name
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param tioExecutor
	 * @param groupExecutor
	 * @author tanyaowu
	 */
	private void init(String name, ServerAioHandler serverAioHandler, ServerAioListener serverAioListener, SynThreadPoolExecutor tioExecutor, ThreadPoolExecutor groupExecutor) {
		this.name = name;
		this.groupStat = new ServerGroupStat();
		this.acceptCompletionHandler = new AcceptCompletionHandler();
		this.serverAioHandler = serverAioHandler;
		this.serverAioListener = serverAioListener;// == null ? new DefaultServerAioListener() : serverAioListener;
		checkHeartbeatThread = new Thread(new Runnable() {
			@Override
			public void run() {
				//第一次先休息一下
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e1) {
					log.error(e1.toString(), e1);
				}

				while (!isStopped()) {
					//					long sleeptime = heartbeatTimeout;
					if (heartbeatTimeout <= 0) {
						log.info("{}, 用户取消了框架层面的心跳检测，如果业务需要，请用户自己去完成心跳检测", ServerGroupContext.this.name);
						break;
					}
					try {
						Thread.sleep(heartbeatTimeout);
					} catch (InterruptedException e1) {
						log.error(e1.toString(), e1);
					}
					long start = SystemTimer.currTime;
					SetWithLock<ChannelContext> setWithLock = ServerGroupContext.this.connections;
					Set<ChannelContext> set = null;
					long start1 = 0;
					int count = 0;
					ReadLock readLock = setWithLock.readLock();
					readLock.lock();
					try {
						start1 = SystemTimer.currTime;
						set = setWithLock.getObj();

						for (ChannelContext channelContext : set) {
							count++;
							long compareTime = Math.max(channelContext.stat.latestTimeOfReceivedByte, channelContext.stat.latestTimeOfSentPacket);
							long currtime = SystemTimer.currTime;
							long interval = currtime - compareTime;

							boolean needRemove = false;
							if (channelContext.heartbeatTimeout != null && channelContext.heartbeatTimeout > 0) {
								needRemove = interval > channelContext.heartbeatTimeout;
							} else {
								needRemove = interval > heartbeatTimeout;
							}

							if (needRemove) {
								log.info("{}, {} ms没有收发消息", channelContext, interval);
								Tio.remove(channelContext, interval + " ms没有收发消息");
							}
						}
					} catch (Throwable e) {
						log.error("", e);
					} finally {
						try {
							readLock.unlock();
							if (debug) {
								StringBuilder builder = new StringBuilder();
								builder.append("\r\n").append(ServerGroupContext.this.getName());
								builder.append("\r\n ├ 当前时间:").append(SystemTimer.currTime);
								builder.append("\r\n ├ 连接统计");
								builder.append("\r\n │ \t ├ 共接受过连接数  :").append(((ServerGroupStat) groupStat).accepted.get());
								builder.append("\r\n │ \t ├ 当前连接数            :").append(set.size());
								//								builder.append("\r\n │ \t ├ 当前群组数            :").append(groups);
								builder.append("\r\n │ \t ├ 异IP连接数           :").append(ServerGroupContext.this.ips.getIpmap().getObj().size());
								builder.append("\r\n │ \t └ 关闭过的连接数  :").append(groupStat.closed.get());

								builder.append("\r\n ├ 消息统计");
								builder.append("\r\n │ \t ├ 已处理消息  :").append(groupStat.handledPackets.get());
								builder.append("\r\n │ \t ├ 已接收消息(packet/byte):").append(groupStat.receivedPackets.get()).append("/").append(groupStat.receivedBytes.get());
								builder.append("\r\n │ \t ├ 已发送消息(packet/byte):").append(groupStat.sentPackets.get()).append("/").append(groupStat.sentBytes.get()).append("b");
								builder.append("\r\n │ \t ├ 平均每次TCP包接收的字节数  :").append(groupStat.getBytesPerTcpReceive());
								builder.append("\r\n │ \t └ 平均每次TCP包接收的业务包  :").append(groupStat.getPacketsPerTcpReceive());
								builder.append("\r\n └ IP统计时段 ");
								if (ServerGroupContext.this.ipStats.durationList != null && ServerGroupContext.this.ipStats.durationList.size() > 0) {
									builder.append("\r\n   \t └ ").append(Json.toJson(ServerGroupContext.this.ipStats.durationList));
								} else {
									builder.append("\r\n   \t └ ").append("没有设置ip统计时间");
								}

								builder.append("\r\n ├ 节点统计");
								builder.append("\r\n │ \t ├ clientNodes :").append(ServerGroupContext.this.clientNodes.getObjWithLock().getObj().size());
								builder.append("\r\n │ \t ├ 所有连接               :").append(ServerGroupContext.this.connections.getObj().size());
								builder.append("\r\n │ \t ├ 绑定user数         :").append(ServerGroupContext.this.users.getMap().getObj().size());
								builder.append("\r\n │ \t ├ 绑定token数       :").append(ServerGroupContext.this.tokens.getMap().getObj().size());
								builder.append("\r\n │ \t └ 等待同步消息响应 :").append(ServerGroupContext.this.waitingResps.getObj().size());

								builder.append("\r\n ├ 群组");
								builder.append("\r\n │ \t └ groupmap:").append(ServerGroupContext.this.groups.getGroupmap().getObj().size());
								builder.append("\r\n └ 拉黑IP ");
								builder.append("\r\n   \t └ ").append(Json.toJson(ServerGroupContext.this.ipBlacklist.getAll()));

								log.warn(builder.toString());

								long end = SystemTimer.currTime;
								long iv1 = start1 - start;
								long iv = end - start1;
								log.warn("{}, 检查心跳, 共{}个连接, 取锁耗时{}ms, 循环耗时{}ms, 心跳超时时间:{}ms", ServerGroupContext.this.name, count, iv1, iv, heartbeatTimeout);
							}
						} catch (Throwable e) {
							log.error("", e);
						}
					}
				}
			}
		}, "tio-timer-checkheartbeat-" + id);
		checkHeartbeatThread.setDaemon(true);
		checkHeartbeatThread.setPriority(Thread.MIN_PRIORITY);
		checkHeartbeatThread.start();

	}

	/**
	 * 
	 * @param keyStoreFile 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @param trustStoreFile 如果是以"classpath:"开头，则从classpath中查找，否则视为普通的文件路径
	 * @param keyStorePwd 
	 * @throws FileNotFoundException
	 */
	public void useSsl(String keyStoreFile, String trustStoreFile, String keyStorePwd) throws Exception {
		if (StrUtil.isNotBlank(keyStoreFile) && StrUtil.isNotBlank(trustStoreFile)) {
			SslConfig sslConfig = SslConfig.forServer(keyStoreFile, trustStoreFile, keyStorePwd);
			this.setSslConfig(sslConfig);
		}
	}

	/**
	 * 
	 * @param keyStoreInputStream
	 * @param trustStoreInputStream
	 * @param passwd
	 * @throws Exception
	 * @author tanyaowu
	 */
	public void useSsl(InputStream keyStoreInputStream, InputStream trustStoreInputStream, String passwd) throws Exception {
		SslConfig sslConfig = SslConfig.forServer(keyStoreInputStream, trustStoreInputStream, passwd);
		this.setSslConfig(sslConfig);
	}

	/**
	 * @return the acceptCompletionHandler
	 */
	public AcceptCompletionHandler getAcceptCompletionHandler() {
		return acceptCompletionHandler;
	}

	/**
	 * @see org.tio.core.GroupContext#getAioHandler()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:34:37
	 *
	 */
	@Override
	public AioHandler getAioHandler() {
		return this.getServerAioHandler();
	}

	/**
	 * @see org.tio.core.GroupContext#getAioListener()
	 *
	 * @return
	 * @author tanyaowu
	 * 2016年12月20日 上午11:34:37
	 *
	 */
	@Override
	public AioListener getAioListener() {
		return getServerAioListener();
	}

	/**
	 * @return the serverAioHandler
	 */
	public ServerAioHandler getServerAioHandler() {
		return serverAioHandler;
	}

	/**
	 * @return the serverAioListener
	 */
	public ServerAioListener getServerAioListener() {
		return serverAioListener;
	}

	public void setServerAioListener(ServerAioListener serverAioListener) {
		this.serverAioListener = serverAioListener;
	}

	/** 
	 * @return
	 * @author tanyaowu
	 */
	@Override
	public boolean isServer() {
		return true;
	}

	@Override
	public String toString() {
		return "ServerGroupContext [name=" + name + "]";
	}

}
