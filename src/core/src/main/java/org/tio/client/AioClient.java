package org.tio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.Aio;
import org.tio.core.ChannelContext;
import org.tio.core.ChannelStat;
import org.tio.core.Node;
import org.tio.core.ObjWithLock;
import org.tio.core.intf.Packet;
import org.tio.core.threadpool.SynThreadPoolExecutor;
import org.tio.core.threadpool.intf.SynRunnableIntf;
import org.tio.core.utils.SystemTimer;

/**
 * 
 * @author tanyaowu 
 * 2017年4月1日 上午9:29:58
 */
public class AioClient<SessionContext, P extends Packet, R>
{
	private static Logger log = LoggerFactory.getLogger(AioClient.class);

	private AsynchronousChannelGroup channelGroup;

	private ClientGroupContext<SessionContext, P, R> clientGroupContext;

	/**
	 * @param serverIp 可以为空
	 * @param serverPort 
	 * @param aioDecoder
	 * @param aioEncoder
	 * @param aioHandler
	 *
	 * @author: tanyaowu
	 * @throws IOException 
	 * 
	 */
	public AioClient(final ClientGroupContext<SessionContext, P, R> clientGroupContext) throws IOException
	{
		super();
		this.clientGroupContext = clientGroupContext;
		ExecutorService groupExecutor = clientGroupContext.getGroupExecutor();
		this.channelGroup = AsynchronousChannelGroup.withThreadPool(groupExecutor);

		startHeartbeatTask();
		startReconnTask();
	}
	
	/**
	 * 
	 * @param serverNode
	 * @return
	 * @throws Exception
	 *
	 * @author: tanyaowu
	 *
	 */
	public ClientChannelContext<SessionContext, P, R> connect(Node serverNode) throws Exception
	{
		return connect(serverNode, null);
	}

	/**
	 * 
	 * @param serverNode
	 * @param timeout
	 * @return
	 * @throws Exception
	 * @author: tanyaowu
	 */
	public ClientChannelContext<SessionContext, P, R> connect(Node serverNode, Integer timeout) throws Exception
	{
		return connect(serverNode, null, 0, timeout);
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
	 * @author: tanyaowu
	 *
	 */
	public ClientChannelContext<SessionContext, P, R> connect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception
	{
		return connect(serverNode, bindIp, bindPort, null, timeout);
	}
	
	/**
	 * 
	 * @param serverNode
	 * @throws Exception
	 *
	 * @author: tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode) throws Exception
	{
		asynConnect(serverNode, null);
	}
	
	/**
	 * 
	 * @param serverNode
	 * @param timeout
	 * @throws Exception
	 *
	 * @author: tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, Integer timeout) throws Exception
	{
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
	 * @author: tanyaowu
	 *
	 */
	public void asynConnect(Node serverNode, String bindIp, Integer bindPort, Integer timeout) throws Exception
	{
		connect(serverNode, bindIp, bindPort, null, timeout, false);
	}

	/**
	 * 
	 * @return
	 *
	 * @author: tanyaowu
	 *
	 */
	public boolean stop()
	{
		//		isWaitingStop = true;
		boolean ret = true;
		ExecutorService groupExecutor = clientGroupContext.getGroupExecutor();
		SynThreadPoolExecutor<SynRunnableIntf> executor = clientGroupContext.getHandlerExecutorNormPrior();
		ThreadPoolExecutor closePoolExecutor = clientGroupContext.getClosePoolExecutor();
		groupExecutor.shutdown();
		executor.shutdown();
		closePoolExecutor.shutdown();
		clientGroupContext.setStopped(true);
		try
		{
			ret = ret && groupExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e)
		{
			log.error(e.getLocalizedMessage(), e);
		}

		try
		{
			ret = ret && executor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e)
		{
			log.error(e.getLocalizedMessage(), e);
		}

		try
		{
			ret = ret && closePoolExecutor.awaitTermination(6000, TimeUnit.SECONDS);
		} catch (InterruptedException e)
		{
			log.error(e.getLocalizedMessage(), e);
		}

		String logstr = "client resource has released";
		System.out.println(logstr);
		log.info(logstr);
		return ret;

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
	 *
	 * @author: tanyaowu
	 *
	 */
	private ClientChannelContext<SessionContext, P, R> connect(Node serverNode, String bindIp, Integer bindPort,
			ClientChannelContext<SessionContext, P, R> initClientChannelContext, Integer timeout) throws Exception
	{
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
	 *
	 * @author: tanyaowu
	 *
	 */
	private ClientChannelContext<SessionContext, P, R> connect(Node serverNode, String bindIp, Integer bindPort,
			ClientChannelContext<SessionContext, P, R> initClientChannelContext, Integer timeout, boolean isSyn) throws Exception
	{
//		Integer _timeout = timeout;
//		if (_timeout == null)
//		{
//			_timeout = DEFAULT_TIMEOUT;
//		}

		AsynchronousSocketChannel asynchronousSocketChannel = null;
		ClientChannelContext<SessionContext, P, R> channelContext = null;
		boolean isReconnect = initClientChannelContext != null;
//		ClientAioListener<SessionContext, P, R> clientAioListener = clientGroupContext.getClientAioListener();
		
		
		long start = SystemTimer.currentTimeMillis();
		asynchronousSocketChannel = AsynchronousSocketChannel.open(channelGroup);
		long end = SystemTimer.currentTimeMillis();
		long iv = end - start;
		if (iv >= 100)
		{
			log.error("{}, open 耗时:{} ms", channelContext, iv);
		}

		asynchronousSocketChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
		asynchronousSocketChannel.setOption(StandardSocketOptions.SO_KEEPALIVE, true);

		InetSocketAddress bind = null;
		if (bindPort != null && bindPort > 0)
		{
			if (StringUtils.isNotBlank(bindIp))
			{
				bind = new InetSocketAddress(bindIp, bindPort);
			} else
			{
				bind = new InetSocketAddress(bindPort);
			}
		}

		if (bind != null)
		{
			asynchronousSocketChannel.bind(bind);
		}

		channelContext = initClientChannelContext;

		start = SystemTimer.currentTimeMillis();

		InetSocketAddress inetSocketAddress = new InetSocketAddress(serverNode.getIp(), serverNode.getPort());
		
		ConnectionCompletionVo<SessionContext, P, R> connectionCompletionVo = new ConnectionCompletionVo<>(channelContext, this, isReconnect,
				asynchronousSocketChannel, serverNode, bindIp, bindPort);
		
		if (isSyn)
		{
			synchronized (connectionCompletionVo)
			{
				asynchronousSocketChannel.connect(inetSocketAddress, connectionCompletionVo, clientGroupContext.getConnectionCompletionHandler());
				connectionCompletionVo.wait();
			}
		} else
		{
			return null;
		}
		
		return connectionCompletionVo.getChannelContext();
	}

	/**
	 * @return the channelGroup
	 */
	public AsynchronousChannelGroup getChannelGroup()
	{
		return channelGroup;
	}

	/**
	 * @return the clientGroupContext
	 */
	public ClientGroupContext<SessionContext, P, R> getClientGroupContext()
	{
		return clientGroupContext;
	}

	/**
	 * 
	 * @param channelContext
	 * @param timeout
	 * @return
	 * @throws Exception
	 *
	 * @author: tanyaowu
	 *
	 */
	public void reconnect(ClientChannelContext<SessionContext, P, R> channelContext, Integer timeout) throws Exception
	{
		connect(channelContext.getServerNode(), channelContext.getBindIp(), channelContext.getBindPort(), channelContext, timeout);
	}

	/**
	 * @param clientGroupContext the clientGroupContext to set
	 */
	public void setClientGroupContext(ClientGroupContext<SessionContext, P, R> clientGroupContext)
	{
		this.clientGroupContext = clientGroupContext;
	}

	/**
	 * 定时任务：发心跳，重连(待实现)
	 * @author: tanyaowu
	 *
	 */
	private void startHeartbeatTask()
	{
		final ClientGroupStat clientGroupStat = clientGroupContext.getClientGroupStat();
		final ClientAioHandler<SessionContext, P, R> aioHandler = clientGroupContext.getClientAioHandler();
		final long heartbeatTimeout = clientGroupContext.getHeartbeatTimeout();
		final String id = clientGroupContext.getId();
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (!clientGroupContext.isStopped())
				{
					ReadLock readLock = null;
					try
					{
						ObjWithLock<Set<ChannelContext<SessionContext, P, R>>> objWithLock = clientGroupContext.getConnecteds().getSetWithLock();
						readLock = objWithLock.getLock().readLock();
						readLock.lock();
						Set<ChannelContext<SessionContext, P, R>> set = objWithLock.getObj();
						long currtime = SystemTimer.currentTimeMillis();
						for (ChannelContext<SessionContext, P, R> entry : set)
						{
							ClientChannelContext<SessionContext, P, R> channelContext = (ClientChannelContext<SessionContext, P, R>) entry;
							if (channelContext.isClosed() || channelContext.isRemoved())
							{
								continue;
							}

							ChannelStat stat = channelContext.getStat();
							long timeLatestReceivedMsg = stat.getLatestTimeOfReceivedPacket();
							long timeLatestSentMsg = stat.getLatestTimeOfSentPacket();
							long compareTime = Math.max(timeLatestReceivedMsg, timeLatestSentMsg);
							long interval = (currtime - compareTime);
							if (interval >= heartbeatTimeout / 2)
							{
								P packet = aioHandler.heartbeatPacket();
								if (packet != null)
								{
									log.info("{}发送心跳包", channelContext.toString());
									Aio.send(channelContext, packet);
								}
							}
						}
						if (log.isInfoEnabled())
						{
							log.info("[{}]: curr:{}, closed:{}, received:({}p)({}b), handled:{}, sent:({}p)({}b)", id, set.size(), clientGroupStat.getClosed().get(),
									clientGroupStat.getReceivedPacket().get(), clientGroupStat.getReceivedBytes().get(), clientGroupStat.getHandledPacket().get(),
									clientGroupStat.getSentPacket().get(), clientGroupStat.getSentBytes().get());
						}

					} catch (Throwable e)
					{
						log.error("", e);
					} finally
					{
						try
						{
							if (readLock != null)
							{
								readLock.unlock();
							}
							Thread.sleep(heartbeatTimeout / 4);
						} catch (Exception e)
						{
							log.error(e.toString(), e);
						} finally
						{

						}
					}
				}
			}
		}, "t-aio-timer-heartbeat" + id).start();
	}

	private static class ReconnRunnable<SessionContext, P extends Packet, R> implements Runnable
	{
		ClientChannelContext<SessionContext, P, R> channelContext = null;
		AioClient<SessionContext, P, R> aioClient = null;
		
//		private static Map<Node, Long> map = new HashMap<>(); 

		public ReconnRunnable(ClientChannelContext<SessionContext, P, R> channelContext, AioClient<SessionContext, P, R> aioClient)
		{
			this.channelContext = channelContext;
			this.aioClient = aioClient;
		}

		/** 
		 * @see java.lang.Runnable#run()
		 *  
		 * @author: tanyaowu
		 * 2017年2月2日 下午8:24:40
		 * 
		 */
		@Override
		public void run()
		{
			ReentrantReadWriteLock closeLock = channelContext.getCloseLock();
			WriteLock writeLock = closeLock.writeLock();

			try
			{
				writeLock.lock();
				if (!channelContext.isClosed()) //已经连上了，不需要再重连了
				{
					return;
				}
				long start = SystemTimer.currentTimeMillis();
				aioClient.reconnect(channelContext, 2);
				long end = SystemTimer.currentTimeMillis();
				long iv = end - start;
				if (iv >= 100)
				{
					log.error("{},重连耗时:{} ms", channelContext, iv);
				} else
				{
					log.info("{},重连耗时:{} ms", channelContext, iv);
				}

				if (channelContext.isClosed())
				{
					channelContext.setReconnCount(channelContext.getReconnCount() + 1);
//					map.put(channelContext.getServerNode(), SystemTimer.currentTimeMillis());
					return;
				}
			} catch (java.lang.Throwable e)
			{
				log.error(e.toString(), e);
			} finally
			{
				writeLock.unlock();
			}

		}
	}

	/**
	 * 启动重连任务
	 * 
	 *
	 * @author: tanyaowu
	 *
	 */
	private void startReconnTask()
	{
		final ReconnConf<SessionContext, P, R> reconnConf = clientGroupContext.getReconnConf();
		if (reconnConf == null || reconnConf.getInterval() <= 0)
		{
			return;
		}

		final String id = clientGroupContext.getId();
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (!clientGroupContext.isStopped())
				{
					log.info("准备重连");
					LinkedBlockingQueue<ChannelContext<SessionContext, P, R>> queue = reconnConf.getQueue();
					ClientChannelContext<SessionContext, P, R> channelContext = null;
					try
					{
						channelContext = (ClientChannelContext<SessionContext, P, R>) queue.take();
					} catch (InterruptedException e1)
					{
						log.error(e1.toString(), e1);
					}
					if (channelContext == null)
					{
						continue;
						//						return;
					}

					if (channelContext.isRemoved()) //已经删除的，不需要重新再连
					{
						continue;
					}

					long currtime = SystemTimer.currentTimeMillis();
					long timeInReconnQueue = channelContext.getStat().getTimeInReconnQueue();
					long sleeptime = reconnConf.getInterval() - (currtime - timeInReconnQueue);
					log.info("sleeptime:{}, closetime:{}", sleeptime, timeInReconnQueue);
					if (sleeptime > 0)
					{
						try
						{
							Thread.sleep(sleeptime);
						} catch (InterruptedException e)
						{
							log.error(e.toString(), e);
						}
					}

					if (channelContext.isRemoved() || !channelContext.isClosed()) //已经删除的和已经连上的，不需要重新再连
					{
						continue;
					}
					ReconnRunnable<SessionContext, P, R> runnable = new ReconnRunnable<SessionContext, P, R>(channelContext, AioClient.this);
					reconnConf.getThreadPoolExecutor().execute(runnable);
				}
			}
		});
		thread.setName("t-aio-timer-reconnect" + id);
		thread.setDaemon(true);
		thread.start();

	}
}
