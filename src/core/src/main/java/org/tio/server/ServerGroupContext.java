package org.tio.server;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelStat;
import org.tio.core.GroupContext;
import org.tio.core.ObjWithLock;
import org.tio.core.intf.AioHandler;
import org.tio.core.intf.AioListener;
import org.tio.core.intf.Packet;
import org.tio.core.stat.GroupStat;
import org.tio.core.utils.SystemTimer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;

/**
 * The Class ServerGroupContext.
 *
 * @author tanyaowu
 */
public class ServerGroupContext<SessionContext, P extends Packet, R> extends GroupContext<SessionContext, P, R> {
	static Logger log = LoggerFactory.getLogger(ServerGroupContext.class);

	private AcceptCompletionHandler<SessionContext, P, R> acceptCompletionHandler = null;

	private ServerAioHandler<SessionContext, P, R> serverAioHandler = null;

	private ServerAioListener<SessionContext, P, R> serverAioListener = null;

	protected ServerGroupStat serverGroupStat = new ServerGroupStat();

	/** The accept executor. */
	//private ThreadPoolExecutor acceptExecutor = null;

	private Thread checkHeartbeatThread = null;

	/**
	 * 
	 * @param serverAioHandler
	 * @param serverAioListener
	 * @param groupExecutor
	 *
	 * @author: tanyaowu
	 * 2017年2月2日 下午1:40:11
	 *
	 */
	public ServerGroupContext(ServerAioHandler<SessionContext, P, R> serverAioHandler, ServerAioListener<SessionContext, P, R> serverAioListener) {
		super();
		this.acceptCompletionHandler = new AcceptCompletionHandler<>();
		this.serverAioHandler = serverAioHandler;
		this.serverAioListener = serverAioListener == null ? new DefaultServerAioListener<SessionContext, P, R>() : serverAioListener;

		checkHeartbeatThread = new Thread(new Runnable() {
			@SuppressWarnings("unused")
			@Override
			public void run() {

				while (!isStopped()) {
					//					long sleeptime = heartbeatTimeout;
					if (heartbeatTimeout <= 0) {
						log.warn("用户取消了框架层面的心跳检测，请用户自己去完成心跳机制");
						break;
					}
					long start = SystemTimer.currentTimeMillis();
					ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> objWithLock = ServerGroupContext.this.connections.getSetWithLock();
					Set<ChannelContext<SessionContext, P, R>> set = null;
					ReadLock readLock = objWithLock.getLock().readLock();
					long start1 = 0;
					int count = 0;
					try {
						readLock.lock();
						start1 = SystemTimer.currentTimeMillis();
						set = objWithLock.getObj();

						for (ChannelContext<SessionContext, P, R> entry : set) {
							count++;
							ChannelContext<SessionContext, P, R> channelContext = entry;
							ChannelStat stat = channelContext.getStat();
							long timeLatestReceivedMsg = stat.getLatestTimeOfReceivedPacket();
							long timeLatestSentMsg = stat.getLatestTimeOfSentPacket();
							long compareTime = Math.max(timeLatestReceivedMsg, timeLatestSentMsg);
							long currtime = SystemTimer.currentTimeMillis();
							long interval = (currtime - compareTime);
							if (interval > heartbeatTimeout) {
								log.warn("{}, {} ms没有收发消息", channelContext, interval);
								Aio.remove(channelContext, interval + " ms没有收发消息");
							}
						}
					} catch (Throwable e) {
						log.error("", e);
					} finally {
						try {
							readLock.unlock();

							//							if (log.isWarnEnabled())
							//							{
							//								int groups = 0;
							//								ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> objwithlock = ServerGroupContext.this.getGroups().clients("g");
							//								if (objwithlock != null)
							//								{
							//									groups = objwithlock.getObj().size();
							//								}
							//								
							//								log.warn("[{}]:[{}]: 当前连接个数:{}, 群组(g):{}, 共接受连接:{}, 一共关闭过的连接个数:{}, 已接收消息:({}p)({}b), 已处理消息:{}p, 已发送消息:({}p)({}b)", SystemTimer.currentTimeMillis(), id,
							//										set.size(), groups, serverGroupStat.getAccepted().get(), serverGroupStat.getClosed().get(), serverGroupStat.getReceivedPacket().get(),
							//										serverGroupStat.getReceivedBytes().get(), serverGroupStat.getHandledPacket().get(), serverGroupStat.getSentPacket().get(),
							//										serverGroupStat.getSentBytes().get());
							//							}
							//							
							//							//打印各集合信息
							//							if (log.isWarnEnabled())
							//							{							
							//								log.warn("clientNodes:{},connections:{},connecteds:{},closeds:{},groups:[channelmap:{}, groupmap:{}],users:{},syns:{}", 
							//										ServerGroupContext.this.clientNodes.getMap().getObj().size(),
							//										ServerGroupContext.this.connections.getSetWithLock().getObj().size(),
							//										ServerGroupContext.this.connecteds.getSetWithLock().getObj().size(),
							//										ServerGroupContext.this.closeds.getSetWithLock().getObj().size(),
							//										ServerGroupContext.this.groups.getChannelmap().getObj().size(), ServerGroupContext.this.groups.getGroupmap().getObj().size(),
							//										ServerGroupContext.this.users.getMap().getObj().size(),
							//										ServerGroupContext.this.waitingResps.getMap().getObj().size()
							//										);
							//							}
							//							
							//							if (log.isInfoEnabled())
							//							{
							//								long end = SystemTimer.currentTimeMillis();
							//								long iv1 = start1 - start;
							//								long iv = end - start1;
							//								log.info("检查心跳, 共{}个连接, 取锁耗时{}ms, 循环耗时{}ms, 心跳超时时间:{}ms", count, iv1, iv, heartbeatTimeout);
							//							}
							Thread.sleep(heartbeatTimeout);
						} catch (Exception e) {
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

	public ServerGroupStat getServerGroupStat() {
		return serverGroupStat;
	}

	/**
	 * @return the acceptCompletionHandler
	 */
	public AcceptCompletionHandler<SessionContext, P, R> getAcceptCompletionHandler() {
		return acceptCompletionHandler;
	}

	/**
	 * @return the serverAioHandler
	 */
	public ServerAioHandler<SessionContext, P, R> getServerAioHandler() {
		return serverAioHandler;
	}

	/**
	 * @return the serverAioListener
	 */
	public ServerAioListener<SessionContext, P, R> getServerAioListener() {
		return serverAioListener;
	}

	/** 
	 * @see org.tio.core.GroupContext#getAioHandler()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:34:37
	 * 
	 */
	@Override
	public AioHandler<SessionContext, P, R> getAioHandler() {
		return this.getServerAioHandler();
	}

	/** 
	 * @see org.tio.core.GroupContext#getGroupStat()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:34:37
	 * 
	 */
	@Override
	public GroupStat getGroupStat() {
		return this.getServerGroupStat();
	}

	/** 
	 * @see org.tio.core.GroupContext#getAioListener()
	 * 
	 * @return
	 * @author: tanyaowu
	 * 2016年12月20日 上午11:34:37
	 * 
	 */
	@Override
	public AioListener<SessionContext, P, R> getAioListener() {
		return getServerAioListener();
	}
}
