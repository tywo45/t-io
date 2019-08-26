package org.tio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.core.ssl.SslFacadeContext;
import org.tio.core.stat.ChannelStat;
import org.tio.utils.SystemTimer;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.lock.SetWithLock;

/**
 *
 * @author tanyaowu
 * 2017年4月1日 上午9:29:58
 */
public class TioClient {
	private static Logger log = LoggerFactory.getLogger(TioClient.class);

	private AsynchronousChannelGroup channelGroup;

	private ClientTioConfig clientTioConfig;

	/**
	 * @param serverIp 可以为空
	 * @param serverPort
	 * @param aioDecoder
	 * @param aioEncoder
	 * @param aioHandler
	 *
	 * @author tanyaowu
	 * @throws IOException
	 *
	 */
	public TioClient(final ClientTioConfig clientTioConfig) throws IOException {
		super();
		this.clientTioConfig = clientTioConfig;
		this.channelGroup = AsynchronousChannelGroup.withThreadPool(clientTioConfig.groupExecutor);

		startHeartbeatTask();
		startReconnTask();
	}

	/**
	 *
	 * @param serverNode
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode) throws Exception {
		asynConnect(serverNode, null);
	}

	/**
	 *
	 * @param serverNode
	 * @param timeout
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, Integer timeout) throws Exception {
		asynConnect(serverNode, null, null, timeout);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param timeout
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
		connect(serverNode, bindIp, bindPort, null, timeout, false);
	}

	/**
	 *
	 * @param serverNode
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public ClientChannelContext connect(Node serverNode) throws Exception {
		return connect(serverNode, null);
	}

	/**
	 *
	 * @param serverNode
	 * @param timeout
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public ClientChannelContext connect(Node serverNode, Integer timeout) throws Exception {
		return connect(serverNode, null, 0, timeout);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param initClientChannelContext
	 * @param timeout 超时时间，单位秒
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout) throws Exception {
		return connect(serverNode, bindIp, bindPort, initClientChannelContext, timeout, true);
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param initClientChannelContext
	 * @param timeout 超时时间，单位秒
	 * @param isSyn true: 同步, false: 异步
	 * @return
	 * @throws Exception
	 * @author tanyaowu
	 */
	private ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, ClientChannelContext initClientChannelContext, Integer timeout, boolean isSyn)
	        throws Exception {

		AsynchronousSocketChannel asynchronousSocketChannel = null;
		ClientChannelContext channelContext = null;
		boolean isReconnect = initClientChannelContext != null;
		//		ClientAioListener clientAioListener = clientTioConfig.getClientAioListener();

		long start = SystemTimer.currTime;
		asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
		long end = SystemTimer.currTime;
		long iv = end - start;
		if (iv >= 100) {
			log.error("{}, open 耗时:{} ms", channelContext, iv);
		}

		asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		InetSocketAddress bind = null;
		if (bindPort != null && bindPort > 0) {
			if (false == StrUtil.isBlank(bindIp)) {
				bind = new InetSocketAddress(bindIp, bindPort);
			} else {
				bind = new InetSocketAddress(bindPort);
			}
		}

		if (bind != null) {
			asynchronousSocketChannel.bind(bind);
		}

		channelContext = initClientChannelContext;

		start = SystemTimer.currTime;

		InetSocketAddress inetSocketAddress = new InetSocketAddress(serverNode.getIp(), serverNode.getPort());

		ConnectionCompletionVo attachment = new ConnectionCompletionVo(channelContext, this, isReconnect, asynchronousSocketChannel, serverNode, bindIp, bindPort);

		if (isSyn) {
			Integer realTimeout = timeout;
			if (realTimeout == null) {
				realTimeout = 5;
			}

			CountDownLatch countDownLatch = new CountDownLatch(1);
			attachment.setCountDownLatch(countDownLatch);
			asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientTioConfig.getConnectionCompletionHandler());
			boolean f = countDownLatch.await(realTimeout, TimeUnit.SECONDS);
			if (f) {
				return attachment.getChannelContext();
			} else {
				log.error("countDownLatch.await(realTimeout, TimeUnit.SECONDS) 返回false ");
				return attachment.getChannelContext();
			}
		} else {
			asynchronousSocketChannel.connect(inetSocketAddress, attachment, clientTioConfig.getConnectionCompletionHandler());
			return null;
		}
	}

	/**
	 *
	 * @param serverNode
	 * @param bindIp
	 * @param bindPort
	 * @param timeout 超时时间，单位秒
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public ClientChannelContext connect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception {
		return connect(serverNode, bindIp, bindPort, null, timeout);
	}

	/**
	 * @return the channelGroup
	 */
	public AsynchronousChannelGroup getChannelGroup() {
		return channelGroup;
	}

	/**
	 * @return the clientTioConfig
	 */
	public ClientTioConfig getClientTioConfig() {
		return clientTioConfig;
	}

	/**
	 *
	 * @param channelContext
	 * @param timeout 单位秒
	 * @return
	 * @throws Exception
	 *
	 * @author tanyaowu
	 *
	 */
	public void reconnect(ClientChannelContext channelContext, Integer timeout) throws Exception {
		connect(channelContext.getServerNode(), channelContext.getBindIp(), channelContext.getBindPort(), channelContext, timeout);
	}

	/**
	 * @param clientTioConfig the clientTioConfig to set
	 */
	public void setClientTioConfig(ClientTioConfig clientTioConfig) {
		this.clientTioConfig = clientTioConfig;
	}

	/**
	 * 定时任务：发心跳
	 * @author tanyaowu
	 *
	 */
	private void startHeartbeatTask() {
		final ClientGroupStat clientGroupStat = (ClientGroupStat) clientTioConfig.groupStat;
		final ClientAioHandler aioHandler = clientTioConfig.getClientAioHandler();

		final String id = clientTioConfig.getId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!clientTioConfig.isStopped()) {
					//					final long heartbeatTimeout = clientTioConfig.heartbeatTimeout;
					if (clientTioConfig.heartbeatTimeout <= 0) {
						log.warn("用户取消了框架层面的心跳定时发送功能，请用户自己去完成心跳机制");
						break;
					}
					SetWithLock<ChannelContext> setWithLock = clientTioConfig.connecteds;
					ReadLock readLock = setWithLock.readLock();
					readLock.lock();
					try {
						Set<ChannelContext> set = setWithLock.getObj();
						long currtime = SystemTimer.currTime;
						for (ChannelContext entry : set) {
							ClientChannelContext channelContext = (ClientChannelContext) entry;
							if (channelContext.isClosed || channelContext.isRemoved) {
								continue;
							}

							ChannelStat stat = channelContext.stat;
							long compareTime = Math.max(stat.latestTimeOfReceivedByte, stat.latestTimeOfSentPacket);
							long interval = currtime - compareTime;
							if (interval >= clientTioConfig.heartbeatTimeout / 2) {
								Packet packet = aioHandler.heartbeatPacket(channelContext);
								if (packet != null) {
									if (log.isInfoEnabled()) {
										log.info("{}发送心跳包", channelContext.toString());
									}
									Tio.send(channelContext, packet);
								}
							}
						}
						if (log.isInfoEnabled()) {
							log.info("[{}]: curr:{}, closed:{}, received:({}p)({}b), handled:{}, sent:({}p)({}b)", id, set.size(), clientGroupStat.closed.get(),
							        clientGroupStat.receivedPackets.get(), clientGroupStat.receivedBytes.get(), clientGroupStat.handledPackets.get(),
							        clientGroupStat.sentPackets.get(), clientGroupStat.sentBytes.get());
						}

					} catch (Throwable e) {
						log.error("", e);
					} finally {
						try {
							readLock.unlock();
							Thread.sleep(clientTioConfig.heartbeatTimeout / 4);
						} catch (Throwable e) {
							log.error(e.toString(), e);
						} finally {

						}
					}
				}
			}
		}, "tio-timer-heartbeat" + id).start();
	}

	/**
	 * 启动重连任务
	 *
	 * @author tanyaowu
	 *
	 */
	private void startReconnTask() {
		final ReconnConf reconnConf = clientTioConfig.getReconnConf();
		if (reconnConf == null || reconnConf.getInterval() <= 0) {
			return;
		}

		final String id = clientTioConfig.getId();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!clientTioConfig.isStopped()) {
					log.error("closeds:{}, connections:{}", clientTioConfig.closeds.size(), clientTioConfig.connections.size());
					//log.info("准备重连");
					LinkedBlockingQueue<ChannelContext> queue = reconnConf.getQueue();
					ClientChannelContext channelContext = null;
					try {
						channelContext = (ClientChannelContext) queue.take();
					} catch (InterruptedException e1) {
						log.error(e1.toString(), e1);
					}
					if (channelContext == null) {
						continue;
						//						return;
					}

					if (channelContext.isRemoved) //已经删除的，不需要重新再连
					{
						continue;
					}

					SslFacadeContext sslFacadeContext = channelContext.sslFacadeContext;
					if (sslFacadeContext != null) {
						sslFacadeContext.setHandshakeCompleted(false);
					}

					long sleeptime = reconnConf.getInterval() - (SystemTimer.currTime - channelContext.stat.timeInReconnQueue);
					//log.info("sleeptime:{}, closetime:{}", sleeptime, timeInReconnQueue);
					if (sleeptime > 0) {
						try {
							Thread.sleep(sleeptime);
						} catch (InterruptedException e) {
							log.error(e.toString(), e);
						}
					}

					if (channelContext.isRemoved || !channelContext.isClosed) //已经删除的和已经连上的，不需要重新再连
					{
						continue;
					} else {
						ReconnRunnable runnable = channelContext.getReconnRunnable();
						if (runnable == null) {
							synchronized (channelContext) {
								runnable = channelContext.getReconnRunnable();
								if (runnable == null) {
									runnable = new ReconnRunnable(channelContext, TioClient.this, reconnConf.getThreadPoolExecutor());
									channelContext.setReconnRunnable(runnable);
								}
							}
						}
						runnable.execute();
						//						reconnConf.getThreadPoolExecutor().execute(runnable);
					}
				}
			}
		});
		thread.setName("tio-timer-reconnect-" + id);
		thread.setDaemon(true);
		thread.start();

	}

	/**
	 * 
	 * @return
	 * @author tanyaowu
	 */
	public boolean stop() {
		boolean ret = true;
		try {
			clientTioConfig.groupExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}
		try {
			clientTioConfig.tioExecutor.shutdown();
		} catch (Exception e1) {
			log.error(e1.toString(), e1);
		}

		clientTioConfig.setStopped(true);
		try {
			ret = ret && clientTioConfig.groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
			ret = ret && clientTioConfig.tioExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		log.info("client resource has released");
		return ret;
	}
}
